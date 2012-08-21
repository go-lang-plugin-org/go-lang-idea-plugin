package ro.redeul.google.go;

import java.lang.ref.Reference;
import java.util.ResourceBundle;

import com.intellij.CommonBundle;
import com.intellij.reference.SoftReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

public class GoBundle {
    private static Reference<ResourceBundle> ourBundle;

    @NonNls
    private static final String BUNDLE = "ro.redeul.google.go.GoBundle";

    public static String message(
        @PropertyKey(resourceBundle = BUNDLE) String key,
        Object ... params) {
        return CommonBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = null;

        if (ourBundle != null) bundle = ourBundle.get();

        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            ourBundle = new SoftReference<ResourceBundle>(bundle);
        }
        return bundle;
    }
}
