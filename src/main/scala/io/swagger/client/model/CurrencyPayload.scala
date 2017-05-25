package io.swagger.client.model


case class CurrencyPayload(
  description: Option[String], // currency&#39;s description
  domain_name: String, // domain name to which currency belongs
  ledger_name: String, // ledger name to which currency belongs
  name: String, // currency name
  value: Double // currency&#39;s value
)
