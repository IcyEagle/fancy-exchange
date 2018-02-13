package com.fancy_exchange

import com.fancy_exchange.Exchange.OrderBook
import org.scalatest.WordSpec

class ExchangeTest extends WordSpec {

  "ExchangeTest" can {

    "processOrder" should {

      "add order to order book" in {
        val orderBook = Map(("B", 200, 2) -> List(Order("C1", "B", 200, 2)))
        val clients = Map("C1" -> Client("C1", 1000, Map("A" -> 10, "B" -> 20, "C" -> 30, "D" -> 40)))
        val state = (orderBook, clients)
        val order = Order("C1", "C", 100, 5)

        val expectedOrderBook = Map(("B", 200, 2) -> List(Order("C1", "B", 200, 2)), ("C", 100, 5) -> List(Order("C1", "C", 100, 5)))
        val expectedClients = clients

        assert(Exchange.processOrder(state, order) == (expectedOrderBook, expectedClients))
      }

      "add order to order book if counterpart order has been executed earlier" in {
        val orderBook = Map(("C", 100, 5) -> List.empty)
        val clients = Map("C1" -> Client("C1", 1000, Map("A" -> 10, "B" -> 20, "C" -> 30, "D" -> 40)))
        val state = (orderBook, clients)
        val order = Order("C1", "C", 100, 5)

        val expectedOrderBook = Map(("C", 100, 5) -> List(Order("C1", "C", 100, 5)))
        val expectedClients = clients

        assert(Exchange.processOrder(state, order) == (expectedOrderBook, expectedClients))
      }

      "add order to order book if similar order is present" in {
        val orderBook = Map(("C", 100, 5) -> List(Order("C3", "C", 100, 5)))
        val clients = Map("C1" -> Client("C1", 1000, Map("A" -> 10, "B" -> 20, "C" -> 30, "D" -> 40)))
        val state = (orderBook, clients)
        val order = Order("C1", "C", 100, 5)

        val expectedOrderBook = Map(("C", 100, 5) -> List(Order("C1", "C", 100, 5), Order("C3", "C", 100, 5)))
        val expectedClients = clients

        assert(Exchange.processOrder(state, order) == (expectedOrderBook, expectedClients))
      }

      "execute order and change balances" in {
        val orderBook = Map(("B", 200, 2) -> List(Order("C1", "B", 200, 2)))
        val clients = Map(
          "C1" -> Client("C1", 1000, Map("A" -> 10, "B" -> 20, "C" -> 30, "D" -> 40)),
          "C2" -> Client("C2", 2000, Map("A" -> 20, "B" -> 40, "C" -> 60, "D" -> 80))
        )
        val state = (orderBook, clients)
        val order = Order("C2", "B", 200, -2)

        val expectedOrderBook: OrderBook = Map(("B", 200, 2) -> List.empty)
        val expectedClients = Map(
          "C1" -> Client("C1", 600, Map("A" -> 10, "B" -> 22, "C" -> 30, "D" -> 40)),
          "C2" -> Client("C2", 2400, Map("A" -> 20, "B" -> 38, "C" -> 60, "D" -> 80))
        )

        assert(Exchange.processOrder(state, order) == (expectedOrderBook, expectedClients))
      }

      "execute exactly one order" in {
        val orderBook = Map(("C", 100, 5) -> List(Order("C2", "C", 100, 5), Order("C3", "C", 100, 5)))
        val clients = Map(
          "C1" -> Client("C1", 1000, Map("A" -> 10, "B" -> 20, "C" -> 30, "D" -> 40)),
          "C2" -> Client("C2", 2000, Map("A" -> 20, "B" -> 40, "C" -> 60, "D" -> 80)),
          "C3" -> Client("C3", 2000, Map("A" -> 20, "B" -> 40, "C" -> 60, "D" -> 80))
        )
        val state = (orderBook, clients)
        val order = Order("C1", "C", 100, -5)

        val expectedOrderBook = Map(("C", 100, 5) -> List(Order("C3", "C", 100, 5)))
        val expectedClients = Map(
          "C1" -> Client("C1", 1500, Map("A" -> 10, "B" -> 20, "C" -> 25, "D" -> 40)),
          "C2" -> Client("C2", 1500, Map("A" -> 20, "B" -> 40, "C" -> 65, "D" -> 80)),
          "C3" -> Client("C3", 2000, Map("A" -> 20, "B" -> 40, "C" -> 60, "D" -> 80))
        )

        assert(Exchange.processOrder(state, order) == (expectedOrderBook, expectedClients))
      }

    }

    "simulate" in {
      val clients = List(
        Client("C1", 1000, Map("A" -> 10, "B" -> 20, "C" -> 30, "D" -> 40)),
        Client("C2", 2000, Map("A" -> 10, "B" -> 20, "C" -> 30, "D" -> 40))
      ).toStream

      val orders = List(
        Order("C1", "C", 100, 5),
        Order("C1", "B", 200, 5),
        Order("C1", "B", 250, 2),  // pair #1
        Order("C2", "B", 300, -5),
        Order("C2", "A", 230, 2),  // pair #2
        Order("C2", "B", 250, -2), // pair #1
        Order("C1", "A", 230, -2)  // pair #2
      ).toStream

      assert(Exchange.simulate(clients, orders) == List(
        Client("C1", 960, Map("A" -> 8, "B" -> 22, "C" -> 30, "D" -> 40)),
        Client("C2", 2040, Map("A" -> 12, "B" -> 18, "C" -> 30, "D" -> 40))
      ))
    }

  }
}
