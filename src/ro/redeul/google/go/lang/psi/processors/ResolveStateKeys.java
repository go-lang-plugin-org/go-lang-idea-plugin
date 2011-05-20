package ro.redeul.google.go.lang.psi.processors;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.KeyWithDefaultValue;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/20/11
 * Time: 3:40 AM
 */
public class ResolveStateKeys {
    public static com.intellij.openapi.util.Key<Boolean> ProcessOnlyLocalDeclarations = new KeyWithDefaultValue<Boolean>("ProcessOnlyLocalDeclarations") {
        @Override
        public Boolean getDefaultValue() {
            return Boolean.FALSE;
        }
    };
}
