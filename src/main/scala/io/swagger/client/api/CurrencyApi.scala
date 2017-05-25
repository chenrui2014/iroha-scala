package io.swagger.client.api

import com.wordnik.swagger.client._
import io.swagger.client.model._

import scala.collection.mutable
import scala.concurrent.Future

class CurrencyApi(client: TransportClient, config: SwaggerConfig) extends ApiClient(client, config) {

  def currencyAdd(payload: CreateCurrencyRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[CreateCurrencyRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/currency"))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling CurrencyApi->currencyAdd")

    val resFuture = client.submit("POST", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def currencyAddValue(currencyUri: String,
    payload: CurrencyValueRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[CurrencyValueRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/currency/{currency_uri}/add")
      replaceAll("\\{" + "currency_uri" + "\\}", currencyUri.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (currencyUri == null) throw new Exception("Missing required parameter 'currencyUri' when calling CurrencyApi->currencyAddValue")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling CurrencyApi->currencyAddValue")

    val resFuture = client.submit("POST", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def currencyDelete(currencyUri: String,
    payload: DeleteCurrencyRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[DeleteCurrencyRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/currency/{currency_uri}")
      replaceAll("\\{" + "currency_uri" + "\\}", currencyUri.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (currencyUri == null) throw new Exception("Missing required parameter 'currencyUri' when calling CurrencyApi->currencyDelete")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling CurrencyApi->currencyDelete")

    val resFuture = client.submit("DELETE", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def currencyGetAll(creatorPubkey: String,
    currencyUri: String,
    target: String,
    isCommitted: Option[Boolean] = None
  )(implicit reader: ClientResponseReader[Currency]): Future[Currency] = {
    // create path and map variables
    val path = (addFmt("/currency/{currency_uri}")
      replaceAll("\\{" + "currency_uri" + "\\}", currencyUri.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (creatorPubkey == null) throw new Exception("Missing required parameter 'creatorPubkey' when calling CurrencyApi->currencyGetAll")

    if (currencyUri == null) throw new Exception("Missing required parameter 'currencyUri' when calling CurrencyApi->currencyGetAll")

    if (target == null) throw new Exception("Missing required parameter 'target' when calling CurrencyApi->currencyGetAll")

    queryParams += "creator_pubkey" -> creatorPubkey.toString
    isCommitted match {
      case Some(param) => queryParams += "is_committed" -> param.toString
      case _ => queryParams
    }
    queryParams += "target" -> target.toString

    val resFuture = client.submit("GET", path, queryParams.toMap, headerParams.toMap, "")
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def currencySubtractValue(currencyUri: String,
    payload: CurrencyValueRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[CurrencyValueRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/currency/{currency_uri}/subtract")
      replaceAll("\\{" + "currency_uri" + "\\}", currencyUri.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (currencyUri == null) throw new Exception("Missing required parameter 'currencyUri' when calling CurrencyApi->currencySubtractValue")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling CurrencyApi->currencySubtractValue")

    val resFuture = client.submit("POST", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def currencyTransfer(currencyUri: String,
    payload: CurrencyTransferRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[CurrencyTransferRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/currency/{currency_uri}/transfer")
      replaceAll("\\{" + "currency_uri" + "\\}", currencyUri.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (currencyUri == null) throw new Exception("Missing required parameter 'currencyUri' when calling CurrencyApi->currencyTransfer")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling CurrencyApi->currencyTransfer")

    val resFuture = client.submit("POST", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }

  def currencyUpdate(currencyUri: String,
    payload: UpdateCurrencyRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[UpdateCurrencyRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/currency/{currency_uri}")
      replaceAll("\\{" + "currency_uri" + "\\}", currencyUri.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (currencyUri == null) throw new Exception("Missing required parameter 'currencyUri' when calling CurrencyApi->currencyUpdate")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling CurrencyApi->currencyUpdate")

    val resFuture = client.submit("PUT", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }


}
