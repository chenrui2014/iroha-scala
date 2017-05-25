package io.swagger.client.model


case class Transactions(
  code: Long, // response code
  message: String, // response message
  transactions: List[Transaction]
)
