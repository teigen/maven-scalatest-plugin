/*
 * Copyright 2001-2008 Artima, Inc.
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

import org.scalatest.Spec
import org.scalatest.BeforeAndAfter
import org.scalatestexamples.helpers.Stack

trait StackBehaviors extends BeforeAndAfter { this: Spec =>

  def nonEmptyStack(lastItemAdded: Int)(implicit stack: Stack[Int]) {

    it("should be non-empty") {
      assert(!stack.empty)
    }  

    it("should return the top item on peek") {
      assert(stack.peek === lastItemAdded)
    }
  
    it("should not remove the top item on peek") {
      val size = stack.size
      assert(stack.peek === lastItemAdded)
      assert(stack.size === size)
    }

    it("should remove the top item on pop") {
      val size = stack.size
      assert(stack.pop === lastItemAdded)
      assert(stack.size === size - 1)
    }
  }
  
  def nonFullStack(implicit stack: Stack[Int]) {
      
    it("should not be full") {
      assert(!stack.full)
    }
      
    it("should add to the top on push") {
      val size = stack.size
      stack.push(7)
      assert(stack.size === size + 1)
      assert(stack.peek === 7)
    }
  }
}

class SharedTestExampleSpec extends Spec with StackBehaviors {

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

  describe("A Stack") {

    describe("(when empty)") {
      
      it("should be empty") {
        assert(emptyStack.empty)
      }

      it("should complain on peek") {
        intercept[IllegalStateException] {
          emptyStack.peek
        }
      }

      it("should complain on pop") {
        intercept[IllegalStateException] {
          emptyStack.pop
        }
      }
    }

    describe("(with one item)") {
      implicit def stackFixture = stackWithOneItem
      it should behave like nonEmptyStack(lastValuePushed)
      it should behave like nonFullStack
    }
    
    describe("(with one item less than capacity)") {
      implicit def stackFixture = stackWithOneItemLessThanCapacity
      it should behave like nonEmptyStack(lastValuePushed)
      it should behave like nonFullStack
    }

    describe("(full)") {
      
      it("should be full") {
        assert(fullStack.full)
      }

      it should behave like nonEmptyStack(lastValuePushed)(fullStack)

      it("should complain on a push") {
        intercept[IllegalStateException] {
          fullStack.push(10)
        }
      }
    }
  }
}
 
