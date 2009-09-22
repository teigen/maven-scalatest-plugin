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

class StackFeatureSpec extends FeatureSpec with GivenWhenThen with FeatureSpecStackBehaviors {

  // Stack fixture creation methods
  def emptyStack = new Stack[Int]
 
  def fullStack = {
    val stack = new Stack[Int]
    for (i <- 0 until stack.MAX)
      stack.push(i)
    stack
  }
 
  def stackWithOneItem = {
    val stack = new Stack[Int]
    stack.push(9)
    stack
  }
 
  def stackWithOneItemLessThanCapacity = {
    val stack = new Stack[Int]
    for (i <- 1 to 9)
      stack.push(i)
    stack
  }
 
  val lastValuePushed = 9
 
  feature("A Stack is pushed and popped") {
 
    scenario("empty is invoked on an empty stack") {

      given("an empty stack")
      val stack = emptyStack

      when("empty is invoked on the stack")
      then("empty returns true")
      assert(stack.empty)
    }
 
    scenario("peek is invoked on an empty stack") {

      given("an empty stack")
      val stack = emptyStack

      when("peek is invoked on the stack")
      then("peek throws IllegalStateException")
      intercept[IllegalStateException] {
        stack.peek
      }
    }
 
    scenario("pop is invoked on an empty stack") {

      given("an empty stack")
      val stack = emptyStack

      when("pop is invoked on the stack")
      then("pop throws IllegalStateException")
      intercept[IllegalStateException] {
        emptyStack.pop
      }
    }
 
    scenariosFor(nonEmptyStack(stackWithOneItem, lastValuePushed))
    scenariosFor(nonFullStack(stackWithOneItem))
 
    scenariosFor(nonEmptyStack(stackWithOneItemLessThanCapacity, lastValuePushed))
    scenariosFor(nonFullStack(stackWithOneItemLessThanCapacity))
 
    scenario("full is invoked on a full stack") {

      given("an full stack")
      val stack = fullStack

      when("full is invoked on the stack")
      then("full returns true")
      assert(stack.full)
    }
 
    scenariosFor(nonEmptyStack(fullStack, lastValuePushed))
 
    scenario("push is invoked on a full stack") {

      given("an full stack")
      val stack = fullStack

      when("push is invoked on the stack")
      then("push throws IllegalStateException")
      intercept[IllegalStateException] {
        stack.push(10)
      }
    }
  }
}
