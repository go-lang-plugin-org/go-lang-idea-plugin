package ro.redeul.google.go.lang.psi.processors;

import com.intellij.openapi.util.Key;
import com.intellij.psi.ResolveState;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/20/11
 * Time: 3:40 AM
 */
public class GoResolveStates {

    public static ResolveState initial()
    {
        return ResolveState.initial()
                .put(IsOriginalFile, true)
                .put(IsOriginalPackage, true)
                .put(ResolvingVariables, false);
    }

    public static ResolveState imported(String importName, String visibleName)
    {
        return ResolveState.initial()
                .put(IsOriginalFile, false)
                .put(IsOriginalPackage, false)
                .put(PackageName, importName)
                .put(ResolvingVariables, false)
                .put(VisiblePackageName, visibleName);
    }

    public static ResolveState variables() {
        return ResolveState.initial()
                .put(ResolvingVariables, true)
                .put(IsOriginalFile, true)
                .put(IsOriginalPackage, true);
    }

    private static final Key<Boolean> ResolvingVariables = new Key<Boolean>("ResolvingVariables");
    public static final Key<Boolean> IsOriginalFile = new Key<Boolean>("IsOriginalFile");
    public static final Key<Boolean> IsOriginalPackage = new Key<Boolean>("IsOriginalPackage");

    public static final Key<String> VisiblePackageName = new Key<String>("VisiblePackageName");

    public static final Key<String> PackageName = new Key<String>("PackageName");
}
