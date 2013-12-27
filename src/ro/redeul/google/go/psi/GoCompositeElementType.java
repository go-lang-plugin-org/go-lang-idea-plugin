package ro.redeul.google.go.psi;

import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.GoLanguage;

/**
 * @author Mihai Toader &lt;mtoader@gmail.com&gt;
 */
public class GoCompositeElementType extends IElementType {
    public GoCompositeElementType(String debug) {
        super(debug, GoLanguage.INSTANCE);
    }
}
