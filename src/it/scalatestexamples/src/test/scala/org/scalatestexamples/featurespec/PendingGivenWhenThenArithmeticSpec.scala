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
package org.scalatestexamples.featurespec

import org.scalatestexamples._
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen

class PendingGivenWhenThenArithmeticSpec extends FeatureSpec with GivenWhenThen {

  feature("Integer arithmetic") {

    scenario("addition") {

      given("two integers")
      when("they are added")
      then("the result is the sum of the two numbers")
      pending
    }

    scenario("subtraction") {

      given("two integers")
      when("one is subtracted from the other")
      then("the result is the difference of the two numbers")
      pending
    }
  }
}
