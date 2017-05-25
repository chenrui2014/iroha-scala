package io.swagger.client.api

import com.wordnik.swagger.client._
import io.swagger.client.model._

import scala.collection.mutable
import scala.concurrent.Future

class AccountApi(client: TransportClient, config: SwaggerConfig) extends ApiClient(client, config) {

  def accountAdd(payload: AddAccountRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[AddAccountRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/accounts"))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling AccountApi->accountAdd")

    val resFuture = client.submit("POST", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def accountDeleteByUUID(uuid: String,
    payload: DeleteAccountRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[DeleteAccountRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/accounts/{uuid}")
      replaceAll("\\{" + "uuid" + "\\}", uuid.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (uuid == null) throw new Exception("Missing required parameter 'uuid' when calling AccountApi->accountDeleteByUUID")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling AccountApi->accountDeleteByUUID")

    val resFuture = client.submit("DELETE", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def accountDeleteByUsername(domainUri: String,
    username: String,
    payload: DeleteAccountRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[DeleteAccountRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/domains/{domain_uri}/accounts/{username}")
      replaceAll("\\{" + "domain_uri" + "\\}", domainUri.toString)
      replaceAll("\\{" + "username" + "\\}", username.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (domainUri == null) throw new Exception("Missing required parameter 'domainUri' when calling AccountApi->accountDeleteByUsername")

    if (username == null) throw new Exception("Missing required parameter 'username' when calling AccountApi->accountDeleteByUsername")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling AccountApi->accountDeleteByUsername")

    val resFuture = client.submit("DELETE", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def accountDeleteByUsernameFromDefaultDomain(username: String,
    payload: DeleteAccountRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[DeleteAccountRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/domains/default/accounts/{username}")
      replaceAll("\\{" + "username" + "\\}", username.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (username == null) throw new Exception("Missing required parameter 'username' when calling AccountApi->accountDeleteByUsernameFromDefaultDomain")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling AccountApi->accountDeleteByUsernameFromDefaultDomain")

    val resFuture = client.submit("DELETE", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def accountGetAll()(implicit reader: ClientResponseReader[Accounts]): Future[Accounts] = {
    // create path and map variables
    val path = (addFmt("/accounts"))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]


    val resFuture = client.submit("GET", path, queryParams.toMap, headerParams.toMap, "")
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def accountGetByUUID(uuid: String,
    isCommitted: Option[Boolean] = None
  )(implicit reader: ClientResponseReader[Account]): Future[Account] = {
    // create path and map variables
    val path = (addFmt("/accounts/{uuid}")
      replaceAll("\\{" + "uuid" + "\\}", uuid.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (uuid == null) throw new Exception("Missing required parameter 'uuid' when calling AccountApi->accountGetByUUID")

    isCommitted match {
      case Some(param) => queryParams += "is_committed" -> param.toString
      case _ => queryParams
    }

    val resFuture = client.submit("GET", path, queryParams.toMap, headerParams.toMap, "")
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def accountGetByUsername(domainUri: String,
    username: String,
    isCommitted: Option[Boolean] = None
  )(implicit reader: ClientResponseReader[Account]): Future[Account] = {
    // create path and map variables
    val path = (addFmt("/domains/{domain_uri}/accounts/{username}")
      replaceAll("\\{" + "domain_uri" + "\\}", domainUri.toString)
      replaceAll("\\{" + "username" + "\\}", username.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (domainUri == null) throw new Exception("Missing required parameter 'domainUri' when calling AccountApi->accountGetByUsername")

    if (username == null) throw new Exception("Missing required parameter 'username' when calling AccountApi->accountGetByUsername")

    isCommitted match {
      case Some(param) => queryParams += "is_committed" -> param.toString
      case _ => queryParams
    }

    val resFuture = client.submit("GET", path, queryParams.toMap, headerParams.toMap, "")
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def accountGetByUsernameFromDefaultDomain(username: String,
    isCommitted: Option[Boolean] = None
  )(implicit reader: ClientResponseReader[Account]): Future[Account] = {
    // create path and map variables
    val path = (addFmt("/domains/default/accounts/{username}")
      replaceAll("\\{" + "username" + "\\}", username.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (username == null) throw new Exception("Missing required parameter 'username' when calling AccountApi->accountGetByUsernameFromDefaultDomain")

    isCommitted match {
      case Some(param) => queryParams += "is_committed" -> param.toString
      case _ => queryParams
    }

    val resFuture = client.submit("GET", path, queryParams.toMap, headerParams.toMap, "")
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def accountUpdateByUUID(uuid: String,
    payload: UpdateAccountRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[UpdateAccountRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/accounts/{uuid}")
      replaceAll("\\{" + "uuid" + "\\}", uuid.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (uuid == null) throw new Exception("Missing required parameter 'uuid' when calling AccountApi->accountUpdateByUUID")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling AccountApi->accountUpdateByUUID")

    val resFuture = client.submit("PUT", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def accountUpdateByUsername(domainUri: String,
    username: String,
    payload: UpdateAccountRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[UpdateAccountRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/domains/{domain_uri}/accounts/{username}")
      replaceAll("\\{" + "domain_uri" + "\\}", domainUri.toString)
      replaceAll("\\{" + "username" + "\\}", username.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (domainUri == null) throw new Exception("Missing required parameter 'domainUri' when calling AccountApi->accountUpdateByUsername")

    if (username == null) throw new Exception("Missing required parameter 'username' when calling AccountApi->accountUpdateByUsername")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling AccountApi->accountUpdateByUsername")

    val resFuture = client.submit("PUT", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def accountUpdateByUsernameFromDefaultDomain(username: String,
    payload: UpdateAccountRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[UpdateAccountRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/domains/default/accounts/{username}")
      replaceAll("\\{" + "username" + "\\}", username.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (username == null) throw new Exception("Missing required parameter 'username' when calling AccountApi->accountUpdateByUsernameFromDefaultDomain")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling AccountApi->accountUpdateByUsernameFromDefaultDomain")

    val resFuture = client.submit("PUT", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }


}
