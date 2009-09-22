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
import org.scalatestexamples.helpers.Stack

trait WordStackBehaviors { this: WordSpec =>

  def nonEmptyStack(lastItemAdded: Int)(stack: Stack[Int]) {

    "be non-empty" in {
      assert(!stack.empty)
    }

    "return the top item on peek" in {
      assert(stack.peek === lastItemAdded)
    }

    "not remove the top item on peek" in {
      val size = stack.size
      assert(stack.peek === lastItemAdded)
      assert(stack.size === size)
    }

    "remove the top item on pop" in {
      val size = stack.size
      assert(stack.pop === lastItemAdded)
      assert(stack.size === size - 1)
    }
  }

  def nonFullStack(stack: Stack[Int]) {

    "not be full" in {
      assert(!stack.full)
    }

    "add to the top on push" in {
      val size = stack.size
      stack.push(7)
      assert(stack.size === size + 1)
      assert(stack.peek === 7)
    }
  }
}
