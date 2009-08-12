package org.scalatest.tools.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Run tests using ScalaTest
 *
 * @author Jon-Anders Teigen
 * @goal test
 * @phase test
 */
public class TestMojo extends AbstractScalaTestMojo {


    /**
     * @parameter expression="${project.build.directory}/scalatest-reports"
     * @required
     */
    File reportsDirectory;

    /**
     * Set this to 'true' to skip running tests
     *
     * @parameter expression="${skipTests}"
     */
    boolean skipTests;

    /**
     * Enables and optionally configures file reporters for given files.
     * Either enable it for a given file with default configuration, or provide reporter configuration characters and the filename separated by whitespace
     * (Same as passing <code>-f<code> to ScalaTest)
     *
     * @parameter
     */
    String[] fileReporters;

    /**
     * configures the standard out reporter with the reporter configuration characters
     * (Same as passing <code>-o</code> to ScalaTest)
     *
     * @parameter expression="${stdout}"
     */
    String stdout;

    /**
     * enables and configures the standard error reporter with the reporter configuration characters
     * (Same as passing <code>-e</code> to ScalaTest)
     *
     * @parameter expression="${stderr}"
     * TODO: Er denne nødvendig?
     */
    String stderr;

    /**
     * enables and optionally configures reporterclasses.
     * Either list the fully qualified reporterclass, or provide reporter configuration characters and the fully qualified reporter class separated by whitespace
     * (Same as passing <code>-r</code> to ScalaTest)
     *
     * @parameter
     */
    String[] reporters;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipTests) {
            getLog().info("Skipping tests!");
        } else {
            if (fileReporters != null && !reportsDirectory.exists()) {
                if (!reportsDirectory.mkdirs()) {
                    throw new MojoExecutionException("Error creating " + reportsDirectory);
                }
            }
            runScalaTest();
            if (isFail()) {
                throw new MojoFailureException("There are test failures");
            }
        }
    }

    private boolean isFail() throws MojoExecutionException {
        try {
            Class<?> result = getClass("org.scalatest.tools.maven.Result");
            Method isFail = result.getMethod("isFail");
            return (Boolean) isFail.invoke(null);
        } catch (Exception e) {
            throw new MojoExecutionException("error", e);
        }
    }

    List<String> additionalConfiguration() {
        List<String> args = new ArrayList<String>();
        args.addAll(reporters());
        return args;
    }


    private List<String> reporters() {
        List<String> args = new ArrayList<String>();
        args.addAll(stdOutReporter());
        args.addAll(stdErrReporter());
        args.addAll(reporterClasses());
        args.addAll(fileReporters());
        return args;
    }

    private List<String> stdOutReporter() {
        List<String> args = new ArrayList<String>();
        if (stdout == null) {
            args.add("-o");
        } else {
            args.add("-o" + stdout);
        }
        return args;
    }

    private List<String> stdErrReporter() {
        List<String> args = new ArrayList<String>();
        if (stderr != null) {
            args.add("-e" + stderr);
        }
        return args;
    }

    private List<String> reporterClasses() {
        List<String> parts = new ArrayList<String>();
        if (reporters != null) {
            parts.addAll(Arrays.asList(reporters));
        }
        parts.add("org.scalatest.tools.maven.MavenReporter");

        List<String> args = new ArrayList<String>();
        for (String reporter : parts) {
            String[] split = reporter.split("\\s");
            if (split.length == 1) {
                args.add("-r");
                args.add(split[0]);
            } else {
                args.add("-r" + split[0]);
                args.add(split[1]);
            }
        }
        return args;
    }

    private List<String> fileReporters() {
        List<String> args = new ArrayList<String>();
        if (fileReporters != null) {
            for (String reporter : fileReporters) {
                String[] split = reporter.split("\\s");
                if (split.length == 1) {
                    args.add("-f");
                    args.add(new File(reportsDirectory, split[0]).getAbsolutePath());
                } else {
                    args.add("-f" + split[0]);
                    args.add(new File(reportsDirectory, split[1]).getAbsolutePath());
                }
            }
        }
        return args;
    }
}
