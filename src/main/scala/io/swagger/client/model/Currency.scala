package io.swagger.client.model


case class Currency(
  code: Long, // response code
  currency: List[CurrencyPayload],
  message: String // response message
)
