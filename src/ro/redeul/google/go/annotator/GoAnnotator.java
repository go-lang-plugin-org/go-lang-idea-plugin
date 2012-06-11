package ro.redeul.google.go.annotator;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.QuickFix;
import com.intellij.codeInspection.ex.QuickFixWrapper;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.findUsages.GoVariableUsageStatVisitor;
import ro.redeul.google.go.highlight.GoSyntaxHighlighter;
import ro.redeul.google.go.inspection.FunctionDeclarationInspection;
import ro.redeul.google.go.inspection.VarDeclarationInspection;
import ro.redeul.google.go.inspection.fix.RemoveImportFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoDeferStatement;
import ro.redeul.google.go.lang.psi.statements.GoGoStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.services.GoCodeManager;

import java.util.Collection;
import java.util.List;

import static ro.redeul.google.go.inspection.ConstDeclarationInspection.isExtraExpressionInConst;
import static ro.redeul.google.go.inspection.ConstDeclarationInspection.isFirstConstExpressionMissed;
import static ro.redeul.google.go.inspection.ConstDeclarationInspection.isMissingExpressionInConst;
import static ro.redeul.google.go.inspection.InspectionUtil.getProblemRange;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isFunctionOrMethodCall;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 8:30:33 PM
 */
public class GoAnnotator extends GoElementVisitor implements Annotator {

    private AnnotationHolder annotationHolder;
    private GoNamesCache goNamesCache;
    private InspectionManager inspectionManager;

    public GoAnnotator() {

    }

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if ( element instanceof GoPsiElement ) {
            goNamesCache = ContainerUtil.findInstance(element.getProject().getExtensions(PsiShortNamesCache.EP_NAME), GoNamesCache.class);
            annotationHolder = holder;
            inspectionManager = InspectionManager.getInstance(element.getProject());
            ((GoPsiElement) element).accept(this);
            inspectionManager = null;
            annotationHolder = null;
        }
    }

    private Annotation toAnnotation(ProblemDescriptor pd) {
        TextRange problemRange = getProblemRange(pd);
        String desc = pd.getDescriptionTemplate();
        switch (pd.getHighlightType()) {
            case GENERIC_ERROR_OR_WARNING:
            case ERROR:
            case GENERIC_ERROR:
            case LIKE_UNKNOWN_SYMBOL:
                return annotationHolder.createErrorAnnotation(problemRange, desc);

            case LIKE_DEPRECATED:
            case LIKE_UNUSED_SYMBOL:
                return annotationHolder.createWeakWarningAnnotation(problemRange, desc);

            case INFO:
            case INFORMATION:
                return annotationHolder.createInfoAnnotation(problemRange, desc);

            case WEAK_WARNING:
            default:
                return annotationHolder.createWarningAnnotation(problemRange, desc);
        }
    }

    /**
     * Add all problems to annotation holder.
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
    public void visitIdentifier(GoLiteralIdentifier id) {
        PsiElement resolve = id.resolve();
        // if the identifier resolves to a const, set const highlight
        if (resolve != null && resolve.getParent() instanceof GoConstDeclaration) {
            Annotation annotation = annotationHolder.createInfoAnnotation(id, null);
            annotation.setTextAttributes(GoSyntaxHighlighter.CONST);
        }
    }

    @Override
    public void visitFile(GoFile file) {
        GoVariableUsageStatVisitor visitor =
            new GoVariableUsageStatVisitor(inspectionManager);
        visitor.visitFile(file);
        addProblems(visitor.getProblems());
    }

    @Override
    public void visitTypeName(GoTypeName typeName) {
        Annotation annotation = annotationHolder.createInfoAnnotation(typeName, null);
        annotation.setTextAttributes(GoSyntaxHighlighter.TYPE_NAME);
    }

    @Override
    public void visitImportDeclaration(GoImportDeclaration importDeclaration) {
        Collection<GoFile> fileCollection =
            goNamesCache.getFilesByPackageName(
                importDeclaration.getImportPath().replaceAll("^\"|\"$", ""));

        if ( fileCollection == null || fileCollection.size() == 0 ) {
            Annotation annotation =
                annotationHolder.createErrorAnnotation(
                    importDeclaration, GoBundle.message("error.invalid.import"));

            if (annotation != null)
                annotation.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
        }

        Project project = importDeclaration.getProject();
        PsiFile file = importDeclaration.getContainingFile();
        if (!(file instanceof GoFile)) {
            return;
        }

        if (!GoCodeManager.getInstance(project).isImportUsed(importDeclaration, (GoFile) file)) {
            Annotation anno = annotationHolder.createErrorAnnotation(importDeclaration, "Unused import");
            anno.registerFix(new RemoveImportFix(importDeclaration));
        }
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration fd) {
        addProblems(new FunctionDeclarationInspection(inspectionManager, fd).checkFunction());
    }

    @Override
    public void visitConstDeclarations(GoConstDeclarations constDeclarations) {
        if (isFirstConstExpressionMissed(constDeclarations)) {
            GoConstDeclaration declaration = constDeclarations.getDeclarations()[0];
            annotationHolder.createErrorAnnotation(declaration, "Unexpected semicolon or newline");
        }
    }

    @Override
    public void visitConstDeclaration(GoConstDeclaration constDeclaration) {
        if (isMissingExpressionInConst(constDeclaration)) {
            annotationHolder.createErrorAnnotation(constDeclaration, "Missing expression in const declaration");
        } else if (isExtraExpressionInConst(constDeclaration)) {
            annotationHolder.createErrorAnnotation(constDeclaration, "Extra expression in const declaration");
        }
    }

    @Override
    public void visitShortVarDeclaration(GoShortVarDeclaration shortVarDeclaration) {
        visitVarDeclaration(shortVarDeclaration);
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration varDeclaration) {
        addProblems(new VarDeclarationInspection(inspectionManager, varDeclaration).checkVar());
    }

    @Override
    public void visitGoStatement(GoGoStatement goStatement) {
        if (!isFunctionOrMethodCall(goStatement.getExpression())) {
            PsiElement lastChild = GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment(goStatement.getLastChild());
            if (lastChild == null) {
                lastChild = goStatement;
            }

            annotationHolder.createErrorAnnotation(lastChild, "Argument to go must be function call");
        }
    }

    @Override
    public void visitDeferStatement(GoDeferStatement deferStatement) {
        if (!isFunctionOrMethodCall(deferStatement.getExpression())) {
            PsiElement lastChild = GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment(deferStatement.getLastChild());
            if (lastChild == null) {
                lastChild = deferStatement;
            }

            annotationHolder.createErrorAnnotation(lastChild, "Argument to defer must be function call");
        }
    }
}
