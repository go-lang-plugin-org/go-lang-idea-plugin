package ro.redeul.google.go.lang.psi.patterns;

import com.intellij.patterns.ElementPattern;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class GoElementPatterns {

    public static final ElementPattern<GoLiteralIdentifier> CONST_DECLARATION =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoConstDeclaration.class)
                    .withParent(
                        psiElement(GoConstDeclarations.class)
                            .withParent(psiElement(GoFile.class))));

    public static final ElementPattern<GoLiteralIdentifier> VAR_DECLARATION =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoVarDeclaration.class)
                    .withParent(
                        psiElement(GoVarDeclarations.class)
                            .withParent(psiElement(GoFile.class))));

}
