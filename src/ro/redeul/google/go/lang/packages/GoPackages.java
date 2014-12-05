package ro.redeul.google.go.lang.packages;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.reference.SoftReference;
import com.intellij.util.ConcurrencyUtil;
import com.intellij.util.containers.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.impl.GoPackageImpl;

import java.util.concurrent.ConcurrentMap;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getAs;

public class GoPackages implements ModuleComponent {

    private final Module myModule;

    private volatile SoftReference<ConcurrentMap<Pair<String, Boolean>, GoPackage>> packagesCache;

    public static final GoPackage C = new GoPackageImpl(null, null, null, false) {
        @Override
        public String getImportPath() {
            return "C";
        }

        @NotNull
        @Override
        public String getName() {
            return "C";
        }

        @Override
        public GoFile[] getFiles() {
            return GoFile.EMPTY_ARRAY;
        }

        @NotNull
        @Override
        public PsiDirectory[] getDirectories() {
            return PsiDirectory.EMPTY_ARRAY;
        }

        @NotNull
        @Override
        public PsiDirectory[] getDirectories(@NotNull GlobalSearchScope scope) {
            return getDirectories();
        }
    };

    public static final GoPackage Invalid = new GoPackageImpl(null, null, null, false) {
        @Override
        public String getImportPath() {
            return "";
        }

        @NotNull
        @Override
        public String getName() {
            return "";
        }

        @Override
        public GoFile[] getFiles() {
            return GoFile.EMPTY_ARRAY;
        }

        @NotNull
        @Override
        public PsiDirectory[] getDirectories() {
            return PsiDirectory.EMPTY_ARRAY;
        }

        @NotNull
        @Override
        public PsiDirectory[] getDirectories(@NotNull GlobalSearchScope scope) {
            return getDirectories();
        }
    };

    public GoPackages(Module module) {
        this.myModule = module;
    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void moduleAdded() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "GoPackages";
    }

    public static GoPackages getInstance(@NotNull Module module) {
        return module.getComponent(GoPackages.class);
    }

    /**
     * @param path a fully qualified path name (not a relative one) accessible from one of the source roots.
     * @return an object encapsulating a go package.
     */
    @NotNull
    public GoPackage getPackage(String path, boolean testPackage) {
        ConcurrentMap<Pair<String, Boolean>, GoPackage> cache = SoftReference.dereference(packagesCache);
        if (cache == null) {
            packagesCache = new SoftReference<ConcurrentMap<Pair<String, Boolean>, GoPackage>>(cache = new ConcurrentHashMap<Pair<String, Boolean>, GoPackage>());
        }

        GoPackage aPackage = cache.get(Pair.create(path, testPackage));
        if (aPackage != null) {
            return aPackage;
        }

        aPackage = resolvePackage(path, testPackage);
        return aPackage != null ? ConcurrencyUtil.cacheOrGet(cache, Pair.create(path, testPackage), aPackage) : Invalid;
    }

    private GoPackage resolvePackage(String path, boolean testPackage){
        if ( path.equals(C.getImportPath()))
            return C;

//        myProjectFileIndex = ProjectRootManager.getInstance(module.getProject()).getFileIndex();

        OrderEnumerator en = ModuleRootManager.getInstance(myModule).orderEntries().recursively();
        VirtualFile sourceRoots[] = en.sources().getRoots();

        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(myModule);
//        ProjectRootManagerEx rootManager = ProjectRootManagerEx.getInstanceEx(myProject);

//        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance();
//
//        VirtualFile sourceRoots[] = rootManager.getContentSourceRoots();

        for (VirtualFile sourceRoot : sourceRoots) {
            VirtualFile packagePath = sourceRoot.findFileByRelativePath(path);
            if (packagePath != null && packagePath.isDirectory()) {
                return new GoPackageImpl(packagePath, sourceRoot, PsiManager.getInstance(myModule.getProject()), testPackage);
            }
        }

//        Sdk projectSdk = rootManager.getProjectSdk();
//        if ( projectSdk == null )
//            return null;
//
//        VirtualFile[] sdkSourceRoots = projectSdk.getRootProvider().getFiles(OrderRootType.SOURCES);
//
//        for (VirtualFile sourceRoot : sdkSourceRoots) {
//            VirtualFile packagePath = sourceRoot.findFileByRelativePath(path);
//            if ( packagePath != null && packagePath.isDirectory()) {
//                return new GoPackageImpl(packagePath, sourceRoot, PsiManager.getInstance(myModule.getProject()), testPackage);
//            }
//        }
//
        return null;
    }

    public GoPackage getBuiltinPackage() {
        return getPackage("builtin", false);
    }

    @NotNull
    public static GoPackage getPackageFor(@Nullable PsiElement element) {
        if ( element == null || !(element.isValid()))
            return GoPackages.Invalid;

        Module module = ModuleUtil.findModuleForPsiElement(element);

        if ( module == null )
            return Invalid;

        GoPackages goPackages = getInstance(module);

        GoFile goFile = getAs(GoFile.class, element.getContainingFile());

        if ( goFile == null )
            return GoPackages.Invalid;

        return goPackages.getPackage(goFile.getPackageImportPath(), goFile.isTestFile());
    }

    @Nullable public static GoPackage getTargetPackageIfDifferent(GoPackage sourcePackage, PsiElement target) {
        if ( sourcePackage == null || target == null)
            return null;

        GoPackage targetPackage = getPackageFor(target);

        if (!sourcePackage.equals(targetPackage)) {
            Module targetModule = ModuleUtil.findModuleForPsiElement(target);
            if ( targetModule != null ) {
                GoPackage builtin = GoPackages.getInstance(targetModule).getBuiltinPackage();

                if (!builtin.equals(targetPackage))
                    return targetPackage;
            }
        }

        return null;
    }

    @Nullable public static GoPackage getTargetPackageIfDifferent(PsiElement source, PsiElement target) {
        return getTargetPackageIfDifferent(GoPackages.getPackageFor(source), target);
    }

    public GoPackage getInvalidPackage() {
        return Invalid;
    }
}
