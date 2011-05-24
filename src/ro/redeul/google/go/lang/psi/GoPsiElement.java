package ro.redeul.google.go.lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 10:24:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoPsiElement extends PsiElement {

    void accept(GoElementVisitor visitor);

    void acceptChildren(GoElementVisitor visitor);

}
