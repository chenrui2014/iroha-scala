package io.swagger.client.model


case class Signature(
  pubkey: String, // ed25519 public key, which should be used to validate the signature
  signature: String // The signature, which can be verified with pubkey
)
