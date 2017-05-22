package io.swagger.client.model

case class CreateCurrencyRequest (
  creator_pubkey: String,  // Public key of creator's account
  currency: Currency,
  signature: String,  // The signature, which can be verified with pubkey
  target: String,  // Public key of URL-encoded target's account
  timestamp: String  // Transaction timestamp
)
