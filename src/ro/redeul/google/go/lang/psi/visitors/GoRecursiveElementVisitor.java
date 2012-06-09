package ro.redeul.google.go.lang.psi.visitors;

import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Aug 30, 2010
 */
public class GoRecursiveElementVisitor extends GoElementVisitor {

    @Override
    public void visitElement(GoPsiElement element) {
        if (element != null) {
            element.acceptChildren(this);
        }
    }
}
