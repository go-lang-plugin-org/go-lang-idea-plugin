package ro.redeul.google.go.lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:24:11 PM
 */
public interface GoPsiElement extends PsiElement {

    void setUseScope(SearchScope scope);

    void accept(GoElementVisitor visitor);

    void acceptChildren(GoElementVisitor visitor);

}
