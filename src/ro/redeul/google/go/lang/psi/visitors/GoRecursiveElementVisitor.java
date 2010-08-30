package ro.redeul.google.go.lang.psi.visitors;

import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 8:26:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoRecursiveElementVisitor extends GoElementVisitor {

    @Override
    public void visitElement(GoPsiElement element) {
        element.acceptChildren(this);
    }

}
