package io.swagger.client.model


case class UpdateCurrencyRequest(
  creator_pubkey: String, // Public key of creator&#39;s account
  description: String, // currency&#39;s description
  signature: String, // The signature, which can be verified with pubkey
  timestamp: String // Transaction timestamp
)
