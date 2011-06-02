package ro.redeul.google.go.lang.psi.expressions;

import com.intellij.codeInsight.navigation.actions.GotoTypeDeclarationAction;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiType;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.types.GoType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 10:55 PM
 */
public interface GoExpr extends GoPsiElement {

    GoType getType();

}
