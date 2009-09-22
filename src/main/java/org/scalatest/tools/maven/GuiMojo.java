package org.scalatest.tools.maven;

import java.util.List;
import static java.util.Collections.singletonList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import static org.scalatest.tools.maven.MojoUtils.concat;

/**
 * @author Jon-Anders Teigen
 *
 * @goal gui
 */
public class GuiMojo extends AbstractScalaTestMojo {

    /**
     * Starts the ScalaTest GUI with the given configuration.
     * For more info on configuring reporters, see the scalatest documentation.
     *
     * @parameter expression="${gui}"
     */
    String gui;

    public void execute() throws MojoExecutionException, MojoFailureException {
        runScalaTest(configuration());
    }

    String[] configuration(){
        return concat(sharedConfiguration(), guiConfig());
    }

    private List<String> guiConfig() {
        return singletonList("-g" + (gui == null ? "" : gui));
    }
}
