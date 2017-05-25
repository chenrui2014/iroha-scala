package io.swagger.client.model


case class SignatoryRequest(
  creator_pubkey: String, // Public key of creator&#39;s account
  signatories: List[String], // Account signatories.
  signature: String, // The signature, which can be verified with pubkey
  timestamp: String // Transaction timestamp
)
