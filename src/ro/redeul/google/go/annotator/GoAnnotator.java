package ro.redeul.google.go.annotator;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.highlight.GoSyntaxHighlighter;
import ro.redeul.google.go.inspection.FunctionDeclarationInspection;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import java.util.Collection;

import static ro.redeul.google.go.inspection.ConstDeclarationInspection.isExtraExpressionInConst;
import static ro.redeul.google.go.inspection.ConstDeclarationInspection.isFirstConstExpressionMissed;
import static ro.redeul.google.go.inspection.ConstDeclarationInspection.isMissingExpressionInConst;
import static ro.redeul.google.go.inspection.InspectionUtil.getProblemRange;

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

    @Override
    public void visitIdentifier(GoIdentifier id) {
        if ( !GoPsiUtils.isIotaInConstantDeclaration(id) && id.resolve() == null ) {
            annotationHolder.createErrorAnnotation(
                id, GoBundle.message("warning.unresolved.identifier"));
        }
    }

    @Override
    public void visitTypeName(GoTypeName typeName) {
        Annotation annotation = annotationHolder.createInfoAnnotation(typeName, null);
        annotation.setTextAttributes(GoSyntaxHighlighter.TYPE_NAME);
    }

    @Override
    public void visitImportDeclaration(GoImportDeclaration importDeclaration) {
        Collection<GoFile> fileCollection = goNamesCache.getFilesByPackageName(importDeclaration.getImportPath().replaceAll("^\"|\"$", ""));
        if ( fileCollection == null || fileCollection.size() == 0 ) {
            annotationHolder.createErrorAnnotation(importDeclaration, "Invalid package import path");
        }
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration functionDeclaration) {
        FunctionDeclarationInspection fdi = new FunctionDeclarationInspection(inspectionManager, functionDeclaration);
        for (ProblemDescriptor pd : fdi.checkFunction()) {
            annotationHolder.createErrorAnnotation(getProblemRange(pd), pd.getDescriptionTemplate());
        }
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
}
