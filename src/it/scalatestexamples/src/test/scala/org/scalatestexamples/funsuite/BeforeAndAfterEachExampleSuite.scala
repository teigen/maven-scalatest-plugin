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
import org.scalatest.BeforeAndAfterEach
import java.io.FileReader
import java.io.FileWriter
import java.io.File

class BeforeAndAfterEachExampleSuite extends FunSuite with BeforeAndAfterEach {

  private val FileName = "TempFile.txt"
  private var reader: FileReader = _

  // Set up the temp file needed by the test
  override def beforeEach() {
    val writer = new FileWriter(FileName)
    try {
      writer.write("Hello, test!")
    }
    finally {
      writer.close()
    }

    // Create the reader needed by the test
    reader = new FileReader(FileName)
  }

  // Close and delete the temp file
  override def afterEach() {
    reader.close()
    val file = new File(FileName)
    file.delete()
  }

  test("reading from the temp file") {
    var builder = new StringBuilder
    var c = reader.read()
    while (c != -1) {
      builder.append(c.toChar)
      c = reader.read()
    }
    assert(builder.toString === "Hello, test!")
  }

  test("first char of the temp file") {
    assert(reader.read() === 'H')
  }
}
