package io.swagger.client.model

case class UpdateCurrencyRequest (
  creator_pubkey: String,  // Public key of creator's account
  description: String,  // currency's description
  signature: String,  // The signature, which can be verified with pubkey
  timestamp: String  // Transaction timestamp
)
