package io.swagger.client.model


case class CurrencyValueRequest(
  creator_pubkey: String, // Public key of creator&#39;s account
  signature: String, // The signature, which can be verified with pubkey
  target: String, // Public key of URL-encoded target&#39;s account
  timestamp: String, // Transaction timestamp
  value: Double // currency&#39;s value
)
