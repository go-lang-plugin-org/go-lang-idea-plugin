package ro.redeul.google.go.lang.stubs;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.containers.HashSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageImportPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 8:04 PM
 */
public class GoNamesCache extends PsiShortNamesCache {

    private final Project project;

    private final static Pattern RE_PACKAGE_TARGET = Pattern.compile("^TARG=([^\\s]+)\\s*$", Pattern.MULTILINE);

    public GoNamesCache(Project project) {
        this.project = project;
    }

    public Collection<String> getProjectPackages() {
        return getGoPackagesInScope(GlobalSearchScope.projectScope(project));
    }

    public Collection<String> getSdkPackages() {
        return getGoPackagesInScope(GlobalSearchScope.notScope(GlobalSearchScope.projectScope(project)));
    }

    private Collection<String> getGoPackagesInScope(GlobalSearchScope scope) {

        StubIndex index = StubIndex.getInstance();

        Collection<String> keys = index.getAllKeys(GoPackageImportPath.KEY, project);

        Collection<String> packagesCollection = new ArrayList<String>();

        for (String key : keys) {
            Collection<GoFile> files = index.get(GoPackageImportPath.KEY, key, project, scope);
            if ( files != null && files.size() > 0 ) {
                packagesCollection.add(key);
            }
        }

        return packagesCollection;
    }

    public Collection<GoFile> getFilesByPackageName(String packageName) {
        StubIndex index = StubIndex.getInstance();

        return index.get(GoPackageImportPath.KEY, packageName, project, GlobalSearchScope.allScope(project));
    }


    @NotNull
    @Override
    public PsiClass[] getClassesByName(@NotNull @NonNls String name, @NotNull GlobalSearchScope scope) {
        return new PsiClass[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public String[] getAllClassNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void getAllClassNames(@NotNull HashSet<String> dest) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public PsiMethod[] getMethodsByName(@NonNls @NotNull String name, @NotNull GlobalSearchScope scope) {
        return new PsiMethod[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public PsiMethod[] getMethodsByNameIfNotMoreThan(@NonNls @NotNull String name, @NotNull GlobalSearchScope scope, int maxCount) {
        return new PsiMethod[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public String[] getAllMethodNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void getAllMethodNames(@NotNull HashSet<String> set) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public PsiField[] getFieldsByName(@NotNull @NonNls String name, @NotNull GlobalSearchScope scope) {
        return new PsiField[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public String[] getAllFieldNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void getAllFieldNames(@NotNull HashSet<String> set) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resolveGoPackageName(String importPackageName) {
    }
}
