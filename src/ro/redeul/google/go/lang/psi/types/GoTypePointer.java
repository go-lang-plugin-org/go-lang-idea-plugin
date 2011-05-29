package ro.redeul.google.go.lang.psi.types;

import com.intellij.codeInsight.navigation.actions.GotoTypeDeclarationAction;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/26/11
 * Time: 11:52 PM
 */
public interface GoTypePointer extends GoType {

    GoType getTargetType();

}
