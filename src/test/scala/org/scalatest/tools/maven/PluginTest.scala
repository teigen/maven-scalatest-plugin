package org.scalatest.tools.maven

import java.io.File
import junit.JUnit3Suite
import matchers.ShouldMatchers

class PluginTest extends JUnit3Suite with ShouldMatchers with PluginMatchers {

  val testOutputDirectory = File.createTempFile("scala", "test").getAbsolutePath
  val outputDirectory = File.createTempFile("scala", "test").getAbsolutePath
  val reportsDirectory = File.createTempFile("scala", "test").getAbsolutePath

  def testDefault {
    val config = configure(_ => ())

    config should containSlice("-p", testOutputDirectory + " " + outputDirectory)
    config should contain("-o")
    config should containSlice("-r", "org.scalatest.tools.maven.FailReporter")
    config should have length (5)
  }

  def testProperties {
    val config = configure {
      mojo =>
              val props = new java.util.Properties
              props.setProperty("foo", "bar")
              props.setProperty("monkey", "donkey")
              mojo.properties = props
    }
    config should contain("-Dfoo=bar")
    config should contain("-Dmonkey=donkey")
  }

  def testRunpath {
    configure(_.additionalRunpaths = Array("http://foo.com/my.jar", "/some/where")) should containCompoundArgs("-p", testOutputDirectory, outputDirectory, "http://foo.com/my.jar", "/some/where")
  }

  def testFileReporters {
    val config = configure(_.fileReporters = Array("foo.txt", "YZT some.txt"))
    config should containSlice("-f", new File(reportsDirectory, "foo.txt").getAbsolutePath)
    config should containSlice("-fYZT", new File(reportsDirectory, "some.txt").getAbsolutePath)
  }

  def testStdOutReporter {
    configure(_.stdout = "GUP") should contain ("-oGUP")

  }

  def testStdErrReporter {
    configure(_.stderr = "BIS") should contain ("-eBIS")
  }

  def testIncludes {
    configure(_.includes = "a, b,c") should containCompoundArgs("-n", "a", "b", "c")
  }

  def testExcludes {
    configure(_.excludes = "a,b ,c") should containCompoundArgs("-x", "a", "b", "c")
  }

  def testConcurrent {
    configure(_.concurrent = true) should contain("-c")
    configure(_.concurrent = false) should not contain ("-c")
  }

  def testSuites {
    configure(_.suites = "a,b,c") should containSuiteArgs("-s", "a", "b", "c")
  }

  def testMembers {
    configure(_.members = "a, b, c") should containSuiteArgs("-m", "a", "b", "c")
  }

  def testWildcards {
    configure(_.wildcards = "a,b,c") should containSuiteArgs("-w", "a", "b", "c")
  }

  def testTestNg {
    configure(_.testNG = "a,b,c") should containSuiteArgs("-t", "a", "b", "c")
  }

  def configure(m: TestMojo => Unit) = {
    val mojo = new TestMojo
    mojo.testOutputDirectory = new File(testOutputDirectory)
    mojo.outputDirectory = new File(outputDirectory)
    mojo.scalaTestReportsDirectory = new File(reportsDirectory)
    mojo.testClasspathElements = new java.util.ArrayList[String]()
    m(mojo)
    mojo.configuration
  }

  def testGui {
    val mojo = new GuiMojo
    mojo.testOutputDirectory = new File(testOutputDirectory)
    mojo.outputDirectory = new File(outputDirectory)
    mojo.testClasspathElements = new java.util.ArrayList[String]()
    mojo.configuration should contain ("-g")
    mojo.configuration should containSlice("-p", testOutputDirectory + " " + outputDirectory)

    mojo.gui = "BIS"
    mojo.configuration should contain ("-gBIS")
  }
}