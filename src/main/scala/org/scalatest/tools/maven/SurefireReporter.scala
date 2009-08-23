package org.scalatest.tools.maven

import apache.maven.surefire.report.{XMLReporter, StackTraceWriter, ReportEntry, ReporterManager, Reporter => SReporter, FileReporter => SFileReporter}

class SurefireReporter extends Reporter {

  implicit def report2ReportEntry(r:Report) = {
    r.throwable match {
      case Some(throwable) => new ReportEntry(r.name, r.name, r.message, new ScalaTestStackTraceWriter(throwable))
      case _ => new ReportEntry(r.name, r.name, r.message)
    }
  }

  private class ScalaTestStackTraceWriter(throwable:Throwable) extends StackTraceWriter {
    import java.io.{PrintWriter, StringWriter}

    def getThrowable = throwable

    def writeTraceToString = {
      val writer = new StringWriter
      throwable.printStackTrace(new PrintWriter(writer))
      writer.flush
      writer.toString
    }

    //TODO - write a trimmed version ? (see org.apache.maven.surefire.report.PojoStackTraceWriter)
    def writeTrimmedTraceToString = writeTraceToString
  }

  val reporters = {
    val list = new java.util.ArrayList[SReporter]
    list.add(new SFileReporter(SurefireReporterConfig.reportsDirectory, false))
    list.add(new XMLReporter(SurefireReporterConfig.reportsDirectory, false))
    list
  }

  private val surefireReporterManager = new ReporterManager(reporters)

  override def runStopped() = surefireReporterManager.runStopped()

  override def suiteAborted(r: Report) = surefireReporterManager.testSetAborted(r)

  override def testIgnored(r: Report) = surefireReporterManager.testSkipped(r)

  override def testStarting(r: Report) = surefireReporterManager.testStarting(r)

  override def runAborted(r: Report) = surefireReporterManager.runAborted(r)

  override def suiteCompleted(r: Report) = surefireReporterManager.testSetCompleted(r)

  //override def dispose() = null

  override def runCompleted() = surefireReporterManager.runCompleted()

  override def testSucceeded(r: Report) = surefireReporterManager.testSucceeded(r)

  override def infoProvided(r: Report) = surefireReporterManager.writeMessage(r.message)

  override def runStarting(i: Int) = surefireReporterManager.runStarting(i)

  override def suiteStarting(r: Report) = surefireReporterManager.testSetStarting(r)

  override def testFailed(r: Report) = surefireReporterManager.testFailed(r)
}