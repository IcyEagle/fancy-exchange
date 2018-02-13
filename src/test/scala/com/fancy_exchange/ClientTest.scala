package com.fancy_exchange

import org.scalatest.WordSpec

class ClientTest extends WordSpec {

  "ClientTest" should {

    "exchange" in {
      assert(
        Client("C1", 1000, Map("A" -> 42, "B" -> 15, "C" -> 25, "D" -> 35))
          .exchange("B", 200, 2)
          == Client("C1", 600, Map("A" -> 42, "B" -> 17, "C" -> 25, "D" -> 35)))
    }

  }
}
