package ro.redeul.google.go.services;

import com.intellij.ProjectTopics;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.util.RecursionGuard;
import com.intellij.openapi.util.RecursionManager;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.util.ConcurrencyUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ConcurrentWeakHashMap;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.typing.GoType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/15/11
 * Time: 7:50 AM
 */
public class GoPsiManager {

    private static final Logger LOG = Logger.getInstance("ro.redeul.google.go.services.GoPsiManager");

    private final ConcurrentMap<GoPsiElement, GoType[]> myCalculatedTypes =
            new ConcurrentWeakHashMap<GoPsiElement, GoType[]>();

    private final ConcurrentMap<GoPsiElement, GoType> myComputedType =
            new ConcurrentHashMap<GoPsiElement, GoType>();

    private static final RecursionGuard ourGuard =
            RecursionManager.createGuard("goPsiManager");

    private static final RecursionGuard outSecondGuard =
            RecursionManager.createGuard("goPsiManagerSecond");

    private GoPsiManager(Project project) {

        ((PsiManagerEx) PsiManager.getInstance(project)).registerRunnableToRunOnAnyChange(new Runnable() {
            public void run() {
                myCalculatedTypes.clear();
                myComputedType.clear();
            }
        });
        ((PsiManagerEx) PsiManager.getInstance(project)).registerRunnableToRunOnChange(new Runnable() {
            public void run() {
                myComputedType.clear();
                myCalculatedTypes.clear();
            }
        });

        project.getMessageBus().connect().subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
            public void beforeRootsChange(ModuleRootEvent event) {
            }

            public void rootsChanged(ModuleRootEvent event) {
                myCalculatedTypes.clear();
                myComputedType.clear();
            }
        });
    }

    public static GoPsiManager getInstance(Project project) {
        return ServiceManager.getService(project, GoPsiManager.class);
    }

    @NotNull
    public <T extends GoPsiElement, V> GoType getOrCompute(T element, Function<T, GoType> valueCalculator) {
        GoType type = myComputedType.get(element);
        if (type == null) {
            RecursionGuard.StackStamp stamp = outSecondGuard.markStack();
            type = valueCalculator.fun(element);
            if ( type != null ) {
                if (stamp.mayCacheNow()) {
                    type = ConcurrencyUtil.cacheOrGet(myComputedType, element, type);
                } else {
                    final GoType alreadyInferred = myComputedType.get(element);
                    if (alreadyInferred != null) {
                        type = alreadyInferred;
                    }
                }
            }
        }

        return type != null ? type : GoType.Unknown;
    }

    @NotNull
    public <T extends GoPsiElement> GoType[] getType(T element, Function<T, GoType[]> calculator) {
        GoType[] types = myCalculatedTypes.get(element);
//        GoType[] types = null;
        if (types == null) {
            RecursionGuard.StackStamp stamp = ourGuard.markStack();
            types = calculator.fun(element);
            if (types == null) {
                types = GoType.EMPTY_ARRAY;
            }
            if (stamp.mayCacheNow()) {
                types = ConcurrencyUtil.cacheOrGet(myCalculatedTypes, element, types);
            } else {
                final GoType[] alreadyInferred = myCalculatedTypes.get(element);
                if (alreadyInferred != null) {
                    types = alreadyInferred;
                }
            }
        }

        return types;
    }
}
