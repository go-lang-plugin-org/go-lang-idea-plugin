package ro.redeul.google.go.lang.psi.patterns;

import com.intellij.patterns.ElementPattern;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

public class GoElementPatterns {

    public static final ElementPattern<GoLiteralIdentifier> GLOBAL_CONST_DECL =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoConstDeclaration.class)
                    .withParent(
                        psiElement(GoConstDeclarations.class)
                            .withParent(psiElement(GoFile.class))));

    public static final ElementPattern<GoLiteralIdentifier> GLOBAL_VAR_DECL =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoVarDeclaration.class)
                    .withParent(
                        psiElement(GoVarDeclarations.class)
                            .withParent(psiElement(GoFile.class))));

    public static final ElementPattern<GoLiteralIdentifier> VAR_DECLARATION =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                or(
                    psiElement(GoShortVarDeclaration.class),
                    psiElement(GoVarDeclaration.class),
                    psiElement(GoTypeStructField.class),
                    psiElement(GoFunctionParameter.class)
                )
            );
}
