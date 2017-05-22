package io.swagger.client.api

import io.swagger.client.model.Transactions
import com.wordnik.swagger.client._
import scala.concurrent.Future
import collection.mutable

class TransactionsApi(client: TransportClient, config: SwaggerConfig) extends ApiClient(client, config) {

  def transactionsGetAll(creatorPubkey: String,
      currencyUri: String,
      target: String,
      isCommitted: Option[Boolean] = None
      )(implicit reader: ClientResponseReader[Transactions]): Future[Transactions] = {
    // create path and map variables
    val path = (addFmt("/transactions/{currency_uri}")
        replaceAll ("\\{" + "currency_uri" + "\\}",currencyUri.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (creatorPubkey == null) throw new Exception("Missing required parameter 'creatorPubkey' when calling TransactionsApi->transactionsGetAll")

    if (currencyUri == null) throw new Exception("Missing required parameter 'currencyUri' when calling TransactionsApi->transactionsGetAll")

    if (target == null) throw new Exception("Missing required parameter 'target' when calling TransactionsApi->transactionsGetAll")

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


}
