package ro.redeul.google.go.ide.contributor;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/23/11
 * Time: 2:00 PM
 */
public class GoGoToClassContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        return GoNamesCache.getInstance(project).getAllTypeNames();
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        return GoNamesCache.getInstance(project).getTypesByName(name, includeNonProjectItems);
    }
}
