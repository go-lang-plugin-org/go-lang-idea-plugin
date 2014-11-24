package ro.redeul.google.go.lang.packages;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ex.ProjectRootManagerEx;
import com.intellij.openapi.util.NotNullLazyKey;
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

public class GoPackages extends AbstractProjectComponent {

    private static final NotNullLazyKey<GoPackages, Project> INSTANCE_KEY = ServiceManager.createLazyKey(GoPackages.class);

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

    public GoPackages(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "GoPackages";
    }

    public static GoPackages getInstance(Project project) {
        return INSTANCE_KEY.getValue(project);
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

        VirtualFile sourceRoots[] = ProjectRootManagerEx.getInstanceEx(myProject).getContentSourceRoots();

        for (VirtualFile sourceRoot : sourceRoots) {
            VirtualFile packagePath = sourceRoot.findFileByRelativePath(path);
            if (packagePath != null && packagePath.isDirectory()) {
                return new GoPackageImpl(packagePath, sourceRoot, PsiManager.getInstance(myProject), testPackage);
            }
        }

        Sdk projectSdk = ProjectRootManagerEx.getInstanceEx(myProject).getProjectSdk();
        if ( projectSdk == null )
            return null;

        VirtualFile[] sdkSourceRoots = projectSdk.getRootProvider().getFiles(OrderRootType.SOURCES);

        for (VirtualFile sourceRoot : sdkSourceRoots) {
            VirtualFile packagePath = sourceRoot.findFileByRelativePath(path);
            if ( packagePath != null && packagePath.isDirectory()) {
                return new GoPackageImpl(packagePath, sourceRoot, PsiManager.getInstance(myProject), testPackage);
            }
        }

        return null;
    }

    public GoPackage getBuiltinPackage() {
        return getPackage("builtin", false);
    }

    @NotNull
    public static GoPackage getPackageFor(@Nullable PsiElement element) {
        if ( element == null )
            return GoPackages.Invalid;

        GoPackages goPackages = getInstance(element.getProject());

        GoFile goFile = getAs(GoFile.class, element.getContainingFile());

        if ( goFile == null )
            return GoPackages.Invalid;

        return goPackages.getPackage(goFile.getPackageImportPath(), goFile.isTestFile());
    }

    @Nullable public static GoPackage getTargetPackageIfDifferent(GoPackage sourcePackage, PsiElement target) {
        if ( sourcePackage == null || target == null)
            return null;

        GoPackage targetPackage = getPackageFor(target);

        if (targetPackage != null && !sourcePackage.equals(targetPackage)) {
            GoPackage builtin = GoPackages.getInstance(target.getProject()).getBuiltinPackage();

            if ( !builtin.equals(targetPackage))
                return targetPackage;
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
