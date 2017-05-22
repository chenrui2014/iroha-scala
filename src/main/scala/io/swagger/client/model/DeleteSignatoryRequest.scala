package io.swagger.client.model

case class DeleteSignatoryRequest (
  creator_pubkey: String,  // Public key of creator's account
  signature: String,  // The signature, which can be verified with pubkey
  timestamp: String  // Transaction timestamp
)
