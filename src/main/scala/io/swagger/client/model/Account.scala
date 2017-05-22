package io.swagger.client.model

case class Account (
  quorum: Integer,  // Minimum number of signatures required to issue valid transaction.
  signatories: List[String],  // Account Signatories
  username: String,  // account's username
  uuid: String  // account's guid
)
