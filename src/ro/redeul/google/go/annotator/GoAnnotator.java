package ro.redeul.google.go.annotator;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.highlight.GoSyntaxHighlighter;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralBool;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.statements.GoDeferStatement;
import ro.redeul.google.go.lang.psi.statements.GoGoStatement;
import ro.redeul.google.go.lang.psi.statements.GoIfStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.inspection.InspectionUtil.getProblemRange;
import static ro.redeul.google.go.lang.psi.patterns.GoElementPatterns.GLOBAL_VAR_DECL;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isFunctionOrMethodCall;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 8:30:33 PM
 */
public class GoAnnotator extends GoRecursiveElementVisitor
    implements Annotator {

    private AnnotationHolder annotationHolder;

    public GoAnnotator() {

    }

    public void annotate(@NotNull PsiElement element,
                         @NotNull AnnotationHolder holder) {
        if (element instanceof GoPsiElement) {
            GoPsiElement goPsiElement = (GoPsiElement) element;

            try {
                annotationHolder = holder;

                goPsiElement.accept(this);
            } finally {
                annotationHolder = null;
            }
        }
    }

    private Annotation toAnnotation(ProblemDescriptor pd) {
        TextRange problemRange = getProblemRange(pd);
        String desc = pd.getDescriptionTemplate();

        Annotation annotation;

        switch (pd.getHighlightType()) {
            case GENERIC_ERROR_OR_WARNING:
            case ERROR:
            case GENERIC_ERROR:
            case LIKE_UNKNOWN_SYMBOL:
                annotation = annotationHolder.createErrorAnnotation(
                    problemRange, desc);
                break;

            case LIKE_DEPRECATED:
            case LIKE_UNUSED_SYMBOL: {
                annotation =
                    annotationHolder.createWeakWarningAnnotation(problemRange,
                                                                 desc);
                break;
            }

            case INFORMATION:
                annotation =
                    annotationHolder.createInfoAnnotation(problemRange, desc);
                break;

            case WEAK_WARNING:
            default:
                annotation =
                    annotationHolder.createWarningAnnotation(problemRange,
                                                             desc);
        }

        if (annotation != null) {
            annotation.setHighlightType(pd.getHighlightType());
        }

        return annotation;
    }

    @Override
    public void visitLiteralBool(GoLiteralBool literal) {
        Annotation ann = annotationHolder.createInfoAnnotation(literal, null);
        ann.setTextAttributes(GoSyntaxHighlighter.KEYWORD);
    }

    @Override
    public void visitIfStatement(GoIfStatement statement) {
        super.visitIfStatement(statement);

        if (statement.getExpression() == null) {
            PsiElement element = statement.getFirstChild();
            if (element == null) {
                element = statement;
            }
            annotationHolder.createErrorAnnotation(element,
                                                   GoBundle.message(
                                                       "error.missing.condition.in.if.statement"));
        }
    }

    @Override
    public void visitCallOrConvExpression(GoCallOrConvExpression expression) {
        if (expression.getTypeArgument() == null) {
            PsiElement definition = resolveSafely(
                expression.getBaseExpression(), PsiElement.class);

            if (psiElement(GoTypeSpec.class).accepts(definition)) {
                annotationHolder.createInfoAnnotation(
                    expression.getBaseExpression(), null)
                                .setTextAttributes(
                                    GoSyntaxHighlighter.TYPE_NAME);
                return;
            }
        }

        for (GoExpr argumentExpression : expression.getArguments()) {
            argumentExpression.accept(this);
        }
    }

    @Override
    public void visitBuiltinCallExpression(GoBuiltinCallExpression expression) {
        PsiElement definition = resolveSafely(expression.getBaseExpression(),
                                              PsiElement.class);

        if (definition == null || psiElement(GoLiteralIdentifier.class).accepts(
            definition)) {
            annotationHolder.createInfoAnnotation(expression.getBaseExpression(), null)
                            .setTextAttributes(GoSyntaxHighlighter.KEYWORD);
        }

        if (psiElement(GoTypeSpec.class).accepts(definition)) {
            annotationHolder.createInfoAnnotation(
                expression.getBaseExpression(), null)
                            .setTextAttributes(GoSyntaxHighlighter.TYPE_NAME);
        }

        for (GoExpr argumentExpression : expression.getArguments()) {
            argumentExpression.accept(this);
        }
    }

    @Override
    public void visitLiteralExpression(GoLiteralExpression expression) {
        super.visitLiteralExpression(expression);

        if (expression.getLiteral().getType() == GoLiteral.Type.Identifier) {
            GoLiteralIdentifier identifier = (GoLiteralIdentifier) expression.getLiteral();
            processLiteralIdentifier(identifier);
        }
    }

    void processLiteralIdentifier(GoLiteralIdentifier identifier) {
        if (identifier.isBlank()) {
            return;
        }

        // make iota a keyword
        if (identifier.isIota() || identifier.getText().matches(
            "nil|true|false")) {
            annotationHolder.createInfoAnnotation(identifier, null)
                            .setTextAttributes(GoSyntaxHighlighter.KEYWORD);
            return;
        }

        if (psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(
                        psiElement(GoCallOrConvExpression.class)))
            .accepts(identifier))
            return;

        PsiElement definition = resolveSafely(identifier, PsiElement.class);

        if (definition == null)
            return;

        Annotation annotation =
            annotationHolder.createInfoAnnotation(identifier, null);

        if (psiElement().withParent(GoConstDeclaration.class)
            .accepts(definition)) {
            annotation.setTextAttributes(GoSyntaxHighlighter.CONST);
            return;
        }

        if (GLOBAL_VAR_DECL.accepts(definition)) {
            annotation.setTextAttributes(GoSyntaxHighlighter.GLOBAL_VARIABLE);
            return;
        }

        if (psiElement(GoTypeSpec.class).accepts(definition)) {
            annotation.setTextAttributes(GoSyntaxHighlighter.TYPE_NAME);
            return;
        }

        annotation.setTextAttributes(GoSyntaxHighlighter.VARIABLE);
    }

    @Override
    public void visitTypeName(GoPsiTypeName typeName) {
        Annotation ann = annotationHolder.createInfoAnnotation(typeName, null);
        ann.setTextAttributes(GoSyntaxHighlighter.TYPE_NAME);
    }

    @Override
    public void visitTypeNameDeclaration(GoTypeNameDeclaration declaration) {
        Annotation ann = annotationHolder.createInfoAnnotation(declaration,
                                                               null);
        ann.setTextAttributes(GoSyntaxHighlighter.TYPE_NAME);
    }

    @Override
    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        super.visitMethodDeclaration(declaration);

        PsiElement nameIdentifier = declaration.getNameIdentifier();
        if (nameIdentifier != null) {
            Annotation ann = annotationHolder.createInfoAnnotation(
                nameIdentifier, null);
            ann.setTextAttributes(GoSyntaxHighlighter.METHOD_DECLARATION);
        }
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
        super.visitFunctionDeclaration(declaration);

        PsiElement nameIdentifier = declaration.getNameIdentifier();
        if (nameIdentifier != null) {
            Annotation ann = annotationHolder.createInfoAnnotation(
                nameIdentifier, null);
            ann.setTextAttributes(GoSyntaxHighlighter.METHOD_DECLARATION);
        }
    }

    @Override
    public void visitConstDeclaration(GoConstDeclaration declaration) {
        super.visitConstDeclaration(declaration);

        for (GoLiteralIdentifier identifier : declaration.getIdentifiers()) {
            annotationHolder
                .createInfoAnnotation(identifier, null)
                .setTextAttributes(GoSyntaxHighlighter.CONST);
        }
    }

    @Override
    public void visitFunctionParameter(GoFunctionParameter parameter) {
        super.visitFunctionParameter(parameter);

        for (GoLiteralIdentifier identifier : parameter.getIdentifiers()) {
            annotationHolder
                .createInfoAnnotation(identifier, null)
                .setTextAttributes(GoSyntaxHighlighter.VARIABLE);
        }
    }

    @Override
    public void visitTypeStructField(GoTypeStructField field) {
        super.visitTypeStructField(field);

        for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
            annotationHolder
                .createInfoAnnotation(identifier, null)
                .setTextAttributes(GoSyntaxHighlighter.VARIABLE);
        }
    }

    @Override
    public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
        visitVarDeclaration(declaration);
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration declaration) {
        super.visitVarDeclaration(declaration);
        TextAttributesKey type = GoSyntaxHighlighter.VARIABLE;

        if (psiElement(GoVarDeclarations.class)
            .withParent(psiElement(GoFile.class))
            .accepts(declaration.getParent())) {
            type = GoSyntaxHighlighter.GLOBAL_VARIABLE;
        }

        for (GoLiteralIdentifier identifier : declaration.getIdentifiers()) {
            annotationHolder
                .createInfoAnnotation(identifier, null)
                .setTextAttributes(type);
        }
    }

    @Override
    public void visitGoStatement(GoGoStatement statement) {
        super.visitGoStatement(statement);

        if (!isFunctionOrMethodCall(statement.getExpression())) {
            PsiElement lastChild = GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment(
                statement.getLastChild());
            if (lastChild == null) {
                lastChild = statement;
            }

            annotationHolder.createErrorAnnotation(lastChild,
                                                   "Argument to go must be function call");
        }
    }

    @Override
    public void visitDeferStatement(GoDeferStatement statement) {
        super.visitDeferStatement(statement);

        if (!isFunctionOrMethodCall(statement.getExpression())) {
            PsiElement lastChild = GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment(
                statement.getLastChild());
            if (lastChild == null) {
                lastChild = statement;
            }

            annotationHolder.createErrorAnnotation(lastChild,
                                                   "Argument to defer must be function call");
        }
    }
}
