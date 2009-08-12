package org.scalatest.tools.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

/**
 * @goal gui
 */
public class GuiMojo extends AbstractScalaTestMojo {
    /**
     * Starts the ScalaTest GUI.
     * (Same as passing <code>-g</code> to ScalaTest)
     *
     * @parameter expression="${gui}"
     */
    String gui;

    public void execute() throws MojoExecutionException, MojoFailureException {
        runScalaTest();
    }

    List<String> additionalConfiguration() {
        return Collections.singletonList("-g"+ (gui == null ? "" : gui) );
    }
}
