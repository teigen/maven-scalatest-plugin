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

package org.scalatestexamples.concurrent

import org.scalatestexamples._
import org.scalatest.fixture.FixtureFunSuite
import org.scalatest.matchers.ShouldMatchers
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{TimeUnit, Semaphore, ArrayBlockingQueue}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.concurrent.ConductorFixture

class ConductorExamples extends FixtureFunSuite with ConductorFixture with ShouldMatchers {
  
  test("call to put on a full queue blocks the producer thread") { conductor => import conductor._
    val buf = new ArrayBlockingQueue[Int](1)

    thread("producer") {
      buf put 42
      buf put 17
      beat should be (1)
    }

    thread("consumer") {
      waitForBeat(1)
      buf.take should be (42)
      buf.take should be (17)
    }

    whenFinished {
      buf should be ('empty)
    }
  }

  test("compare and set") { conductor => import conductor._
    val ai = new AtomicInteger(1)

    thread {
      while (!ai.compareAndSet(2, 3)) Thread.`yield`
    }

    thread {
      ai.compareAndSet(1, 2) should be (true)
    }

    whenFinished {
      ai.get should be (3)
    }
  }

  test("interrupted aquire") { conductor => import conductor._
    val s = new Semaphore(0)

    val nice = thread("nice") {
      intercept[InterruptedException] {s.acquire}
      beat should be (1)
    }

    thread("rude") {
      waitForBeat(1)
      nice.interrupt
    }
  }

  test("thread ordering") { conductor => import conductor._
    val ai = new AtomicInteger(0)

    thread {
      ai.compareAndSet(0, 1) should be (true) // S1
      waitForBeat(3)
      ai.get() should be (3) // S4
    }

    thread {
      waitForBeat(1)
      ai.compareAndSet(1, 2) should be (true) // S2
      waitForBeat(3)
      ai.get should be (3) // S4
    }

    thread {
      waitForBeat(2)
      ai.compareAndSet(2, 3) should be (true) // S3
    }
  }

  test("timed offer") { conductor => import conductor._
    val q = new ArrayBlockingQueue[String](2)

    val producer = thread("producer") {
      q put "w"
      q put "x"

      withConductorFrozen {
        q.offer("y", 25, TimeUnit.MILLISECONDS) should be (false)
      }

      intercept[InterruptedException] {
        q.offer("z", 2500, TimeUnit.MILLISECONDS)
      }

      beat should be (1)
    }

    thread("consumer") {
      waitForBeat(1)
      producer.interrupt()
    }
  }
}
