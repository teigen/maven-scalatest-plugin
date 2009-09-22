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

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Tag, WordSpec}
import org.scalatestexamples.helpers.Stack

object SlowTest extends Tag("SlowTest")

class StackCanWordSpec extends WordSpec with StackFixtureCreationMethods with WordStackBehaviors with ShouldMatchers {

  def it = afterWord("it")
  
  "A Stack" when it {
    
    "is empty" can {

      "be empty" in {
        assert(emptyStack.empty)
      }

      "complain on peek" ignore {
        intercept[IllegalStateException] {
          emptyStack.peek
        }
      }

      "complain on pop" in {
        intercept[IllegalStateException] {
          emptyStack.pop
        }
      }
    }

    "has one item" can {
  
      behave like nonEmptyStack(lastValuePushed)(stackWithOneItem)
      behave like nonFullStack(stackWithOneItem)
    }

    "has one item less than capacity" can {

      behave like nonEmptyStack(lastValuePushed)(stackWithOneItemLessThanCapacity)
      behave like nonFullStack(stackWithOneItemLessThanCapacity)
    }

    "is full" can {

      "be full" in {
        assert(fullStack.full)
      }

      "go to sleep soon" is (pending)

      behave like nonEmptyStack(lastValuePushed)(fullStack)

      "complain on a push" in {
        intercept[IllegalStateException] {
          fullStack.push(10)
        }
      }
    }
  }
}
