/*
 * Copyright 2001-2009 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatestexamples.funsuite

import org.scalatestexamples._
import org.scalatest.FunSuite
import scala.collection.mutable.ListBuffer

class CreateFixtureExampleSuite extends FunSuite {

  // create objects needed by tests and return as a tuple
  def createFixture = (
    new StringBuilder("ScalaTest is "),
    new ListBuffer[String]
  )

  test("easy") {
    val (builder, lbuf) = createFixture
    builder.append("easy!")
    assert(builder.toString === "ScalaTest is easy!")
    assert(lbuf.isEmpty)
    lbuf += "sweet"
  }

  test("fun") {
    val (builder, lbuf) = createFixture
    builder.append("fun!")
    assert(builder.toString === "ScalaTest is fun!")
    assert(lbuf.isEmpty)
  }
}
