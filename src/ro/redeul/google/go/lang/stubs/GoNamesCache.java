package ro.redeul.google.go.lang.stubs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.containers.HashSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageImportPath;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageName;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
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

    private String[] sdkPackages;

    public GoNamesCache(Project project) {
        this.project = project;
    }

    public @NotNull String[] getPackagesByName(@NotNull @NonNls String name, @NotNull GlobalSearchScope scope) {
        return getSdkPackages();
    }

    private String[] getSdkPackages() {
        if ( sdkPackages != null) {
            return sdkPackages;
        }

        StubIndex index = StubIndex.getInstance();

        Collection<String> keys = index.getAllKeys(GoPackageName.KEY, project);

        Set<VirtualFile> virtualFiles = new java.util.HashSet<VirtualFile>();

        for (String key : keys) {
            Collection<GoFile> files = index.get(
                    GoPackageName.KEY, key, project,
                    GlobalSearchScope.allScope(project).intersectWith(GlobalSearchScope.notScope(GlobalSearchScope.projectScope(project))));


            for (GoFile file : files) {
                PsiDirectory directory = file.getParent();

                if ( directory == null )
                    continue;

                PsiFile makefile = directory.findFile("Makefile");

                if ( makefile == null || makefile.getVirtualFile() == null )
                    continue;

                virtualFiles.add(makefile.getVirtualFile());
            }
        }

        Set<String> packages = new java.util.HashSet<String>();

        for (VirtualFile virtualFile : virtualFiles) {
            try {
                String content = new String(virtualFile.contentsToByteArray(), "UTF-8");

                Matcher matcher = RE_PACKAGE_TARGET.matcher(content);
                if ( matcher.find() ) {
                    packages.add(matcher.group(1));
                }
            } catch (IOException e) {
                //
            }
        }

        sdkPackages = packages.toArray(new String[packages.size()]);

        return sdkPackages;
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
