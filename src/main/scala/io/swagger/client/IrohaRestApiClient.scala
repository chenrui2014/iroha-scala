package io.swagger.client

import io.swagger.client.api._

import com.wordnik.swagger.client._

import java.io.Closeable

class IrohaRestApiClient(config: SwaggerConfig) extends Closeable {
  val locator: ServiceLocator = config.locator
  val name: String = config.name

  private[this] val client = transportClient

  protected def transportClient: TransportClient = new RestClient(config)
  
  val account = new AccountApi(client, config)
  
  val currency = new CurrencyApi(client, config)
  
  val quorum = new QuorumApi(client, config)
  
  val signatories = new SignatoriesApi(client, config)
  
  val transactions = new TransactionsApi(client, config)
  

  def close() {
    client.close()
  }
}
