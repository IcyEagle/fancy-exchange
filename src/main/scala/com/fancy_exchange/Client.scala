package com.fancy_exchange

case class Client(id: String, quote: Int, bases: Map[String, Int]) {

  def exchange(base: String, price: Int, amount: Int): Client = {
    Client(id, quote - amount * price, bases.updated(base, bases(base) + amount))
  }

}
