package com.fancy_exchange

object Exchange extends App {

  type OrderBook = Map[Order.Key, List[Order]]
  type State = (OrderBook, Map[String, Client])

  def processOrder(state: State, order: Order) = {
    val (orderBook, clients) = state
    val counterpartKey = order.toCounterpart.toKey

    orderBook.getOrElse(counterpartKey, List.empty) match {
      case (counterpartOrder: Order) :: rest =>
        val updatedOrderBook = orderBook.updated(counterpartKey, rest)
        val taker = clients(order.clientId).exchange(order.currency, order.price, order.amount)
        val maker = clients(counterpartOrder.clientId).exchange(counterpartOrder.currency, counterpartOrder.price, counterpartOrder.amount)
        val updatedClients = clients + (order.clientId -> taker, counterpartOrder.clientId -> maker)
        (updatedOrderBook, updatedClients)
      case Nil =>
        val orderKey = order.toKey
        val updatedOrderList = order :: orderBook.getOrElse(orderKey, List.empty)
        val updatedOrderBook = orderBook.updated(orderKey, updatedOrderList)
        (updatedOrderBook, clients)
    }
  }

  def simulate(clients: Stream[Client], orders: Stream[Order]): List[Client] = {
    val clientMap = clients.map(o => o.id -> o).toMap
    val orderBook: OrderBook = Map.empty
    val state = (orderBook, clientMap)
    orders.foldLeft(state)(processOrder)._2.values.toList
  }

  val clients = Storage.loadClients
  val orders = Storage.loadOrders
  val updatedClients = simulate(clients, orders)
  Storage.storeClients(updatedClients)
}
