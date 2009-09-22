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
package org.scalatestexamples

import org.scalatest.{FeatureSpec, GivenWhenThen}

class TVFeatureSpec extends FeatureSpec with GivenWhenThen {

  feature("Ability to change the channel on the TV remotely") {

    info("As a couch potato,")
    info("I want to surf the channels without getting up from my couch,")
    info("So that I need not interrupt my eating of potato chips.")

    scenario("The user presses the channel up button") (pending)
    scenario("The user presses the channel down button") (pending)

    scenario("The user presses number buttons and pauses") {

      given("the TV is on and tuned to a particular channel,")
      when("the user enters a number and waits,")
      then("the TV will change to that channel.")

      pending
    }
  }
}
