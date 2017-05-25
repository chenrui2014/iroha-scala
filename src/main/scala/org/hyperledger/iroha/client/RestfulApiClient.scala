package org.hyperledger.iroha.client

import java.io.File
import java.net.URI
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.{Date, Locale, TimeZone, concurrent => juc}

import com.ning.http.client._
import com.ning.http.client.cookie.{Cookie => AhcCookie}
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig
import com.wordnik.swagger.client.RestClient.{CookieJar, Defaults, RestClientResponse}
import com.wordnik.swagger.client._
import com.wordnik.swagger.client.async.BuildInfo
import org.jboss.netty.channel.socket.nio.{NioClientSocketChannelFactory, NioWorkerPool}
import org.jboss.netty.util.{HashedWheelTimer, Timer}
import rl.Imports._
import rl.{MapQueryString, UrlCodingUtils}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.io.Codec
import scala.util.Failure


object RestfulApiClient {

  private val threadIds = new AtomicLong()

  private lazy val factory = new juc.ThreadFactory {
    def newThread(runnable: Runnable): Thread = {
      val thread = new Thread(runnable)
      thread.setName("swagger-client-thread-" + threadIds.incrementAndGet())
      thread.setDaemon(true)
      thread
    }
  }

  val DefaultUserAgent = s"Reverb SwaggerClient / ${BuildInfo.version}"

  private implicit def stringWithExt(s: String) = new {
    def isBlank = s == null || s.trim.isEmpty
    def nonBlank = !isBlank
    def blankOption = if (isBlank) None else Option(s)
  }

  case class CookieOptions( domain  : String  = "",
    path    : String  = "",
    maxAge  : Int     = -1,
    secure  : Boolean = false,
    comment : String  = "",
    httpOnly: Boolean = false,
    version : Int = 0,
    encoding: String  = "UTF-8")

  trait HttpCookie {
    implicit def cookieOptions: CookieOptions
    def name: String
    def value: String

  }

  case class RequestCookie(name: String, value: String, cookieOptions: CookieOptions = CookieOptions()) extends HttpCookie

  object DateUtil {
    @volatile private[this] var _currentTimeMillis: Option[Long] = None
    def currentTimeMillis = _currentTimeMillis getOrElse System.currentTimeMillis
    def currentTimeMillis_=(ct: Long) = _currentTimeMillis = Some(ct)
    def freezeTime() = _currentTimeMillis = Some(System.currentTimeMillis())
    def unfreezeTime() = _currentTimeMillis = None
    def formatDate(date: Date, format: String, timeZone: TimeZone = TimeZone.getTimeZone("GMT")) = {
      val df = new SimpleDateFormat(format)
      df.setTimeZone(timeZone)
      df.format(date)
    }
  }

  private object InternalDefaults {
    /** true if we think we're runing un-forked in an sbt-interactive session */
    val inTrapExit = (
      for (group ← Option(Thread.currentThread.getThreadGroup))
        yield group.getName == "trap.exit").getOrElse(false)

    /** Sets a user agent, no timeout for requests  */
    object BasicDefaults extends Defaults {
      lazy val timer = new HashedWheelTimer()
      def builder = (new AsyncHttpClientConfig.Builder()
        setAllowPoolingConnection true
        setRequestTimeoutInMs 45000
        setCompressionEnabled true
        setFollowRedirects false
        setMaximumConnectionsPerHost 200
        setUserAgent DefaultUserAgent
        setMaxRequestRetry 0)
    }

    /** Uses daemon threads and tries to exit cleanly when running in sbt  */
    object SbtProcessDefaults extends Defaults {
      def builder = {
        val shuttingDown = new juc.atomic.AtomicBoolean(false)
        /** daemon threads that also shut down everything when interrupted! */
        lazy val interruptThreadFactory = new juc.ThreadFactory {
          def newThread(runnable: Runnable) = {
            new Thread(runnable) {
              setDaemon(true)
              setName("bragi-client-thread-" + threadIds.incrementAndGet())
              override def interrupt() {
                shutdown()
                super.interrupt()
              }
            }
          }
        }
        lazy val nioClientSocketChannelFactory = {
          val workerCount = 2 * Runtime.getRuntime().availableProcessors()
          new NioClientSocketChannelFactory(
            juc.Executors.newCachedThreadPool(interruptThreadFactory),
            1,
            new NioWorkerPool(
              juc.Executors.newCachedThreadPool(interruptThreadFactory),
              workerCount),
            timer)
        }

        def shutdown() {
          if (shuttingDown.compareAndSet(false, true)) {
            nioClientSocketChannelFactory.releaseExternalResources()
          }
        }

        BasicDefaults.builder.setAsyncHttpClientProviderConfig(
          new NettyAsyncHttpProviderConfig().addProperty(
            NettyAsyncHttpProviderConfig.SOCKET_CHANNEL_FACTORY,
            nioClientSocketChannelFactory))
      }
      lazy val timer = new HashedWheelTimer(factory)
    }
  }
}

class RestfulApiClient(config: SwaggerConfig) extends TransportClient with Logging {
  import org.hyperledger.iroha.client.RestfulApiClient._
  protected def underlying: Defaults = {
    if (InternalDefaults.inTrapExit) InternalDefaults.SbtProcessDefaults
    else InternalDefaults.BasicDefaults
  }
  protected val locator: ServiceLocator = config.locator
  protected val clientConfig: AsyncHttpClientConfig = (underlying.builder
    setUserAgent config.userAgent
    setRequestTimeoutInMs config.idleTimeout.toMillis.toInt
    setConnectionTimeoutInMs config.connectTimeout.toMillis.toInt
    setCompressionEnabled config.enableCompression               // enable content-compression
    setAllowPoolingConnection true                               // enable http keep-alive
    setFollowRedirects config.followRedirects).build()

  import com.wordnik.swagger.client.StringHttpMethod._
  implicit val execContext = ExecutionContext.fromExecutorService(clientConfig.executorService())

  private[this] val mimes = new Mimes with Logging {
    protected def warn(message: String) = logger.warn(message)
  }

  private[this] val cookies = new CookieJar(Map.empty)

  protected def createClient() = new AsyncHttpClient(clientConfig)

  private[this] val client = createClient()

  private[this] def createRequest(method: String): String ⇒ AsyncHttpClient#BoundRequestBuilder = {
    method.toUpperCase(Locale.ENGLISH) match {
      case `GET`     ⇒ client.prepareGet _
      case `POST`    ⇒ client.preparePost _
      case `PUT`     ⇒ client.preparePut _
      case `DELETE`  ⇒ client.prepareDelete _
      case `HEAD`    ⇒ client.prepareHead _
      case `OPTIONS` ⇒ client.prepareOptions _
      case `CONNECT` ⇒ client.prepareConnect _
    }
  }

  private[this] def addTimeout(timeout: Duration)(req: AsyncHttpClient#BoundRequestBuilder) = {
    if (timeout.isFinite()) {
      val prc = new PerRequestConfig()
      prc.setRequestTimeoutInMs(timeout.toMillis.toInt)
      req.setPerRequestConfig(prc)
    }
    req
  }

  private[this] def addParameters(method: String, params: Iterable[(String, String)], isMultipart: Boolean = false, charset: Charset = Codec.UTF8.charSet)(req: AsyncHttpClient#BoundRequestBuilder) = {
    method.toUpperCase(Locale.ENGLISH) match {
      case `GET` | `DELETE` | `HEAD` | `OPTIONS` ⇒ params foreach { case (k, v) ⇒ req addQueryParameter (k, v) }
      case `PUT` | `POST`   | `PATCH`            ⇒ {
        if (!isMultipart)
          if (req.build().getHeaders.getFirstValue("Content-Type").startsWith("application/x-www-form-urlencoded"))
            params foreach { case (k, v) ⇒ req addParameter (k, v) }
          else
            params foreach { case (k, v) => req addQueryParameter(k, v) }
        else {
          params foreach { case (k, v) => req addBodyPart new StringPart(k, v, charset.name)}
        }
      }
      case _                                     ⇒ // we don't care, carry on
    }
    req
  }

  private[this] def addHeaders(headers: Iterable[(String, String)], files: Iterable[(String, File)])(req: AsyncHttpClient#BoundRequestBuilder) = {
    headers foreach { case (k, v) => req.addHeader(k, v) }
    if (!Map(headers.map(kv => kv._1.toUpperCase -> kv._2).toSeq:_*).contains("CONTENT-TYPE"))
      req.setHeader("Content-Type", defaultWriteContentType(files)("Content-Type"))
    req
  }

  private[this] def addFiles(files: Iterable[(String, File)], isMultipart: Boolean)(req: AsyncHttpClient#BoundRequestBuilder) = {
    if (isMultipart) {
      files foreach { case (nm, file) =>
        req.addBodyPart(new FilePart(nm, file, mimes(file), FileCharset(file).name))
      }
    }
    req
  }

  private[this] def addCookies(req: AsyncHttpClient#BoundRequestBuilder) = {
    cookies foreach { cookie =>
      val ahcCookie = AhcCookie.newValidCookie(
        cookie.name,
        cookie.value,
        cookie.cookieOptions.domain,
        cookie.value,
        cookie.cookieOptions.path,
        -1,
        cookie.cookieOptions.maxAge,
        cookie.cookieOptions.secure,
        cookie.cookieOptions.httpOnly)
      req.addCookie(ahcCookie)
    }
    req
  }

  private[this] def addQuery(u: URI)(req: AsyncHttpClient#BoundRequestBuilder) = {
    u.getQuery.blankOption foreach { uu =>
      rl.QueryString(uu) match {
        case m: MapQueryString => m.value foreach { case (k, v) => v foreach { req.addQueryParameter(k, _) } }
        case _ =>
      }
    }
    req
  }

  private[this] val allowsBody = Vector(PUT, POST, PATCH, DELETE)


  private[this] def addBody(method: String, body: String)(req: AsyncHttpClient#BoundRequestBuilder) = {
    if (allowsBody.contains(method.toUpperCase(Locale.ENGLISH)) && body.nonBlank) req.setBody(body)
    req
  }


  private[this] def requestFiles(params: Iterable[(String, Any)]) = params collect { case (k, v: File) => k -> v }
  private[this] def paramsFrom(params: Iterable[(String, Any)]) = params collect {
    case (k, v: String) => k -> v
    case (k, null) => k -> ""
    case (k, v) => k -> v.toString
  }
  private[this] def isMultipartRequest(method: String, headers: Iterable[(String, String)], files: Iterable[(String, File)]) = {
    allowsBody.contains(method.toUpperCase(Locale.ENGLISH)) && {
      val ct = (defaultWriteContentType(files) ++ headers)("Content-Type")
      ct.toLowerCase(Locale.ENGLISH).startsWith("multipart/form-data")
    }
  }

  private[this] def requestUri(base: URI, u: URI) = if (u.isAbsolute) u else {
    // There is no constructor on java.net.URI that will not encode the path
    // except for the one where you pass in a uri as string so we're concatenating ourselves
    val prt = base.getPort
    val b =
      if (prt > 0 && prt != 80 && prt != 443) "%s://%s:%d".format(base.getScheme, base.getHost, prt)
      else "%s://%s".format(base.getScheme, base.getHost)
    val p = base.getRawPath + u.getRawPath.blankOption.getOrElse("/")
    val q = u.getRawQuery.blankOption.map("?"+_).getOrElse("")
    val f = u.getRawFragment.blankOption.map("#"+_).getOrElse("")
    URI.create(b+p+q+f)
  }

  def submit(method: String, uri: String, params: Iterable[(String, Any)], headers: Iterable[(String, String)], body: String = "", timeout: Duration = 90.seconds): Future[RestClientResponse] = {
    val u = URI.create(if (UrlCodingUtils.needsUrlEncoding(uri)) uri.urlEncode else uri).normalize()
    val files = requestFiles(params)
    val isMultipart = isMultipartRequest(method, headers, files)
    locator.pickOneAsUri(config.name, "") flatMap { opt =>
      if (opt.isEmpty) sys.error("No host could be found for %s".format(config.name))
      else {
        val baseUrl = opt.get
        (createRequest(method)
          andThen addTimeout(timeout)
          andThen addHeaders(headers, files)
          andThen addCookies
          andThen addParameters(method, paramsFrom(params), isMultipart)
          andThen addQuery(u)
          andThen addBody(method, body)
          andThen addFiles(files, isMultipart)
          andThen executeRequest)(requestUri(URI.create(baseUrl).normalize(), u).toASCIIString)
      }
    }
  }

  private[this] def executeRequest(req: AsyncHttpClient#BoundRequestBuilder): Future[RestClientResponse] = {
    logger.debug("Requesting:\n" + req.build())
    val promise = Promise[RestClientResponse]()
    req.execute(new AsyncCompletionHandler[Promise[RestClientResponse]] {
      override def onThrowable(t: Throwable) = promise.complete(Failure(t))
      def onCompleted(response: Response) = {
        logger.debug(s"Got response [${response.getStatusCode} ${response.getStatusText}}] for request to ${req.build().getUrl}.\n$response")
        promise.success(new RestClientResponse(response))
      }
    })
    promise.future
  }

  private[this] def defaultWriteContentType(files: Iterable[(String, File)]) = {
    val value = if (files.nonEmpty) "multipart/form-data" else config.contentType.headerValue
    Map("Content-Type" -> value)
  }

  def close() = client.closeAsynchronously()
}

