package com.fancy_exchange

import org.scalatest.WordSpec

class StorageTest extends WordSpec {

  "StorageTest" should {

    "parseOrder" in {
      // buy order
      assert(Storage.parseOrder("C8\tb\tC\t15\t4") == Order("C8", "C", 15, 4))

      // sell order
      assert(Storage.parseOrder("C8\ts\tC\t15\t4") == Order("C8", "C", 15, -4))
    }

    "parseClient" in {
      assert(Storage.parseClient("C1\t1000\t130\t240\t760\t320") == Client("C1", 1000, Map(
        "A" -> 130,
        "B" -> 240,
        "C" -> 760,
        "D" -> 320
      )))
    }

    "stringifyClient" in {
      assert(Storage.stringifyClient(Client("C1", 1000, Map(
        "A" -> 130,
        "B" -> 240,
        "C" -> 760,
        "D" -> 320
      ))) == "C1\t1000\t130\t240\t760\t320")
    }

  }
}
