package ro.redeul.google.go.ide.contributor;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GoGoToSymbolContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        GoNamesCache namesCache = GoNamesCache.getInstance(project);
        Set<String> names = new HashSet<String>();
        namesCache.getAllTypeNames(names);
        namesCache.getAllFunctionNames(names);
        namesCache.getAllVariableNames(names);
        return names.toArray(new String[names.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project,
                                           boolean includeNonProjectItems) {
        GoNamesCache namesCache = GoNamesCache.getInstance(project);
        List<NavigationItem> result = new ArrayList<NavigationItem>();
        Collections.addAll(result, namesCache.getTypesByName(name, includeNonProjectItems));
        Collections.addAll(result, namesCache.getFunctionsByName(name, includeNonProjectItems));
        Collections.addAll(result, namesCache.getVariablesByName(name, includeNonProjectItems));
        return result.toArray(new NavigationItem[result.size()]);
    }
}
