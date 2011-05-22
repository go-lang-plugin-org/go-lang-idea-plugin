package ro.redeul.google.go.lang.psi.processors;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.KeyWithDefaultValue;
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
                .put(IsOriginalPackage, true);
    }

    public static ResolveState imported(String importName, String visibleName)
    {
        return ResolveState.initial()
                .put(IsOriginalFile, false)
                .put(IsOriginalPackage, false)
                .put(PackageName, importName)
                .put(VisiblePackageName, visibleName);
    }

    public static Key<Boolean> IsOriginalFile = new Key<Boolean>("IsOriginalFile");

    public static Key<Boolean> IsOriginalPackage = new Key<Boolean>("IsOriginalPackage");

    public static Key<String> VisiblePackageName = new Key<String>("VisiblePackageName");

    public static Key<String> PackageName = new Key<String>("PackageName");
}
