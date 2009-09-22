package org.scalatestexamples.fixture.suite

import org.scalatest.fixture.FixtureSuite
import scala.collection.mutable.ListBuffer
      
class TupleFixtureSuite extends FixtureSuite {

  type Fixture = (StringBuilder, ListBuffer[String])

  def withFixture(test: OneArgTest) {

    // Create needed mutable objects
    val sb = new StringBuilder("ScalaTest is ")
    val lb = new ListBuffer[String]

    // Invoke the test function, passing in the mutable objects
    test(sb, lb)
  }

  def testEasy(fixture: Fixture) {
    val (builder, lbuf) = fixture
    builder.append("easy!")
    assert(builder.toString === "ScalaTest is easy!")
    assert(lbuf.isEmpty)
    lbuf += "sweet"
  }

  def testFun(fixture: Fixture) {
    val (builder, lbuf) = fixture
    builder.append("fun!")
    assert(builder.toString === "ScalaTest is fun!")
    assert(lbuf.isEmpty)
  }
}
