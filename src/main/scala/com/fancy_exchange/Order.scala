package com.fancy_exchange

object Order {
  // (currency, price, amount)
  type Key = (String, Int, Int)
}

case class Order(clientId: String, currency: String, price: Int, amount: Int) {

  def toKey: Order.Key = (currency, price, amount)

  def toCounterpart: Order = Order(clientId, currency, price, -amount)
}


