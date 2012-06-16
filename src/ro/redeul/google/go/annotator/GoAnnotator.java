package ro.redeul.google.go.annotator;

import java.util.Collection;
import java.util.List;

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
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.findUsages.GoVariableUsageStatVisitor;
import ro.redeul.google.go.highlight.GoSyntaxHighlighter;
import ro.redeul.google.go.inspection.ConstDeclarationInspection;
import ro.redeul.google.go.inspection.InspectionResult;
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

        Annotation annotation = null;

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

            case INFO:
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
//                annotation.setTextAttributes(GoSyntaxHighlighter.CONST);
            } else if (identifier.isGlobal()) {
                annotation.setTextAttributes(
                    GoSyntaxHighlighter.GLOBAL_VARIABLE);
            } else {
                annotation.setTextAttributes(GoSyntaxHighlighter.VARIABLE);
            }
        }
    }

    @Override
    public void visitFile(GoFile file) {
        InspectionResult result = new InspectionResult(inspectionManager);
        GoVariableUsageStatVisitor visitor =
            new GoVariableUsageStatVisitor(result);
        visitor.visitFile(file);
        addProblems(result.getProblems());
    }

    @Override
    public void visitTypeName(GoTypeName typeName) {
        Annotation annotation = annotationHolder.createInfoAnnotation(typeName,
                                                                      null);
        annotation.setTextAttributes(GoSyntaxHighlighter.TYPE_NAME);
    }

    @Override
    public void visitImportDeclaration(GoImportDeclaration importDeclaration) {
        Collection<GoFile> fileCollection =
            goNamesCache.getFilesByPackageName(
                importDeclaration.getImportPath().replaceAll("^\"|\"$", ""));

        if (fileCollection == null || fileCollection.size() == 0) {
            Annotation annotation =
                annotationHolder.createWeakWarningAnnotation(
                    importDeclaration,
                    GoBundle.message("error.invalid.import"));

            if (annotation != null)
                annotation.setHighlightType(
                    ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
        }

        Project project = importDeclaration.getProject();
        PsiFile file = importDeclaration.getContainingFile();
        if (!(file instanceof GoFile)) {
            return;
        }

        if (!GoCodeManager.getInstance(project)
                          .isImportUsed(importDeclaration, (GoFile) file)) {
            Annotation anno = annotationHolder.createErrorAnnotation(
                importDeclaration, "Unused import");
            anno.registerFix(new RemoveImportFix(importDeclaration));
            anno.setHighlightType(ProblemHighlightType.LIKE_UNUSED_SYMBOL);
        }
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration fd) {
        // TODO: fix this
//        addProblems(new FunctionDeclarationInspection(inspectionManager,
//                                                      fd).checkFunction());
    }

    @Override
    public void visitConstDeclarations(GoConstDeclarations constDeclarations) {
        InspectionResult result = new InspectionResult(inspectionManager);
        ConstDeclarationInspection
            .checkConstDeclarations(constDeclarations, result);
        addProblems(result.getProblems());
    }

    @Override
    public void visitConstDeclaration(GoConstDeclaration constDeclaration) {
        InspectionResult result = new InspectionResult(inspectionManager);
        ConstDeclarationInspection
            .checkConstDeclaration(constDeclaration, result);

        addProblems(result.getProblems());
    }

    @Override
    public void visitShortVarDeclaration(GoShortVarDeclaration shortVarDecl) {
        visitVarDeclaration(shortVarDecl);
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration varDeclaration) {
        InspectionResult result = new InspectionResult(inspectionManager);
        VarDeclarationInspection.checkVar(varDeclaration, result);
        addProblems(result.getProblems());
    }

    @Override
    public void visitGoStatement(GoGoStatement goStatement) {
        if (!isFunctionOrMethodCall(goStatement.getExpression())) {
            PsiElement lastChild = GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment(
                goStatement.getLastChild());
            if (lastChild == null) {
                lastChild = goStatement;
            }

            annotationHolder.createErrorAnnotation(lastChild,
                                                   "Argument to go must be function call");
        }
    }

    @Override
    public void visitDeferStatement(GoDeferStatement deferStatement) {
        if (!isFunctionOrMethodCall(deferStatement.getExpression())) {
            PsiElement lastChild = GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment(
                deferStatement.getLastChild());
            if (lastChild == null) {
                lastChild = deferStatement;
            }

            annotationHolder.createErrorAnnotation(lastChild,
                                                   "Argument to defer must be function call");
        }
    }
}
