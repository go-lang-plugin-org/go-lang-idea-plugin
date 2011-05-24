package ro.redeul.google.go.ide.contributor;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectAndLibrariesScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.containers.ContainerUtil;
import ro.redeul.google.go.lang.psi.stubs.index.GoQualifiedTypeName;
import ro.redeul.google.go.lang.psi.stubs.index.GoTypeName;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.Collection;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/23/11
 * Time: 2:00 PM
 */
public class GoGoToClassContributor implements ChooseByNameContributor {
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        StubIndex index = StubIndex.getInstance();

        Collection<String> typeNames = index.getAllKeys(GoTypeName.KEY, project);

        return typeNames.toArray(new String[typeNames.size()]);
    }

    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        StubIndex index = StubIndex.getInstance();

        System.out.println("name: " + name);
        Collection<GoTypeNameDeclaration> typeNameDeclarations =
                index.get(
                        GoTypeName.KEY,
                        name, project,
                        includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project));

        if ( typeNameDeclarations.size() > 0 ) {
            int a = 10;
        }

        return typeNameDeclarations.toArray(new NavigationItem[typeNameDeclarations.size()]);
    }
}
