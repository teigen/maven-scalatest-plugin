/*
 * Copyright 2001-2009 OFFIS, Tammo Freese
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
package org.scalatestexamples.jmock

import org.scalatestexamples.easymock.{Collaborator, ClassTested}
import org.jmock.Mockery
import org.scalatestexamples._
import org.junit.Assert._
import java.util.ArrayList
import java.util.List
import org.easymock.IAnswer
import org.junit.Before
import org.junit.Test
import org.scalatest.verb.ShouldVerb
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import org.scalatest.fixture.FixtureFlatSpec
import org.scalatest.mock.{JMockCycleFixture, JMockCycle}
import org.jmock.Expectations.returnValue
import org.jmock.Expectations.equal

class JMockExampleFixtureFlatSpec extends FixtureFlatSpec with JMockCycleFixture {

  def createTestedAndMock(cycle: JMockCycle) = { import cycle._
    val mockCollaborator = mock[Collaborator]
    val classUnderTest = new ClassTested()
    classUnderTest.addListener(mockCollaborator)
    (classUnderTest, mockCollaborator)
  }

  "ClassTested" should "not call the collaborator when removing a non-existing document" in { cycle => import cycle._

    val (classUnderTest, mockCollaborator) = createTestedAndMock(cycle)

    // expecting nothing

    whenExecuting {
      classUnderTest.removeDocument("Does not exist")
    }
  }

  it should "call documentAdded on the Collaborator when a new document is added" in { cycle => import cycle._

    val (classUnderTest, mockCollaborator) = createTestedAndMock(cycle)

    expecting { e => import e._
      e.oneOf (mockCollaborator).documentAdded("New Document")
    }

    whenExecuting {
      classUnderTest.addDocument("New Document", new Array[Byte](0))
    }
  }

  it should "call documentChanged on the Collaborator when a document is changed" in { cycle => import cycle._

    val (classUnderTest, mockCollaborator) = createTestedAndMock(cycle)

    expecting { e => import e._
      oneOf (mockCollaborator).documentAdded("Document")
      exactly(3).of (mockCollaborator).documentChanged("Document")
    }

    whenExecuting {
      classUnderTest.addDocument("Document", new Array[Byte](0))
      classUnderTest.addDocument("Document", new Array[Byte](0))
      classUnderTest.addDocument("Document", new Array[Byte](0))
      classUnderTest.addDocument("Document", new Array[Byte](0))
    }
  }

  it should "call voteForRemoval on Collaborator when removeDocument is called on ClassTested, and " +
          "if a POSITIVE number is returned (i.e., a vote FOR removal), documentRemoved " +
          "should be called on Collaborator" in { cycle => import cycle._

    val (classUnderTest, mockCollaborator) = createTestedAndMock(cycle)

    expecting { e => import e._
      // expect document addition
      oneOf (mockCollaborator).documentAdded("Document");
      // expect to be asked to vote, and vote for it
      oneOf (mockCollaborator).voteForRemoval("Document"); will(returnValue((42).asInstanceOf[Byte]));
      // expect document removal
      oneOf (mockCollaborator).documentRemoved("Document");
    }

    whenExecuting {
      classUnderTest.addDocument("Document", new Array[Byte](0));
      assert(classUnderTest.removeDocument("Document"))
    }
  }

  it should "call voteForRemoval on Collaborator when removeDocument is called on ClassTested, and " +
          "if a NEGATIVE number is returned (i.e., a vote AGAINST removal), documentRemoved " +
          "should NOT be called on Collaborator" in { cycle => import cycle._

    val (classUnderTest, mockCollaborator) = createTestedAndMock(cycle)

    expecting { e => import e._
      // expect document addition
      oneOf (mockCollaborator).documentAdded("Document");
      // expect to be asked to vote, and vote against it
      oneOf (mockCollaborator).voteForRemoval("Document"); will(returnValue((-42).asInstanceOf[Byte])); //
      // document removal is *not* expected
    }

    whenExecuting {
      classUnderTest.addDocument("Document", new Array[Byte](0));
      assert(!classUnderTest.removeDocument("Document"))
    }
  }

  it should "call voteForRemoval on Collaborator when removeDocument is called on ClassTested " +
          "to remove multiple documents, and if a POSITIVE number is returned (i.e., a vote " +
          "FOR removal), documentRemoved should be called on Collaborator" in { cycle => import cycle._

    val (classUnderTest, mockCollaborator) = createTestedAndMock(cycle)

    expecting { e => import e._
      oneOf (mockCollaborator).documentAdded("Document 1");
      oneOf (mockCollaborator).documentAdded("Document 2");
      val documents = Array("Document 1", "Document 2")
      oneOf (mockCollaborator).voteForRemovals(`with`(equal(documents))); will(returnValue((42).asInstanceOf[Byte]));
      oneOf (mockCollaborator).documentRemoved("Document 1");
      oneOf (mockCollaborator).documentRemoved("Document 2");
    }

    whenExecuting {
      classUnderTest.addDocument("Document 1", new Array[Byte](0));
      classUnderTest.addDocument("Document 2", new Array[Byte](0));
      assert(classUnderTest.removeDocuments(Array("Document 1",
              "Document 2")))
    }
  }

  it should "call voteForRemoval on Collaborator when removeDocument is called on ClassTested " +
          "to remove multiple documents, and if a NEGATIVE number is returned (i.e., a vote " +
          "AGAINST removal), documentRemoved should NOT be called on Collaborator" in { cycle => import cycle._

    val (classUnderTest, mockCollaborator) = createTestedAndMock(cycle)

    expecting { e => import e._
      oneOf (mockCollaborator).documentAdded("Document 1");
      oneOf (mockCollaborator).documentAdded("Document 2");
      val documents = Array("Document 1", "Document 2")
      oneOf (mockCollaborator).voteForRemovals(`with`(equal(documents))); will(returnValue((-42).asInstanceOf[Byte]));
    }

    whenExecuting {
      classUnderTest.addDocument("Document 1", new Array[Byte](0));
      classUnderTest.addDocument("Document 2", new Array[Byte](0));
      assert(!classUnderTest.removeDocuments(Array("Document 1",
              "Document 2")))
    }
  }
}
