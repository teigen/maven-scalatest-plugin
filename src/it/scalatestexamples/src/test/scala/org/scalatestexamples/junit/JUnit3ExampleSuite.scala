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
package org.scalatestexamples.junit

import org.scalatestexamples._
import _root_.junit.framework.AssertionFailedError
import org.scalatest.junit.JUnit3Suite

class JUnit3ExampleSuite extends JUnit3Suite {

  def testSuccess() {}
  def testFailure() { throw new AssertionFailedError }
  def testError() { throw new IllegalArgumentException }
  def testFailureWithMessage() { throw new AssertionFailedError("howdy there") }
  def testErrorWithMessage() { throw new IllegalArgumentException("What's up, doc?") }
}
