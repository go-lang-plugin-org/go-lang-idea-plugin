package ro.redeul.google.go.lang.stubs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.containers.HashSet;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageImportPath;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageName;
import ro.redeul.google.go.lang.psi.stubs.index.GoTypeName;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.sdk.GoSdkUtil;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 8:04 PM
 */
public class GoNamesCache {

    private final Project project;

    // TODO: Make this a singleton ?!
    @NotNull
    public static GoNamesCache getInstance(Project project) {
        return new GoNamesCache(project);
    }

    private GoNamesCache(Project project) {
        this.project = project;
    }

    public Collection<String> getProjectPackages() {
        return getPackagesInScope(GlobalSearchScope.projectScope(project));
    }

    public Collection<String> getSdkPackages() {
        return getPackagesInScope(GlobalSearchScope.notScope(
            GlobalSearchScope.projectScope(project)));
    }

    public Collection<String> getAllPackages() {
        return getPackagesInScope(GlobalSearchScope.allScope(project));
    }

    Collection<String> getPackagesInScope(GlobalSearchScope scope) {

        StubIndex index = StubIndex.getInstance();

        Collection<String> keys = index.getAllKeys(GoPackageImportPath.KEY,
                                                   project);

        Collection<String> packagesCollection = new ArrayList<>();

        for (String key : keys) {
            Collection<GoFile> files = index.get(GoPackageImportPath.KEY, key,
                                                 project, scope);
            if (files != null && files.size() > 0) {
                packagesCollection.add(key);
            }
        }

        return packagesCollection;
    }

    public Collection<GoFile> getBuiltinPackageFiles() {
        return getFilesByPackageName("builtin");
    }

    public Collection<GoFile> getFilesByPackageName(String packageName) {
        StubIndex index = StubIndex.getInstance();

        return index.get(GoPackageName.KEY, packageName, project,
                         GlobalSearchScope.allScope(project));
    }

    public Collection<GoFile> getFilesByPackageImportPath(@NotNull String importPath) {
        return getFilesByPackageImportPath(importPath, GlobalSearchScope.allScope(project));
    }

    public Collection<GoFile> getFilesByPackageImportPath(@NotNull String importPath,
                                                          @NotNull GlobalSearchScope scope) {
        StubIndex index = StubIndex.getInstance();

        return index.get(GoPackageImportPath.KEY, importPath, project, scope);
    }


    private GlobalSearchScope getSearchScope(boolean allScope) {
        return
            allScope
                ? GlobalSearchScope.allScope(project)
                : GlobalSearchScope.projectScope(project);
    }

    @NotNull
    public NavigationItem[] getTypesByName(@NotNull @NonNls String name,
                                           boolean includeNonProjectItems) {
        if (GoSdkUtil.getGoogleGoSdkForProject(project) == null) {
            return new NavigationItem[0];
        }

        StubIndex index = StubIndex.getInstance();
        GlobalSearchScope scope = getSearchScope(includeNonProjectItems);
        Collection<NavigationItem> items = new ArrayList<>();
        for (GoTypeNameDeclaration type : index.get(GoTypeName.KEY, name,
                                                    project, scope)) {
            if (type instanceof NavigationItem) {
                items.add((NavigationItem) type);
            }
        }

        return items.toArray(new NavigationItem[items.size()]);
    }

    @NotNull
    public String[] getAllTypeNames() {
        HashSet<String> classNames = new HashSet<>();
        getAllTypeNames(classNames);
        return classNames.toArray(new String[classNames.size()]);
    }

    public void getAllTypeNames(@NotNull Set<String> dest) {
        if (GoSdkUtil.getGoogleGoSdkForProject(project) == null) {
            return;
        }

        StubIndex index = StubIndex.getInstance();
        dest.addAll(index.getAllKeys(GoTypeName.KEY, project));
    }

    @NotNull
    public NavigationItem[] getFunctionsByName() {
        return new NavigationItem[0];
    }

    @NotNull
    public String[] getAllFunctionNames() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @NotNull
    public NavigationItem[] getVariablesByName() {
        return new NavigationItem[0];
    }

    @NotNull
    public String[] getAllVariableNames() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

}
