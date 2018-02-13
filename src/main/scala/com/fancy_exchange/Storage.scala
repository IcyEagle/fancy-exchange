package com.fancy_exchange

import scala.io.Source
import java.io._

object Storage {

  def loadOrders: Stream[Order] = readOrders.map(parseOrder).toStream

  def loadClients: Stream[Client] = readClients.map(parseClient).toStream

  def storeClients(clients: List[Client]): Unit = {
    val data = clients.sortBy(_.id).map(stringifyClient).mkString("\n")
    val writer = new PrintWriter(new File("result.txt"))
    writer.write(data)
    writer.close()
  }

  def stringifyClient(client: Client): String = List(
    client.id,
    client.quote,
    client.bases("A"),
    client.bases("B"),
    client.bases("C"),
    client.bases("D")
  ).mkString("\t")

  def parseClient(data: String): Client = {
    data.split('\t').toList match {
      case (id: String) :: (quote: String) :: (baseA: String) :: (baseB: String) :: (baseC: String) :: (baseD: String) :: Nil => {
        Client(id, quote.toInt, Map(
          "A" -> baseA.toInt,
          "B" -> baseB.toInt,
          "C" -> baseC.toInt,
          "D" -> baseD.toInt
        ))
      }
      case _ => throw new RuntimeException("Incorrect data format")
    }
  }

  def parseOrder(data: String): Order = {
    data.split('\t').toList match {
      case (accountId: String) :: (side: String) :: (currency: String) :: (price: String) :: (amount: String) :: Nil =>
        val sign = toSign(side)
        Order(accountId, currency, price.toInt, amount.toInt * sign)
      case _ => throw new RuntimeException("Incorrect data format")
    }
  }

  private def readClients = Source.fromResource("clients.txt").getLines

  private def readOrders = Source.fromResource("orders.txt").getLines

  private def toSign(side: String) = side match {
    case "b" => 1
    case "s" => -1
  }
}
