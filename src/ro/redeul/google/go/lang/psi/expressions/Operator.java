package ro.redeul.google.go.lang.psi.expressions;

import com.intellij.psi.tree.IElementType;

/**
 * <p/>
 * Created on Jan-09-2014 07:58
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public interface Operator {

    int precedence();

    IElementType tokenType();
}
