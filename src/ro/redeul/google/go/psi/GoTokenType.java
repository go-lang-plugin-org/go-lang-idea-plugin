package ro.redeul.google.go.psi;

import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.GoLanguage;

/**
 * @author Mihai Toader &gt;mtoader@gmail.com&gt;
 */
public class GoTokenType extends IElementType {
    public GoTokenType(String debug) {
        super(debug, GoLanguage.INSTANCE);
    }
}
