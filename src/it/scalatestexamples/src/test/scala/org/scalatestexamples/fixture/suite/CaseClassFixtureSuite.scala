package org.scalatestexamples.fixture.suite

import org.scalatest.fixture.FixtureSuite
import scala.collection.mutable.ListBuffer

class CaseClassFixtureSuite extends FixtureSuite {

  case class FixtureHolder(builder: StringBuilder, buffer: ListBuffer[String])

  type Fixture = FixtureHolder

  def withFixture(test: OneArgTest) {

    // Create needed mutable objects
    val stringBuilder = new StringBuilder("ScalaTest is ")
    val listBuffer = new ListBuffer[String]

    // Invoke the test function, passing in the mutable objects
    test(FixtureHolder(stringBuilder, listBuffer))
  }

  def testEasy(fixture: Fixture) {
    import fixture._
    builder.append("easy!")
    assert(builder.toString === "ScalaTest is easy!")
    assert(buffer.isEmpty)
    buffer += "sweet"
  }

  def testFun(fixture: Fixture) {
    fixture.builder.append("fun!")
    assert(fixture.builder.toString === "ScalaTest is fun!")
    assert(fixture.buffer.isEmpty)
  }
}

