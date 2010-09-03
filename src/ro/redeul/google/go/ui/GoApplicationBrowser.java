package ro.redeul.google.go.ui;

import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.junit2.configuration.ClassBrowser;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import ro.redeul.google.go.GoFileType;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 3:30:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoApplicationBrowser extends ClassBrowser {
    public GoApplicationBrowser(final Project project) {
        super(project, ExecutionBundle.message("choose.test.class.dialog.title"));
    }

    protected void onClassChoosen(final PsiClass psiClass) {
//          setPackage(JUnitUtil.getContainingPackage(psiClass));
    }

    protected PsiClass findClass(final String className) {
//          return getModuleSelector().findClass(className);
        return null;
    }

    protected TreeClassChooser.ClassFilterWithScope getFilter() throws NoFilterException {
//          final ConfigurationModuleSelector moduleSelector = getModuleSelector();
//          final Module module = moduleSelector.getModule();
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();
//          if (module == null) {
//            throw NoFilterException.moduleDoesntExist(moduleSelector);
//          }
        final TreeClassChooser.ClassFilterWithScope classFilter;

        return new GoApplicationTreeClassFilter(getProject());
//          try {
//            final JUnitConfiguration configurationCopy = new JUnitConfiguration(ExecutionBundle.message("default.junit.configuration.name"), getProject(), JUnitConfigurationType.getInstance().getConfigurationFactories()[0]);
//            applyEditorTo(configurationCopy);
//            classFilter = TestClassFilter.create(configurationCopy.getTestObject().getSourceScope(), configurationCopy.getConfigurationModule().getModule());
//          }
//          catch (JUnitUtil.NoJUnitException e) {
//            throw NoFilterException.noJUnitInModule(module);
//          }
//          return classFilter;
//        }
    }

    static class GoApplicationTreeClassFilter implements TreeClassChooser.ClassFilterWithScope {
        Project project;

        GoApplicationTreeClassFilter(Project project) {
            this.project = project;
        }

        public GlobalSearchScope getScope() {
            return GlobalSearchScope.getScopeRestrictedByFileTypes(
                    GlobalSearchScope.projectScope(project),
                    GoFileType.GO_FILE_TYPE);
        }

        public boolean isAccepted(PsiClass aClass) {
            return true;
        }
    }

}
