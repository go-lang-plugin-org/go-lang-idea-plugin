package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoLabeledStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class ShortVarDeclarationResolver {

    private static final ElementPattern<GoLiteralIdentifier> SHORT_VAR_IN_FUNCTION =
            psiElement(GoLiteralIdentifier.class).withParent(
                    psiElement(GoShortVarDeclaration.class).withParent(
                            psiElement(GoBlockStatement.class).withParent(GoFunctionDeclaration.class)
                    ));

    private static final ElementPattern<GoLiteralIdentifier> SHORT_VAR =
            psiElement(GoLiteralIdentifier.class).withParent(
                    psiElement(GoShortVarDeclaration.class));

    public static PsiElement resolve(GoLiteralIdentifier identifier) {
        PsiElement result = null;
        if (SHORT_VAR_IN_FUNCTION.accepts(identifier)) {
            result = findDeclarationInFunctionParameter(identifier);
        }

        if (result == null && SHORT_VAR.accepts(identifier)) {
            PsiElement statement = identifier.getParent().getPrevSibling();
            result = findDeclarationInPreviousStatements(statement, identifier.getUnqualifiedName());
        }

        return result;
    }

    private static PsiElement findDeclarationInPreviousStatements(PsiElement statement, String identifierName) {
        while (statement != null) {
            PsiElement result = findDeclarationInStatement(statement, identifierName);
            if (result != null) {
                return result;
            }
            statement = statement.getPrevSibling();
        }
        return null;
    }

    private static PsiElement findDeclarationInStatement(PsiElement element, String identifierName) {
        while (element instanceof GoLabeledStatement) {
            element = ((GoLabeledStatement) element).getStatement();
        }

        if (element instanceof GoVarDeclaration) {
            for (GoLiteralIdentifier identifier : ((GoShortVarDeclaration) element).getIdentifiers()) {
                if (identifier.getUnqualifiedName().equals(identifierName)) {
                    return identifier;
                }
            }
        }
        return null;
    }

    private static PsiElement findDeclarationInFunctionParameter(GoLiteralIdentifier identifier) {
        GoFunctionDeclaration functionDeclaration = findParentOfType(identifier, GoFunctionDeclaration.class);
        for (GoFunctionParameter parameter : functionDeclaration.getParameters()) {
            for (GoLiteralIdentifier p : parameter.getIdentifiers()) {
                if (identifier.getUnqualifiedName().equals(p.getUnqualifiedName())) {
                    return p;
                }
            }
        }

        for (GoFunctionParameter parameter : functionDeclaration.getResults()) {
            for (GoLiteralIdentifier p : parameter.getIdentifiers()) {
                if (identifier.getUnqualifiedName().equals(p.getUnqualifiedName())) {
                    return p;
                }
            }
        }

        if (functionDeclaration instanceof GoMethodDeclaration) {
            GoMethodReceiver methodReceiver = ((GoMethodDeclaration) functionDeclaration).getMethodReceiver();
            GoLiteralIdentifier methodReceiverIdentifier = methodReceiver.getIdentifier();
            if (methodReceiverIdentifier != null &&
                    methodReceiverIdentifier.getUnqualifiedName().equals(identifier.getUnqualifiedName())) {
                return methodReceiverIdentifier;
            }
        }
        return null;
    }
}
