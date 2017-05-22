package io.swagger.client.model

case class Currency (
  description: Option[String],  // currency's description
  domain_name: String,  // domain name to which currency belongs
  ledger_name: String,  // ledger name to which currency belongs
  name: String,  // currency name
  value: Double  // currency's value
)
