package org.scalatest.tools.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Run tests using ScalaTest
 *
 * @author Jon-Anders Teigen
 * @goal test
 * @phase test
 * @requiresDependencyResolution test
 */
public class ScalaTestMojo extends AbstractMojo {

    /**
     * @parameter expression="${project.testClasspathElements}"
     * @required
     * @readOnly
     */
    List<String> testClasspathElements;

    /**
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    File testOutputDirectory;

    /**
     * Adds additional elements to the runpath (Same as passing <code>-p</code>)
     *
     * @parameter
     */
    String[] additionalRunpaths;

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

    /**
     * Comma separated string of suites to run
     * (Same as passing <code>-s</code> to ScalaTest)
     *
     * @parameter
     */
    String suites;

    /**
     * Comma separated string of groups to include
     * (Same as passing <code>-n</code> to ScalaTest)
     *
     * @parameter
     */
    String includes;

    /**
     * Comma separated string of groups to exclude
     * (Same as passing <code>-x</code> to ScalaTest)
     *
     * @parameter
     */
    String excludes;

    /**
     * Specify user defined properties
     * (Same as passing <code>-D&lt;key&gt;=&lt;value&gt;</code> to ScalaTest)
     *
     * @parameter TODO: denne burde være String[] eller en String som parses, parser ut både keys og values: -Dproperties=trygve=kul,knoll=tott
     */
    Properties properties;

    /**
     * @parameter expression="${properties}"
     */
    String cmdProperties;

    /**
     * Set this to 'true' to run suites concurrently
     * (Same as passing <code>-c</code> to ScalaTest)
     *
     * @parameter expression="${concurrent}" default-value="false"
     */
    boolean concurrent;

    /**
     * Runs comma separated string of suites that are members of given packages
     * (Same as passing <code>-m</code> to ScalaTest)
     *
     * @parameter expression="${members}"
     */
    String members;

    /**
     * Runs comma separated string of suites that match given wildcards
     * (Same as passing <code>-w</code> to ScalaTest)
     *
     * @parameter expression="${wildcards}"
     */
    String wildcards;

    /**
     * Comma separated string of TestNG xml files to run
     * (Same as passing <code>-t</code> to ScalaTest)
     *
     * @parameter expression="${testNG}"
     * TODO: denne burde være String[] eller en String som parses
     */
    String testNG;

    /**
     * Starts the ScalaTest GUI.
     * <strong>This is only meant to be used as a command line parameter!</strong>
     * (Same as passing <code>-g</code> to ScalaTest)
     *
     * @parameter expression="${gui}"
     * TODO: dette burde være et eget mål
     */
    String gui;


    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipTests) {
            getLog().info("Skipping tests!");
        } else {
            try {
                if (fileReporters != null && !reportsDirectory.exists()) {
                    if (!reportsDirectory.mkdirs()) {
                        throw new MojoExecutionException("Error creating " + reportsDirectory);
                    }
                }
                ClassLoader loader = classLoader();

                Class<?> runner = loader.loadClass("org.scalatest.tools.Runner");
                Method main = runner.getMethod("main", new Class[]{String[].class});
                String[] config = config();
                print(config);
                main.invoke(null, new Object[]{config});

                Class<?> result = loader.loadClass("org.scalatest.tools.maven.Result");
                Method isFail = result.getMethod("isFail");
                boolean fail = (Boolean) isFail.invoke(null);
                if (fail) {
                    throw new MojoFailureException("There are test failures");
                }
            } catch (IllegalAccessException e) {
                throw new MojoExecutionException("Oh Noes!", e);
            } catch (NoSuchMethodException e) {
                throw new MojoExecutionException("Oh Noes!", e);
            } catch (MalformedURLException e) {
                throw new MojoExecutionException("Oh Noes!", e);
            } catch (InvocationTargetException e) {
                throw new MojoExecutionException("Oh Noes!", e);
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException("Oh Noes!", e);
            }
        }
    }

    void print(String[] args) {
        StringBuffer sb = new StringBuffer("org.scalatest.tools.Runner.main(");
        for (int i = 0; i < args.length; i++) {
            boolean ws = args[i].contains(" ");
            if (ws) {
                sb.append("\"");
            }
            sb.append(args[i]);
            if (ws) {
                sb.append("\"");
            }
            if (i + 1 < args.length) {
                sb.append(", ");
            }
        }
        sb.append(")");
        getLog().info(sb.toString());
    }

    String[] config() {
        List<String> args = new ArrayList<String>();
        args.addAll(properties());
        args.addAll(runpath());
        args.addAll(reporters());
        args.addAll(includes());
        args.addAll(excludes());
        args.addAll(concurrent());
        args.addAll(suites());
        args.addAll(members());
        args.addAll(wildcards());
        args.addAll(testNGs());

        String[] config = new String[args.size()];
        args.toArray(config);
        return config;
    }

    List<String> testNGs() {
        return suiteArg("-t", commaSeparated(testNG));
    }

    List<String> wildcards() {
        return suiteArg("-w", commaSeparated(wildcards));
    }

    List<String> members() {
        return suiteArg("-m", commaSeparated(members));
    }

    List<String> suites() {
        return suiteArg("-s", commaSeparated(suites));
    }

    List<String> concurrent() {
        List<String> args = new ArrayList<String>();
        if (concurrent) {
            args.add("-c");
        }
        return args;
    }

    List<String> excludes() {
        return compoundArg("-x", commaSeparated(excludes));
    }

    List<String> includes() {
        return compoundArg("-n", commaSeparated(includes));
    }

    List<String> reporters() {
        List<String> args = new ArrayList<String>();
        args.addAll(graphicReporter());
        args.addAll(stdOutReporter());
        args.addAll(stdErrReporter());
        args.addAll(reporterClasses());
        args.addAll(fileReporters());
        return args;
    }

    List<String> graphicReporter() {
        List<String> args = new ArrayList<String>();
        if (gui != null) {
            args.add("-g" + gui);
        }
        return args;
    }

    List<String> stdOutReporter() {
        List<String> args = new ArrayList<String>();
        if (stdout == null) {
            args.add("-o");
        } else {
            args.add("-o" + stdout);
        }
        return args;
    }

    List<String> stdErrReporter() {
        List<String> args = new ArrayList<String>();
        if (stderr != null) {
            args.add("-e" + stderr);
        }
        return args;
    }

    List<String> reporterClasses() {
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

    List<String> fileReporters() {
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

    List<String> properties() {
        List<String> props = new ArrayList<String>();
        if (properties != null) {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                props.add("-D" + entry.getKey() + "=" + entry.getValue());
            }
        }
        return props;
    }

    List<String> runpath() {
        List<String> parts = new ArrayList<String>();
        parts.add(testOutputDirectory.getAbsolutePath());
        if (additionalRunpaths != null) {
            parts.addAll(Arrays.asList(additionalRunpaths));
        }
        return compoundArg("-p", parts);
    }

    static List<String> compoundArg(String name, List<String> params) {
        List<String> list = new ArrayList<String>();
        if (params != null) {
            list.add(name);
            String prefix = "";
            String a = "";
            for (String param : params) {
                a += prefix;
                a += param;
                prefix = " ";
            }
            list.add(a);
        }
        return list;
    }

    static List<String> suiteArg(String name, List<String> params) {
        List<String> list = new ArrayList<String>();
        if (params != null) {
            for (String param : params) {
                list.add(name);
                list.add(param);
            }
        }
        return list;
    }

    static List<String> commaSeparated(String cs) {
        if(cs == null){
          return null;
        } else {
            List<String> args = new ArrayList<String>();
            String[] split = cs.split(",");
            for (String arg : split) {
                args.add(arg.trim());
            }
            return args;
        }
    }

    ClassLoader classLoader() throws MalformedURLException {
        URL[] urls = new URL[testClasspathElements.size() + 1];
        for (int i = 0; i < testClasspathElements.size(); i++) {
            urls[i] = new File(testClasspathElements.get(i)).toURI().toURL();
        }
        return new URLClassLoader(urls, this.getClass().getClassLoader());
    }
}
