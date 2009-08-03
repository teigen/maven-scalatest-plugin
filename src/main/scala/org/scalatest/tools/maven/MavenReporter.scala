package org.scalatest.tools.maven


class MavenReporter extends Reporter {
  override def testFailed(r: Report) {
    Result.fail()
  }

  override def runAborted(r: Report) {
    Result.fail()
  }

}