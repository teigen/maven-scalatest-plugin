package org.scalatest.tools.maven

import java.io.File
import matchers.ShouldMatchers

class ScalaTestMojoSuite extends FunSuite with ShouldMatchers with PluginMatchers {
  val testOutputDirectory = File.createTempFile("scala", "test").getAbsolutePath
  val reportsDirectory = File.createTempFile("scala", "test").getAbsolutePath


  test("default") {
    val config = configure(_ => ())

    config should containSlice("-p", testOutputDirectory)
    config should contain("-o")
    config should containSlice("-r", "org.scalatest.tools.maven.MavenReporter")

    config should have length (5)
  }

  test("properties") {
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

  test("runpath") {
    configure(_.additionalRunpaths = Array("http://foo.com/my.jar", "/some/where")) should containCompoundArgs("-p", testOutputDirectory, "http://foo.com/my.jar", "/some/where")
  }

  test("file reporters") {
    val config = configure(_.fileReporters = Array("foo.txt", "YZT some.txt"))
    config should containSlice("-f", new File(reportsDirectory, "foo.txt").getAbsolutePath)
    config should containSlice("-fYZT", new File(reportsDirectory, "some.txt").getAbsolutePath)
  }

  test("stdOut reporter") {
    configure(_.stdout = "GUP") should contain ("-oGUP")

  }

  test("stdErr reporter") {
    configure(_.stderr = "BIS") should contain ("-eBIS")
  }

  test("gui reporter") {
    configure(_.gui = "PBI") should contain ("-gPBI")
  }

  test("includes") {
    configure(_.includes = "a, b,c") should containCompoundArgs("-n", "a", "b", "c")
  }

  test("excludes") {
    configure(_.excludes = "a,b ,c") should containCompoundArgs("-x", "a", "b", "c")
  }

  test("concurrent") {
    configure(_.concurrent = true) should contain("-c")
    configure(_.concurrent = false) should not contain ("-c")
  }

  test("suites") {
    configure(_.suites = "a,b,c") should containSuiteArgs("-s", "a", "b", "c")
  }

  test("members") {
    configure(_.members = "a, b, c") should containSuiteArgs("-m", "a", "b", "c")
  }

  test("wildcards") {
    configure(_.wildcards = "a,b,c") should containSuiteArgs("-w", "a", "b", "c")
  }

  test("testNg") {
    configure(_.testNG = "a,b,c") should containSuiteArgs("-t", "a", "b", "c")
  }

  def configure(m: ScalaTestMojo => Unit) = {
    val mojo = new ScalaTestMojo
    mojo.testOutputDirectory = new File(testOutputDirectory)
    mojo.reportsDirectory = new File(reportsDirectory)
    m(mojo)
    mojo.config
  }
}