package ro.redeul.google.go.annotator;

import java.util.List;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.QuickFix;
import com.intellij.codeInspection.ex.QuickFixWrapper;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.findUsages.GoVariableUsageStatVisitor;
import ro.redeul.google.go.highlight.GoSyntaxHighlighter;
import ro.redeul.google.go.inspection.ConstDeclarationInspection;
import ro.redeul.google.go.inspection.FunctionDeclarationInspection;
import ro.redeul.google.go.inspection.InspectionResult;
import ro.redeul.google.go.inspection.VarDeclarationInspection;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralBool;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpr;
import ro.redeul.google.go.lang.psi.statements.GoDeferStatement;
import ro.redeul.google.go.lang.psi.statements.GoGoStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import static ro.redeul.google.go.inspection.InspectionUtil.getProblemRange;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isFunctionOrMethodCall;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 8:30:33 PM
 */
public class GoAnnotator extends GoRecursiveElementVisitor implements Annotator {

    private AnnotationHolder annotationHolder;
    private GoNamesCache goNamesCache;
    private InspectionManager inspectionManager;

    public GoAnnotator() {

    }

    public void annotate(@NotNull PsiElement element,
                         @NotNull AnnotationHolder holder) {
        if (element instanceof GoPsiElement) {
            goNamesCache = ContainerUtil.findInstance(
                element.getProject().getExtensions(PsiShortNamesCache.EP_NAME),
                GoNamesCache.class);
            annotationHolder = holder;
            inspectionManager = InspectionManager.getInstance(
                element.getProject());
            ((GoPsiElement) element).accept(this);
            inspectionManager = null;
            annotationHolder = null;
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

    /**
     * Add all problems to annotation holder.
     *
     * @param problems problems to be added to annotation holder
     */
    private void addProblems(List<ProblemDescriptor> problems) {
        for (ProblemDescriptor pd : problems) {
            Annotation anno = toAnnotation(pd);
            anno.setHighlightType(pd.getHighlightType());
            QuickFix[] fixes = pd.getFixes();
            if (fixes == null) {
                continue;
            }

            for (int i = 0; i < fixes.length; i++) {
                if (fixes[i] instanceof IntentionAction) {
                    anno.registerFix((IntentionAction) fixes[i]);
                } else {
                    anno.registerFix(QuickFixWrapper.wrap(pd, i));
                }
            }
        }
    }

    @Override
    public void visitLiteralBool(GoLiteralBool literal) {
        Annotation ann = annotationHolder.createInfoAnnotation(literal, null);
        ann.setTextAttributes(GoSyntaxHighlighter.KEYWORD);
    }

    @Override
    public void visitBuiltinCallExpression(GoBuiltinCallExpr expression) {
        visitElement(expression);

        Annotation ann = annotationHolder.createInfoAnnotation(expression.getIdentifier(), null);
        ann.setTextAttributes(GoSyntaxHighlighter.KEYWORD);
    }

    @Override
    public void visitIdentifier(GoLiteralIdentifier identifier) {
        if (identifier.isBlank()) {
            return;
        }

        Annotation annotation;

        // make iota a keyword
        if (identifier.isIota()) {
            annotation = annotationHolder.createInfoAnnotation(identifier,
                                                               null);
            annotation.setTextAttributes(GoSyntaxHighlighter.KEYWORD);
            return;
        }


        PsiReference reference = identifier.getReference();
        if (reference == null)
            return;

        PsiElement def = reference.resolve();
        if (def != null) {
            annotation = annotationHolder.createInfoAnnotation(identifier,
                                                               null);

            // if the identifier resolves to a const, set const highlight
            if (def.getParent() instanceof GoConstDeclaration) {
                annotation.setTextAttributes(GoSyntaxHighlighter.CONST);
            } else if (identifier.isGlobal()) {
                annotation.setTextAttributes(GoSyntaxHighlighter.GLOBAL_VARIABLE);
            } else {
                annotation.setTextAttributes(GoSyntaxHighlighter.VARIABLE);
            }
        }
    }

    @Override
    public void visitFile(GoFile file) {
        visitElement(file);

        InspectionResult result = new InspectionResult(inspectionManager);
        new GoVariableUsageStatVisitor(result).visitFile(file);
        addProblems(result.getProblems());
    }

    @Override
    public void visitTypeName(GoTypeName typeName) {
        Annotation ann = annotationHolder.createInfoAnnotation(typeName, null);
        ann.setTextAttributes(GoSyntaxHighlighter.TYPE_NAME);
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
        visitElement(declaration);

        InspectionResult result = new InspectionResult(inspectionManager);
        FunctionDeclarationInspection.checkFunction(result, declaration);
        addProblems(result.getProblems());
    }

    @Override
    public void visitFunctionLiteral(GoLiteralFunction literal) {
        visitElement(literal);

        InspectionResult result = new InspectionResult(inspectionManager);
        FunctionDeclarationInspection.checkFunction(result, literal);
        addProblems(result.getProblems());
    }

    @Override
    public void visitConstDeclarations(GoConstDeclarations declarations) {
        visitElement(declarations);

        InspectionResult result = new InspectionResult(inspectionManager);
        ConstDeclarationInspection.checkConstDeclarations(declarations, result);
        addProblems(result.getProblems());
    }

    @Override
    public void visitConstDeclaration(GoConstDeclaration declaration) {
        visitElement(declaration);

        InspectionResult result = new InspectionResult(inspectionManager);
        ConstDeclarationInspection
            .checkConstDeclaration(declaration, result);

        addProblems(result.getProblems());
    }

    @Override
    public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
        visitElement(declaration);

        visitVarDeclaration(declaration);
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration declaration) {
        visitElement(declaration);

        InspectionResult result = new InspectionResult(inspectionManager);
        VarDeclarationInspection.checkVar(declaration, result);
        addProblems(result.getProblems());
    }

    @Override
    public void visitGoStatement(GoGoStatement statement) {
        visitElement(statement);

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
        visitElement(statement);

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
