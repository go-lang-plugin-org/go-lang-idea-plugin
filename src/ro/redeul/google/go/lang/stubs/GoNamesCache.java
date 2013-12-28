package ro.redeul.google.go.lang.stubs;

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.containers.HashSet;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoTargetArch;
import ro.redeul.google.go.config.sdk.GoTargetOs;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageImportPath;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageName;
import ro.redeul.google.go.lang.psi.stubs.index.GoTypeName;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 8:04 PM
 */
public class GoNamesCache {

    private final Project project;

    private static GoSdkData sdkData;

    private static Collection<String> allOsNames;

    private static Collection<String> linuxExcludeNames;
    private static Collection<String> windowsExcludeNames;
    private static Collection<String> darwinExcludeNames;
    private static Collection<String> freeBsdExcludeNames;

    private static Collection<String> allArchNames;

    private static Collection<String> _386ExcludeNames;
    private static Collection<String> _amd64ExcludeNames;
    private static Collection<String> _armExcludeNames;

    // TODO: Make this a singleton ?!
    @NotNull
    public static GoNamesCache getInstance(Project project) {
        if (allOsNames == null) {
            initExcludeNames();
        }
        if (sdkData == null) {
            Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(project);
            if (sdk != null) {
                sdkData = (GoSdkData)sdk.getSdkAdditionalData();
            }
        }
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

        Collection<String> packagesCollection = new ArrayList<String>();

        for (String key : keys) {
            Collection<GoFile> files = index.safeGet(GoPackageImportPath.KEY, key,
                                                 project, scope, GoFile.class);
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

        Collection<GoFile> files = index.safeGet(GoPackageName.KEY, packageName, project,
                         GlobalSearchScope.allScope(project), GoFile.class);
        removeExcludedFiles(files);
        return files;
    }

    public Collection<GoFile> getFilesByPackageImportPath(@NotNull String importPath) {
        return getFilesByPackageImportPath(importPath, GlobalSearchScope.allScope(project));
    }

    public Collection<GoFile> getFilesByPackageImportPath(@NotNull String importPath,
                                                          @NotNull GlobalSearchScope scope) {
        StubIndex index = StubIndex.getInstance();

        Collection<GoFile> files = index.safeGet(GoPackageImportPath.KEY, importPath, project, scope, GoFile.class);
        removeExcludedFiles(files);
        return files;
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
        Collection<NavigationItem> items = new ArrayList<NavigationItem>();
        for (GoTypeNameDeclaration type : index.safeGet(GoTypeName.KEY, name,
                                                    project, scope, GoTypeNameDeclaration.class)) {
            if (type instanceof NavigationItem) {
                items.add((NavigationItem) type);
            }
        }

        return items.toArray(new NavigationItem[items.size()]);
    }

    @NotNull
    public String[] getAllTypeNames() {
        HashSet<String> classNames = new HashSet<String>();
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

    public void removeExcludedFiles(Collection<GoFile> files) {
        if (sdkData == null) {
            return;
        }
        String goos = System.getenv("GOOS");
        GoTargetOs targetOs = GoTargetOs.fromString(goos);
        if (targetOs == null) {
            targetOs = sdkData.TARGET_OS;
        }
        Collection<String> excludeOsNames;
        switch (targetOs) {
            case Windows:
                excludeOsNames = windowsExcludeNames;
                break;
            case Linux:
                excludeOsNames = linuxExcludeNames;
                break;
            case Darwin:
                excludeOsNames = darwinExcludeNames;
                break;
            case FreeBsd:
                excludeOsNames = freeBsdExcludeNames;
                break;
            default:
                excludeOsNames = new ArrayList<String>();
        }
        Collection<GoFile> osExcluded = new ArrayList<GoFile>();
        for (GoFile file:files) {
            String filename = file.getName();
            for (String excludeName:excludeOsNames) {
                if (filename.contains(excludeName)) {
                    osExcluded.add(file);
                }
            }
        }
        files.removeAll(osExcluded);

        String goarch = System.getenv("GOARCH");
        GoTargetArch targetArch = GoTargetArch.fromString(goarch);
        if (targetArch == null) {
            targetArch = sdkData.TARGET_ARCH;
        }
        Collection<String> excludeArchNames;
        switch (targetArch) {
            case _386:
                excludeArchNames = _386ExcludeNames;
                break;
            case _amd64:
                excludeArchNames = _amd64ExcludeNames;
                break;
            case _arm:
                excludeArchNames = _armExcludeNames;
                break;
            default:
                excludeArchNames = new ArrayList<String>();
        }

        Collection<GoFile> archExcluded = new ArrayList<GoFile>();
        for (GoFile file:files) {
            String filename = file.getName();
            for (String excludeName:excludeArchNames) {
                if (filename.contains(excludeName)) {
                    archExcluded.add(file);
                }
            }
        }
        files.removeAll(archExcluded);
    }

    private static void initExcludeNames() {
        allOsNames = new HashSet<String>();
        allOsNames.add("_unix");
        allOsNames.add("_linux");
        allOsNames.add("_darwin");
        allOsNames.add("_bsd");
        allOsNames.add("_netbsd");
        allOsNames.add("_freebsd");
        allOsNames.add("_openbsd");
        allOsNames.add("_dragonfly");
        allOsNames.add("_windows");
        allOsNames.add("_plan9");

        linuxExcludeNames = new HashSet<String>(allOsNames);
        linuxExcludeNames.remove("_linux");
        linuxExcludeNames.remove("_unix");

        windowsExcludeNames = new HashSet<String>(allOsNames);
        windowsExcludeNames.remove("_windows");

        darwinExcludeNames = new HashSet<String>(allOsNames);
        darwinExcludeNames.remove("_darwin");
        darwinExcludeNames.remove("_unix");

        freeBsdExcludeNames = new HashSet<String>(allOsNames);
        freeBsdExcludeNames.remove("_freebsd");
        freeBsdExcludeNames.remove("_bsd");
        freeBsdExcludeNames.remove("_unix");

        allArchNames = new HashSet<String>();
        allArchNames.add("_386");
        allArchNames.add("_amd64");
        allArchNames.add("_arm");

        _386ExcludeNames = new HashSet<String>(allArchNames);
        _386ExcludeNames.remove("_386");

        _amd64ExcludeNames = new HashSet<String>(allArchNames);
        _amd64ExcludeNames.remove("_amd64");

        _armExcludeNames = new HashSet<String>(allArchNames);
        _armExcludeNames.remove("_arm");
    }
}
