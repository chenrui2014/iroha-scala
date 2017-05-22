package io.swagger.client.model

case class SignatoryRequest (
  creator_pubkey: String,  // Public key of creator's account
  signatories: List[String],  // Account Signatories
  signature: String,  // The signature, which can be verified with pubkey
  timestamp: String  // Transaction timestamp
)
