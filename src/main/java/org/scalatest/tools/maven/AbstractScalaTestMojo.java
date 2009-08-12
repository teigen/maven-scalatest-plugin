package org.scalatest.tools.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.lang.reflect.InvocationTargetException;

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
     */
    File testOutputDirectory;

    /**
     * Adds additional elements to the runpath (Same as passing <code>-p</code>)
     *
     * @parameter
     */
    String[] additionalRunpaths;

    /**
     * Comma separated string of suites to run
     * (Same as passing <code>-s</code> to ScalaTest)
     *
     * @parameter expression="${suites}"
     */
    String suites;

    /**
     * Comma separated string of groups to include
     * (Same as passing <code>-n</code> to ScalaTest)
     *
     * @parameter expression="${includes}"
     */
    String includes;

    /**
     * Comma separated string of groups to exclude
     * (Same as passing <code>-x</code> to ScalaTest)
     *
     * @parameter expression="${excludes}"
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

    private ClassLoader classLoader;

    @SuppressWarnings("unchecked")
    void runScalaTest() throws MojoExecutionException, MojoFailureException {
        classLoader = classLoader();
        String[] configuration = configuration();
        print(configuration);
        invokeRunner(configuration);
    }

    abstract List<String> additionalConfiguration();

    String[] configuration() {
        List<String> config = new ArrayList<String>();
        config.addAll(additionalConfiguration());
        config.addAll(testNGs());
        config.addAll(wildcards());
        config.addAll(members());
        config.addAll(suites());
        config.addAll(concurrent());
        config.addAll(excludes());
        config.addAll(includes());
        config.addAll(properties());
        config.addAll(runpath());
        return config.toArray(new String[config.size()]);
    }

    Class<?> getClass(String clazz) throws MojoExecutionException {
        try {
            return classLoader.loadClass(clazz);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("ScalaTest not found!");
        }
    }

    private void invokeRunner(String[] args) throws MojoExecutionException {
        try {
            getClass("org.scalatest.tools.Runner").getMethod("main", String[].class).invoke(null, new Object[]{args});
        } catch (Exception e) {
            throw new MojoExecutionException("Error invoking scalatest", e);
        }
    }

    private void print(String[] args) {
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



    private List<String> testNGs() {
        return suiteArg("-t", commaSeparated(testNG));
    }

    private List<String> wildcards() {
        return suiteArg("-w", commaSeparated(wildcards));
    }

    private List<String> members() {
        return suiteArg("-m", commaSeparated(members));
    }

    private List<String> suites() {
        return suiteArg("-s", commaSeparated(suites));
    }

    private List<String> concurrent() {
        List<String> args = new ArrayList<String>();
        if (concurrent) {
            args.add("-c");
        }
        return args;
    }

    private List<String> excludes() {
        return compoundArg("-x", commaSeparated(excludes));
    }

    private List<String> includes() {
        return compoundArg("-n", commaSeparated(includes));
    }

    private List<String> properties() {
        List<String> props = new ArrayList<String>();
        if (properties != null) {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                props.add("-D" + entry.getKey() + "=" + entry.getValue());
            }
        }
        return props;
    }

    private List<String> runpath() {
        List<String> parts = new ArrayList<String>();
        parts.add(testOutputDirectory.getAbsolutePath());
        if (additionalRunpaths != null) {
            parts.addAll(Arrays.asList(additionalRunpaths));
        }
        return compoundArg("-p", parts);
    }



    private ClassLoader classLoader() {
        try {
            List<URL> urls = new ArrayList<URL>();
            for(String element : testClasspathElements){
                File file = new File(element);
                if(file.exists()){
                    urls.add(file.toURI().toURL());
                }
            }
            URL[] u = urls.toArray(new URL[urls.size()]);
            return new URLClassLoader(u, this.getClass().getClassLoader());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
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
        if (cs == null) {
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
}
