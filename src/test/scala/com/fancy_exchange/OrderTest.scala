package com.fancy_exchange

import org.scalatest.WordSpec

class OrderTest extends WordSpec {

  "OrderTest" should {

    "toKey" in {
      assert(Order("C1", "B", 200, 2).toKey == ("B", 200, 2))
    }

    "toCounterpart" in {
      assert(Order("C1", "B", 200, 2).toCounterpart == Order("C1", "B", 200, -2))
    }

  }
}
