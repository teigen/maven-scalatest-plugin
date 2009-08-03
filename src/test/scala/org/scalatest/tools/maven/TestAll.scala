package org.scalatest.tools.maven

import _root_.junit.framework.TestCase
import _root_.junit.framework.Assert
import collection.mutable.ListBuffer

class TestAll extends TestCase {

  def testAll {
    val fails = new ListBuffer[Throwable]

    val reporter = new Reporter {
      override def testFailed(r: Report) = fails += r.throwable.get
      override def suiteAborted(r: Report) = fails += r.throwable.get
    }

    (new ScalaTestMojoSuite).execute(None, reporter, new Stopper{}, Set.empty, Set.empty, Map.empty, None)

    if(!fails.isEmpty){
      Assert.fail(fails.toString)
    }
  }
}