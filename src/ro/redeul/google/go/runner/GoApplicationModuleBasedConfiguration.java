package ro.redeul.google.go.runner;

import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 27, 2010
 * Time: 11:02:54 AM
 */
public class GoApplicationModuleBasedConfiguration extends RunConfigurationModule {
    public GoApplicationModuleBasedConfiguration(Project project) {
        super(project);
    }
}
