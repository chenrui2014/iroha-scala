package io.swagger.client.model


case class AccountPayload(
  quorum: Integer, // Minimum number of signatures required to issue valid transaction.
  signatories: List[String], // Account signatories.
  username: Option[String], // account&#39;s username
  uuid: String // account&#39;s guid
)
