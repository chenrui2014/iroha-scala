package io.swagger.client.model

case class TransactionRequest (
  pubkey: String,  // ed25519 public key, which should be used to validate the signature
  signature: String,  // The signature, which can be verified with pubkey
  timestamp: String  // Transaction timestamp
)
