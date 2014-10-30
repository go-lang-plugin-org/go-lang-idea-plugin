package ro.redeul.google.go.lang.psi.processors;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.KeyWithDefaultValue;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/20/11
 * Time: 3:40 AM
 */
public class ResolveStates {


    public interface Key {

        public static final KeyWithDefaultValue<Boolean> IsOriginalFile =
                KeyWithDefaultValue.create("IsOriginalFile", true);

        public static final KeyWithDefaultValue<Boolean> IsOriginalPackage =
                KeyWithDefaultValue.create("IsOriginalPackage", true);

        public static final KeyWithDefaultValue<Boolean> JustExports =
                KeyWithDefaultValue.create("JustExports", false);

        public static final KeyWithDefaultValue<Boolean> IsPackageBuiltin =
                KeyWithDefaultValue.create("InsideBuiltinPackage", false);

    }

    public static ResolveState builtins() {
        return ResolveState.initial()
                .put(Key.IsOriginalFile, false)
                .put(Key.IsOriginalPackage, false)
                .put(Key.IsPackageBuiltin, true);
    }

    public static ResolveState initial() {
        return ResolveState.initial();
    }

    public static ResolveState currentPackage() {
        return ResolveState.initial()
                .put(Key.IsOriginalFile, false)
                .put(Key.IsOriginalPackage, true)
                .put(Key.IsPackageBuiltin, false);
    }

    public static ResolveState variables() {
        return ResolveState.initial();
//                .put(ResolvingVariables, true);
    }


    public static ResolveState packageExports() {
        return ResolveState.initial()
                .put(Key.IsOriginalFile, false)
                .put(Key.IsOriginalPackage, false)
                .put(Key.JustExports, true);
    }

    @NotNull
    public static <T> T get(ResolveState state, KeyWithDefaultValue<T> key) {
        T val = state.get(key);
        return val == null ? key.getDefaultValue() : val;
    }
}
