package org.scalatest.tools.maven;

import org.apache.maven.plugin.AbstractMojo;
import static org.scalatest.tools.maven.MojoUtils.*;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import static java.util.Collections.singletonList;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Jon-Anders Teigen
 * @requiresDependencyResolution test
 */
public abstract class AbstractScalaTestMojo extends AbstractMojo {
    /**
     * @parameter expression="${project.testClasspathElements}"
     * @required
     * @readOnly
     */
    List<String> testClasspathElements;

    /**
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     * @readOnly
     */
    File testOutputDirectory;

    /**
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readOnly
     */
    File outputDirectory;

    /**
     * Comma separated list of additional elements to be added
     * to the scalatest runpath. ${project.build.outputDirectory} and
     * ${project.build.testOutputDirectory} are included by default
     * @parameter expression="${runpath}"
     */
    String runpath;

    /**
     * Comma separated list of suites to be executed
     * @parameter expression="${suite}"
     */
    String suites;

    /**
     * Comma separated list of suites to include
     * @parameter expression="${include}"
     */
    String includes;

    /**
     * Comma separated list of suites to exclude
     * @parameter expression="${exclude}"
     */
    String excludes;

    /**
     * Comma separated list of cofiguration parameters to pass to scalatest.
     * The parameters must be on the format &lt;key&gt;=&lt;value&gt;. E.g <code>foo=bar,monkey=donkey</code>
     * @parameter expression="${config}"
     */
    String config;

    /**
     * Set to true to run suites concurrently
     * @parameter expression="${concurrent}"
     */
    boolean concurrent;

    /**
     * Comma separated list of members to execute
     * @parameter expression="${member}"
     */
    String members;

    /**
     * Comma separated list of wildcard suites to execute
     * @parameter expression="${wildcard}"
     */
    String wildcards;

    /**
     * Comma separated list of testNG xml files to execute
     * @parameter expression="${testNG}"
     */
    String testNG;

    /**
     * Comma separated list of JUnit suites/tests to execute
     * @parameter expression="${junit}"
     */
    String junit;

    boolean runScalaTest(String[] args) {
        print(args); // sideeffect!
        try {
            return (Boolean) run().invoke(null, new Object[]{args});
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            if(target instanceof RuntimeException){
                throw (RuntimeException)target;
            } else {
                throw new IllegalArgumentException(target);
            }
        }
    }

    // sideeffect!
    private void print(String[] args) {
        StringBuffer sb = new StringBuffer("org.scalatest.tools.Runner.run(");
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

    private Method run() {
        try {
            Class<?> runner = classLoader().loadClass("org.scalatest.tools.Runner");
            return runner.getMethod("run", String[].class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("scalatest is missing from classpath");
        }
    }

    private ClassLoader classLoader() {
        try {
            List<URL> urls = new ArrayList<URL>();
            for (String element : testClasspathElements) {
                File file = new File(element);
                if (file.isFile()) {
                    urls.add(file.toURI().toURL());
                }
            }
            URL[] u = urls.toArray(new URL[urls.size()]);
            return new URLClassLoader(u);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }


    List<String> sharedConfiguration() {
        return new ArrayList<String>() {{
            addAll(runpath());
            addAll(config());
            addAll(include());
            addAll(exclude());
            addAll(concurrent());
            addAll(suites());
            addAll(members());
            addAll(wildcards());
            addAll(testNG());
            addAll(junit());
        }};
    }

    private List<String> config() {
        List<String> c = new ArrayList<String>();
        for(String pair : splitOnComma(config)){
            c.add("-D"+pair);
        }
        return c;
    }

    private List<String> runpath() {
        return compoundArg("-p",
                outputDirectory.getAbsolutePath(),
                testOutputDirectory.getAbsolutePath(),
                runpath);
    }

    private List<String> include() {
        return compoundArg("-n", includes);
    }

    private List<String> exclude() {
        return compoundArg("-l", excludes);
    }

    private List<String> concurrent() {
        return concurrent ? singletonList("-c") : Collections.<String>emptyList();
    }

    private List<String> suites() {
        return suiteArg("-s", suites);
    }

    private List<String> members() {
        return suiteArg("-m", members);
    }

    private List<String> wildcards() {
        return suiteArg("-w", wildcards);
    }

    private List<String> testNG() {
        return suiteArg("-t", testNG);
    }

    private List<String> junit() {
        return suiteArg("-j", junit);
    }
}
