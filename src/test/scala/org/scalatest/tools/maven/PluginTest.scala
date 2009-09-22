package org.scalatest.tools.maven

import java.io.File
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnit3Suite
import java.util.ArrayList
import org.scalatest.BeforeAndAfter

/**
 * @author Jon -Anders Teigen
 */
class PluginTest extends JUnit3Suite with ShouldMatchers with PluginMatchers with BeforeAndAfter {
  val tmpDir = new File(System.getProperty("java.io.tmpdir"))
  val reportsDirectory = new File(tmpDir, "reportsDirectory")
  val baseDir = new File(tmpDir, "basedir");
  val testOutputDirectory = new File(reportsDirectory, "testOutputDirectory").getAbsolutePath
  val outputDirectory = new File(reportsDirectory, "outputDirectory").getAbsolutePath

  override def afterAll {
    def delete(it: File) {
      if (it.isFile) {
        it.delete()
      } else {
        for (d <- it.listFiles)
          delete(d)
      }
    }
    delete(reportsDirectory)
    delete(baseDir);
  }

  def jlist(a: String*) = new ArrayList[String]() {for (e <- a) this.add(e)}

  def comma(a: String*) = a mkString ","

  def configure(m: TestMojo => Unit) = {
    val mojo = new TestMojo
    mojo.reportsDirectory = reportsDirectory
    mojo.testOutputDirectory = new File(testOutputDirectory)
    mojo.outputDirectory = new File(outputDirectory)
    m(mojo)
    mojo.configuration
  }

  def testDefault {
    val config = configure(_ => ())
    config should contain("-o")
    config should containCompoundArgs("-p", outputDirectory, testOutputDirectory)
    config should have length (3)
  }

  def testConfigs {
    val config = configure(_.config = comma("foo=bar", "monkey=donkey"))
    config should contain("-Dfoo=bar")
    config should contain("-Dmonkey=donkey")
  }

  def testRunpath {
    configure(_.runpath = comma("http://foo.com/my.jar", "/some/where")) should containCompoundArgs("-p", outputDirectory, testOutputDirectory, "http://foo.com/my.jar", "/some/where")
  }

  def testFilereporters {
    val config = configure(_.filereports = comma("foo.txt", "YZT some.txt"))
    config should containSlice("-f", new File(reportsDirectory, "foo.txt").getAbsolutePath)
    config should containSlice("-fYZT", new File(reportsDirectory, "some.txt").getAbsolutePath)
  }

  def testReporters {
    val config = configure(_.reporters = comma("YZT org.my.reporter", "org.your.reporter"))
    config should containSlice("-rYZT", "org.my.reporter")
    config should containSlice("-r", "org.your.reporter")
  }

  def testXmlReporters {
    val config = configure(_.xmlreports = comma("some/foo.xml", "XYZ other.xml"))
    config should containSlice("-u", new File(reportsDirectory, "some/foo.xml").getAbsolutePath)
    config should containSlice("-uXYZ", new File(reportsDirectory, "other.xml").getAbsolutePath)
  }

  def testHtmlReporters {
    val config = configure(_.htmlreports = comma("ABC that/report.html", "all.html"))
    config should containSlice("-hABC", new File(reportsDirectory, "that/report.html").getAbsolutePath)
    config should containSlice("-h", new File(reportsDirectory, "all.html").getAbsolutePath)
  }

  def testStdOutReporter {
    configure(_.stdout = "GUP") should contain("-oGUP")
  }

  def testStdErrReporter {
    configure(_.stderr = "BIS") should contain("-eBIS")
  }

  def testIncludes {
    configure(_.includes = comma("a", "b", "c")) should containCompoundArgs("-n", "a", "b", "c")
  }

  def testExcludes {
    configure(_.excludes = comma("a", "b", "c")) should containCompoundArgs("-l", "a", "b", "c")
  }

  def testConcurrent {
    configure(_.concurrent = true) should contain("-c")
    configure(_.concurrent = false) should not contain ("-c")
  }

  def testSuites {
    configure(_.suites = comma("a", "b", "c")) should containSuiteArgs("-s", "a", "b", "c")
  }

  def testMembers {
    configure(_.members = comma("a", "b", "c")) should containSuiteArgs("-m", "a", "b", "c")
  }

  def testWildcards {
    configure(_.wildcards = comma("a", "b", "c")) should containSuiteArgs("-w", "a", "b", "c")
  }

  def testTestNGs {
    configure(_.testNG = comma("a", "b", "c")) should containSuiteArgs("-t", "a", "b", "c")
  }

  def testJUnits {
    configure(_.junit = comma("a", "b", "c")) should containSuiteArgs("-j", "a", "b", "c")
  }

  def testGui {
    val mojo = new GuiMojo
    mojo.testOutputDirectory = new File(testOutputDirectory)
    mojo.outputDirectory = new File(outputDirectory)
    mojo.configuration should contain("-g")

    mojo.gui = "BIS"
    mojo.configuration should contain("-gBIS")
    mojo.configuration should contain("-gBIS")
  }

  def testMojoConcat {
    MojoUtils.concat(jlist("a", "b", "c"), jlist("1", "2", "3")) should be(Array("a", "b", "c", "1", "2", "3"))
  }

  def testMojoSuiteArg {
    MojoUtils.suiteArg("-a", comma("a", "b", "c")) should be(jlist("-a", "a", "-a", "b", "-a", "c"))
    MojoUtils.suiteArg("-a", null) should be(jlist())
  }

  def testMojoCompundArg {
    MojoUtils.compoundArg("-a", comma("a", "b", "c")) should be(jlist("-a", "a b c"))
    MojoUtils.compoundArg("-a", null.asInstanceOf[String]) should be(jlist())
  }
}