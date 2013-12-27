package ro.redeul.google.go.lang.psi.patterns;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeAndVarsStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;
import ro.redeul.google.go.lang.psi.statements.GoLabeledStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

public class GoElementPatterns {

    public static final ElementPattern<GoLiteralIdentifier> GLOBAL_CONST_DECL =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoConstDeclaration.class)
                    .withParent(
                        psiElement(GoConstDeclarations.class)
                            .withParent(psiElement(GoFile.class))));

    public static final ElementPattern<GoLiteralIdentifier> CONST_DECLARATION =
        psiElement(GoLiteralIdentifier.class)
            .withParent(GoConstDeclaration.class);

    public static final ElementPattern<GoLiteralIdentifier> GLOBAL_VAR_DECL =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoVarDeclaration.class)
                    .withParent(
                        psiElement(GoVarDeclarations.class)
                            .withParent(psiElement(GoFile.class))));

    public static final ElementPattern<GoLiteralIdentifier> METHOD_DECLARATION =
        psiElement(GoLiteralIdentifier.class)
            .withParent(GoMethodDeclaration.class);

    public static final ElementPattern<GoLiteralIdentifier> FUNCTION_DECLARATION =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(GoFunctionDeclaration.class);

    @SuppressWarnings("unchecked")
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

    public static final ElementPattern<GoLiteralIdentifier> VAR_IN_FOR_RANGE =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoForWithRangeAndVarsStatement.class)
                    );

    public static final ElementPattern<GoLiteralIdentifier> PARAMETER_DECLARATION =
        psiElement(GoLiteralIdentifier.class)
            .withParent(GoFunctionParameter.class);

    @SuppressWarnings("unchecked")
    public static final ElementPattern<? extends PsiElement> BLOCK_DECLARATIONS =
        or(
            psiElement(GoShortVarDeclaration.class),
            psiElement(GoVarDeclarations.class),
            psiElement(GoTypeDeclaration.class),
            psiElement(GoConstDeclarations.class),
            psiElement(GoLabeledStatement.class)
        );

}
