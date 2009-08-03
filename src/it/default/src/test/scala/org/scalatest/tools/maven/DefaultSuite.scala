package org.scalatest.tools.maven

class DefaultSuite extends FunSuite {
  test("first") {
    assert(1 === 1)
  }

  test("second") {
    assert(2 === 2)
  }
}