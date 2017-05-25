package io.swagger.client.model


case class UpdateQuorumRequest(
  creator_pubkey: String, // Public key of creator&#39;s account
  quorum: Long, // Minimum number of signatures required to issue valid transaction.
  signature: String, // The signature, which can be verified with pubkey
  timestamp: String // Transaction timestamp
)
