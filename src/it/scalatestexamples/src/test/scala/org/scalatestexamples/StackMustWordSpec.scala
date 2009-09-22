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

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatestexamples.helpers.Stack

class StackMustWordSpec extends WordSpec with StackFixtureCreationMethods with WordStackBehaviors with ShouldMatchers {

  def it = afterWord("it")
  
  "A Stack" when it {
    
    "is empty" must {

      "be empty" in {
        assert(emptyStack.empty)
      }

      "complain on peek" in {
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

    "has one item" must {
  
      behave like nonEmptyStack(lastValuePushed)(stackWithOneItem)
      behave like nonFullStack(stackWithOneItem)
    }

    "has one item less than capacity" must {

      behave like nonEmptyStack(lastValuePushed)(stackWithOneItemLessThanCapacity)
      behave like nonFullStack(stackWithOneItemLessThanCapacity)
    }

    "is full" must {

      "be full" in {
        assert(fullStack.full)
      }

      "go to sleep soon" in (pending)

      behave like nonEmptyStack(lastValuePushed)(fullStack)

      "complain on a push" in {
        intercept[IllegalStateException] {
          fullStack.push(10)
        }
      }
    }
  }
}
