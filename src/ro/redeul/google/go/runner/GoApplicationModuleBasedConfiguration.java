package ro.redeul.google.go.runner;

import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 27, 2010
 * Time: 11:02:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoApplicationModuleBasedConfiguration extends RunConfigurationModule {
    public GoApplicationModuleBasedConfiguration(Project project) {
        super(project);
    }
}
