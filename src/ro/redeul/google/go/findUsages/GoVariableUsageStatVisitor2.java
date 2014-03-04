package ro.redeul.google.go.findUsages;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.InspectionResult;
import ro.redeul.google.go.inspection.fix.RemoveVariableFix;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.types.GoPsiTypeFunctionImpl;
import ro.redeul.google.go.lang.psi.processors.GoNamesUtil;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.lang.psi.patterns.GoElementPatterns.*;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

public class GoVariableUsageStatVisitor2 extends GoRecursiveElementVisitor {

    private static final TokenSet NEW_SCOPE_STATEMENT = TokenSet.create(
        GoElementTypes.BLOCK_STATEMENT,
        GoElementTypes.IF_STATEMENT,
        GoElementTypes.FOR_WITH_CLAUSES_STATEMENT,
        GoElementTypes.FOR_WITH_CONDITION_STATEMENT,
        GoElementTypes.FOR_WITH_RANGE_STATEMENT,
        GoElementTypes.SWITCH_EXPR_STATEMENT,
        GoElementTypes.SWITCH_TYPE_STATEMENT,
        GoElementTypes.SWITCH_EXPR_CASE,
        GoElementTypes.SELECT_STATEMENT,
        GoElementTypes.SELECT_COMM_CLAUSE_RECV,
        GoElementTypes.SELECT_COMM_CLAUSE_SEND,
        GoElementTypes.SELECT_COMM_CLAUSE_DEFAULT
    );

    private final InspectionResult result;

    public GoVariableUsageStatVisitor2(InspectionResult result) {
        this.result = result;
    }

    @Override
    public void visitFile(GoFile file) {
        visitElement(file);

        for (GoPsiElement usage : usages) {
            declarations.remove(usage);
        }

        for (GoPsiElement declaration : declarations) {

            if (psiElement(GoLiteralIdentifier.class).accepts(declaration)) {
                GoLiteralIdentifier ident = (GoLiteralIdentifier) declaration;

                if (ident.isIota() || ident.isBlank())
                    continue;

                if (GLOBAL_CONST_DECL.accepts(ident) ||
                    GLOBAL_VAR_DECL.accepts(ident)) {
                    if (GoNamesUtil.isExportedName(ident.getName())) {
                        continue;
                    }

                    if (GLOBAL_CONST_DECL.accepts(ident)) {
                        result.addProblem(declaration,
                                          GoBundle.message(
                                              "error.constant.not.used",
                                              declaration.getText()),
                                          ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                                          new RemoveVariableFix());
                    } else {
                        result.addProblem(declaration,
                                          GoBundle.message(
                                              "error.variable.not.used",
                                              declaration.getText()),
                                          ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                                          new RemoveVariableFix());
                    }
                    continue;
                }
            }

            if (declaration.getParent().getParent().getParent() instanceof GoPsiTypeFunctionImpl) {
                continue;
            }

            if (PARAMETER_DECLARATION.accepts(declaration)) {
                result.addProblem(declaration,
                                  GoBundle.message("error.parameter.not.used",
                                                   declaration.getText()),
                                  ProblemHighlightType.LIKE_UNUSED_SYMBOL);
            } else if (CONST_DECLARATION.accepts(declaration)) {
                result.addProblem(declaration,
                                  GoBundle.message("error.constant.not.used",
                                                   declaration.getText()),
                                  ProblemHighlightType.ERROR,
                                  new RemoveVariableFix());
            } else {
                result.addProblem(declaration,
                                  GoBundle.message("error.variable.not.used",
                                                   declaration.getText()),
                                  ProblemHighlightType.ERROR,
                                  new RemoveVariableFix());
            }
        }
    }

    private final Set<GoPsiElement> declarations = new HashSet<GoPsiElement>();
    private final Set<GoPsiElement> usages = new HashSet<GoPsiElement>();

    @Override
    public void visitConstDeclaration(GoConstDeclaration declaration) {
        Collections.addAll(declarations, declaration.getIdentifiers());
        for (GoExpr goExpr : declaration.getExpressions()) {
            goExpr.accept(this);
        }
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration declaration) {
        Collections.addAll(declarations, declaration.getIdentifiers());
        for (GoExpr goExpr : declaration.getExpressions()) {
            goExpr.accept(this);
        }
    }

    @Override
    public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
        GoLiteralIdentifier[] identifiers = declaration.getIdentifiers();

        for (GoLiteralIdentifier identifier : identifiers) {
            GoLiteralIdentifier definition = resolveSafely(identifier,
                                                           GoLiteralIdentifier.class);
            if (definition == null) {
                declarations.add(identifier);
            } else {
                usages.add(definition);
            }
        }

        for (GoExpr goExpr : declaration.getExpressions()) {
            goExpr.accept(this);
        }
    }

    @Override
    public void visitFunctionParameter(GoFunctionParameter parameter) {
        Collections.addAll(declarations, parameter.getIdentifiers());
    }

    @Override
    public void visitLiteralExpression(GoLiteralExpression expression) {
        GoPsiElement psiElement = resolveSafely(expression, GoPsiElement.class);
        if (psiElement != null) {
            usages.add(psiElement);
        } else {
            visitElement(expression);
        }
    }

    @Override
    public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
        GoPsiElement psiElement = resolveSafely(identifier, GoPsiElement.class);
        if (psiElement != null) {
            usages.add(psiElement);
        }
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
        for (GoFunctionParameter parameter : declaration.getParameters()) {
            parameter.accept(this);
        }

        if (declaration.getBlock() != null)
            declaration.getBlock().accept(this);
    }

    @Override
    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        declaration.getMethodReceiver().accept(this);

        visitFunctionDeclaration(declaration);
    }

    @Override
    public void visitInterfaceType(GoPsiTypeInterface type) {
        // dont :)
    }
}
