package ro.redeul.google.go.lang.stubs;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.AdapterProcessor;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Function;
import com.intellij.util.Processor;
import com.intellij.util.containers.HashSet;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoTargetArch;
import ro.redeul.google.go.config.sdk.GoTargetOs;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageImportPath;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageName;
import ro.redeul.google.go.lang.psi.stubs.index.GoTypeName;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.util.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 8:04 PM
 */
public class GoNamesCache {

    private final Project project;

    private static GoSdkData sdkData;

    private static final ImmutableSet<String> goDefaultPackages = ImmutableSet.of(
            "archive",
            "bufio",
            "builtin",
            "bytes",
            "compress",
            "container",
            "crypto",
            "database",
            "debug",
            "encoding",
            "errors",
            "expvar",
            "flag",
            "fmt",
            "go",
            "hash",
            "html",
            "image",
            "index",
            "io",
            "log",
            "math",
            "mime",
            "net",
            "os",
            "path",
            "reflect",
            "regexp",
            "runtime",
            "sort",
            "strconv",
            "strings",
            "sync",
            "syscall",
            "testing",
            "text",
            "time",
            "unicode",
            "unsafe"
    );

    private static final Set<String> allOsNames = ImmutableSet.of(
            "_unix",
            "_linux",
            "_darwin",
            "_bsd",
            "_netbsd",
            "_freebsd",
            "_openbsd",
            "_dragonfly",
            "_windows",
            "_plan9"
    );
    private static final Set<String> linuxExcludeNames = ImmutableSet.copyOf(
            Sets.difference(allOsNames, ImmutableSet.of("_linux", "_unix")));
    private static final Set<String> windowsExcludeNames = ImmutableSet.copyOf(
            Sets.difference(allOsNames, ImmutableSet.of("_windows")));
    private static final Set<String> darwinExcludeNames = ImmutableSet.copyOf(
            Sets.difference(allOsNames, ImmutableSet.of("_darwin", "_unix")));
    private static final Set<String> freeBsdExcludeNames = ImmutableSet.copyOf(
            Sets.difference(allOsNames, ImmutableSet.of("_freebsd", "_bsd", "_unix")));

    private static final Set<String> allArchNames = ImmutableSet.of(
            "_386",
            "_amd64",
            "_arm"
    );
    private static final Set<String> _386ExcludeNames = ImmutableSet.copyOf(
            Sets.difference(allArchNames, ImmutableSet.of("_386")));
    private static final Set<String> _amd64ExcludeNames = ImmutableSet.copyOf(
            Sets.difference(allArchNames, ImmutableSet.of("_amd64")));
    private static final Set<String> _armExcludeNames = ImmutableSet.copyOf(
            Sets.difference(allArchNames, ImmutableSet.of("_arm")));

    @NotNull
    public static GoNamesCache getInstance(Project project) {
        // Not using a singleton here; calls should faily inexpensive and most of the data is now static;
        // only the call to ProjectRootManager.getInstance(project).getProjectSdk() does any work on construction.
        if (sdkData == null) {
            Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(project);
            if (sdk != null) {
                sdkData = (GoSdkData) sdk.getSdkAdditionalData();
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

    public Collection<String> getGoDefaultPackages() {
        return goDefaultPackages;
    }

    public boolean isGoDefaultPackage(String packageName) {
        return getGoDefaultPackages().contains(packageName.split("/")[0]);
    }

    public Collection<String> getAllPackages() {
        return getPackagesInScope(GlobalSearchScope.allScope(project));
    }

    Collection<String> getPackagesInScope(GlobalSearchScope scope) {

        StubIndex index = StubIndex.getInstance();

        Collection<String> keys = index.getAllKeys(GoPackageImportPath.KEY, project);

        Collection<String> packagesCollection = new ArrayList<String>();

        for (String key : keys) {
            Collection<GoFile> files = StubIndex.getElements(GoPackageImportPath.KEY, key, project, scope, GoFile.class);
            if (files != null && files.size() > 0) {
                packagesCollection.add(key);
            }
        }

        return packagesCollection;
    }

    public Collection<GoPackage> getPackagesByName(String name, Module module) {
        if ( module == null )
            return Collections.emptyList();

        StubIndex index = StubIndex.getInstance();

        final ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();

        CommonProcessors.CollectUniquesProcessor<GoPackage> uniquePackages = new CommonProcessors.CollectUniquesProcessor<GoPackage>();
        final GoPackages packages = GoPackages.getInstance(module);
        index.processElements(GoPackageName.KEY, name, project, GlobalSearchScope.allScope(project), GoFile.class,
                new AdapterProcessor<GoFile, String>(
                        new CommonProcessors.UniqueProcessor<String>(
                                new AdapterProcessor<String, GoPackage>(uniquePackages, new Function<String, GoPackage>() {
                                    @Override
                                    public GoPackage fun(String s) {
                                        return packages.getPackage(s, false);
                                    }
                                })),
                        new Function<GoFile, String>() {
                            @Override
                            public String fun(GoFile goFile) {
                                return goFile.getPackageImportPath();
                            }
                        }
                ));

        return uniquePackages.getResults();
    }

    public Collection<GoFile> getBuiltinPackageFiles() {
        return getFilesByPackageName("builtin");
    }

    public Collection<GoFile> getFilesByPackageName(String packageName) {
        Collection<GoFile> files = StubIndex.getElements(GoPackageName.KEY, packageName, project,
                GlobalSearchScope.allScope(project), GoFile.class);

        removeExcludedFiles(files);
        return files;
    }

    public Collection<GoFile> getFilesByPackageImportPath(@NotNull String importPath) {
        return getFilesByPackageImportPath(importPath, GlobalSearchScope.allScope(project));
    }

    public Collection<GoFile> getFilesByPackageImportPath(@NotNull String importPath,
                                                          @NotNull GlobalSearchScope scope) {
        Collection<GoFile> files = StubIndex.getElements(GoPackageImportPath.KEY, importPath, project, scope, GoFile.class);
        removeExcludedFiles(files);
        return files;
    }

    public boolean isPackageImportPathExist(String importPath) {
        Collection<GoFile> files = StubIndex.getElements(GoPackageImportPath.KEY,
                importPath, project, GlobalSearchScope.allScope(project), GoFile.class);
        return files.size() > 0;
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

        GlobalSearchScope scope = getSearchScope(includeNonProjectItems);
        Collection<NavigationItem> items = new ArrayList<NavigationItem>();
        for (GoTypeNameDeclaration type : StubIndex.getElements(GoTypeName.KEY, name,
                project, scope, GoTypeNameDeclaration.class)) {
            if (type instanceof NavigationItem) {
                items.add((NavigationItem) type);
            }
        }

        return items.toArray(new NavigationItem[items.size()]);
    }

    public Map<String, Collection<String>> getPackagesByImports() {
        final StubIndex stubIndex = StubIndex.getInstance();

        final Map<String, Collection<String>> results = new HashMap<String, Collection<String>>();

        stubIndex.processAllKeys(GoPackageImportPath.KEY, project, new Processor<String>() {
            @Override
            public boolean process(String importPath) {
                CommonProcessors.CollectUniquesProcessor<String> p =
                        new CommonProcessors.CollectUniquesProcessor<String>();

                stubIndex.processElements(GoPackageImportPath.KEY, importPath, project, getSearchScope(true), GoFile.class, new AdapterProcessor<GoFile, String>(p, new Function<GoFile, String>() {
                    @Override
                    public String fun(GoFile file) {
                        return file.getPackageName();
                    }
                }));

                results.put(importPath, p.getResults());
                return true;
            }
        });

        return results;
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
        Collection<String> excludeOsNames = getExcludeOsNames(getGoTargetOs());
        Collection<GoFile> osExcluded = new ArrayList<GoFile>();
        for (GoFile file : files) {
            String filename = file.getName();
            for (String excludeName : excludeOsNames) {
                if (filename.contains(excludeName)) {
                    osExcluded.add(file);
                }
            }
        }
        files.removeAll(osExcluded);

        Collection<String> excludeArchNames = getExcludeArchNames(getGoTargetArch());
        Collection<GoFile> archExcluded = new ArrayList<GoFile>();
        for (GoFile file : files) {
            String filename = file.getName();
            for (String excludeName : excludeArchNames) {
                if (filename.contains(excludeName)) {
                    archExcluded.add(file);
                }
            }
        }
        files.removeAll(archExcluded);
    }

    private static GoTargetOs getGoTargetOs() {
        GoTargetOs targetOs = GoTargetOs.fromString(System.getenv("GOOS"));
        if (targetOs == null && sdkData != null) {
            targetOs = sdkData.TARGET_OS;
        }
        return targetOs;
    }

    private static GoTargetArch getGoTargetArch() {
        GoTargetArch targetArch = GoTargetArch.fromString(System.getenv("GOARCH"));
        if (targetArch == null) {
            targetArch = sdkData.TARGET_ARCH;
        }
        return targetArch;
    }

    @VisibleForTesting
    static Set<String> getExcludeOsNames(GoTargetOs targetOs) {
        switch (targetOs) {
            case Windows:
                return windowsExcludeNames;
            case Linux:
                return linuxExcludeNames;
            case Darwin:
                return darwinExcludeNames;
            case FreeBsd:
                return freeBsdExcludeNames;
            default:
                return Collections.emptySet();
        }
    }

    @VisibleForTesting
    static Set<String> getExcludeArchNames(GoTargetArch targetArch) {
        switch (targetArch) {
            case _386:
                return _386ExcludeNames;
            case _amd64:
                return _amd64ExcludeNames;
            case _arm:
                return _armExcludeNames;
            default:
                return Collections.emptySet();
        }
    }

}
