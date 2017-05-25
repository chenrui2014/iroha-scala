package io.swagger.client.api

import com.wordnik.swagger.client._
import io.swagger.client.model.{DeleteSignatoryRequest, Message, Signatories, SignatoryRequest}

import scala.collection.mutable
import scala.concurrent.Future

class SignatoriesApi(client: TransportClient, config: SwaggerConfig) extends ApiClient(client, config) {

  def signatoriesAdd(uuid: String,
    payload: SignatoryRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[SignatoryRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/accounts/{uuid}/signatories")
      replaceAll("\\{" + "uuid" + "\\}", uuid.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (uuid == null) throw new Exception("Missing required parameter 'uuid' when calling SignatoriesApi->signatoriesAdd")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling SignatoriesApi->signatoriesAdd")

    val resFuture = client.submit("POST", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def signatoriesDelete(sig: String,
    uuid: String,
    payload: DeleteSignatoryRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[DeleteSignatoryRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/accounts/{uuid}/signatories/{sig}")
      replaceAll("\\{" + "sig" + "\\}", sig.toString)
      replaceAll("\\{" + "uuid" + "\\}", uuid.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (sig == null) throw new Exception("Missing required parameter 'sig' when calling SignatoriesApi->signatoriesDelete")

    if (uuid == null) throw new Exception("Missing required parameter 'uuid' when calling SignatoriesApi->signatoriesDelete")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling SignatoriesApi->signatoriesDelete")

    val resFuture = client.submit("DELETE", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def signatoriesGetAll(uuid: String,
    creatorPubkey: Option[String] = None,
    isCommitted: Option[Boolean] = None
  )(implicit reader: ClientResponseReader[Signatories]): Future[Signatories] = {
    // create path and map variables
    val path = (addFmt("/accounts/{uuid}/signatories")
      replaceAll("\\{" + "uuid" + "\\}", uuid.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (uuid == null) throw new Exception("Missing required parameter 'uuid' when calling SignatoriesApi->signatoriesGetAll")

    creatorPubkey match {
      case Some(param) => queryParams += "creator_pubkey" -> param.toString
      case _ => queryParams
    }
    isCommitted match {
      case Some(param) => queryParams += "is_committed" -> param.toString
      case _ => queryParams
    }

    val resFuture = client.submit("GET", path, queryParams.toMap, headerParams.toMap, "")
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }


}
