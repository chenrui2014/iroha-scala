package io.swagger.client.api

import com.wordnik.swagger.client._
import io.swagger.client.model.{Message, UpdateQuorumRequest}

import scala.collection.mutable
import scala.concurrent.Future

class QuorumApi(client: TransportClient, config: SwaggerConfig) extends ApiClient(client, config) {

  def quorumUpdate(uuid: String,
    payload: UpdateQuorumRequest)(implicit reader: ClientResponseReader[Message], writer: RequestWriter[UpdateQuorumRequest]): Future[Message] = {
    // create path and map variables
    val path = (addFmt("/accounts/{uuid}/quorum")
      replaceAll("\\{" + "uuid" + "\\}", uuid.toString))

    // query params
    val queryParams = new mutable.HashMap[String, String]
    val headerParams = new mutable.HashMap[String, String]

    if (uuid == null) throw new Exception("Missing required parameter 'uuid' when calling QuorumApi->quorumUpdate")

    if (payload == null) throw new Exception("Missing required parameter 'payload' when calling QuorumApi->quorumUpdate")

    val resFuture = client.submit("PUT", path, queryParams.toMap, headerParams.toMap, writer.write(payload))
    resFuture flatMap { resp =>
      process(reader.read(resp))
    }
  }


}
