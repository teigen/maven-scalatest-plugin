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

import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatestexamples.helpers.Stack

trait FeatureSpecStackBehaviors { this: FeatureSpec with GivenWhenThen =>

  def nonEmptyStack(createNonEmptyStack: => Stack[Int], lastItemAdded: Int) {

    scenario("empty is invoked on this non-empty stack: " + createNonEmptyStack.toString) {

      given("a non-empty stack")
      val stack = createNonEmptyStack

      when("empty is invoked on the stack")
      then("empty returns false")
      assert(!stack.empty)
    }

    scenario("peek is invoked on this non-empty stack: " + createNonEmptyStack.toString) {

      given("a non-empty stack")
      val stack = createNonEmptyStack
      val size = stack.size

      when("peek is invoked on the stack")
      then("peek returns the last item added")
      assert(stack.peek === lastItemAdded)

      and("the size of the stack is the same as before")
      assert(stack.size === size)
    }

    scenario("pop is invoked on this non-empty stack: " + createNonEmptyStack.toString) {

      given("a non-empty stack")
      val stack = createNonEmptyStack
      val size = stack.size

      when("pop is invoked on the stack")
      then("pop returns the last item added")
      assert(stack.pop === lastItemAdded)

      and("the size of the stack one less than before")
      assert(stack.size === size - 1)
    }
  }
  
  def nonFullStack(createNonFullStack: => Stack[Int]) {
      
    scenario("full is invoked on this non-full stack: " + createNonFullStack.toString) {

      given("a non-full stack")
      val stack = createNonFullStack

      when("full is invoked on the stack")
      then("full returns false")
      assert(!stack.full)
    }
      
    scenario("push is invoked on this non-full stack: " + createNonFullStack.toString) {

      given("a non-full stack")
      val stack = createNonFullStack
      val size = stack.size

      when("push is invoked on the stack")
      stack.push(7)

      then("the size of the stack is one greater than before")
      assert(stack.size === size + 1)

      and("the top of the stack contains the pushed value")
      assert(stack.peek === 7)
    }
  }
}
