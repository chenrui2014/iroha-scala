package io.swagger.client

import java.io.Closeable

import com.wordnik.swagger.client._
import io.swagger.client.api._
import org.hyperledger.iroha.client.RestfulApiClient

class IrohaRestApiClient(config: SwaggerConfig) extends Closeable {
  private[this] val client = new RestfulApiClient(config)

  val locator: ServiceLocator = config.locator
  val name: String = config.name
  val account = new AccountApi(client, config)
  val currency = new CurrencyApi(client, config)
  val quorum = new QuorumApi(client, config)
  val signatories = new SignatoriesApi(client, config)
  val transactions = new TransactionsApi(client, config)

  def close() {
    client.close()
  }
}

object IrohaHeaders {
  val acceptVndAccount: Map[String, String] = Map("Accept" -> "application/vnd.account+json")
  val acceptVndAccounts: Map[String, String] = Map("Accept" -> "application/vnd.accounts+json")
  val acceptVndCurrency: Map[String, String] = Map("Accept" -> "application/vnd.currency+json")
  val acceptVndMessage: Map[String, String] = Map("Accept" -> "application/vnd.message+json")
  val acceptVndSignatories: Map[String, String] = Map("Accept" -> "application/vnd.signatories+json")
  val acceptVndTransactions: Map[String, String] = Map("Accept" -> "application/vnd.transactions+json")
  val contentTypeJson: Map[String, String] = Map("Content-Type" -> "application/json")
}
