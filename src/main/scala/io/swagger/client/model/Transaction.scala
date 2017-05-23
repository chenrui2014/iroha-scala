package io.swagger.client.model

case class Transaction (
  created_at: String,  // transaction creation time
  creator_signature: String,  // creator's signature
  nonce: Long,  // random 4 bytes
  signatures: List[Signature]
)
