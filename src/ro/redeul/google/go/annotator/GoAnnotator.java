package ro.redeul.google.go.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 8:30:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoAnnotator extends GoElementVisitor implements Annotator {

    private AnnotationHolder annotationHolder;

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if ( element instanceof GoPsiElement ) {
            annotationHolder = holder;
            ((GoPsiElement) element).accept(this);
            annotationHolder = null;
        }
    }


    @Override
    public void visitTypeName(GoTypeName typeName) {
//        Annotation annotation = annotationHolder.createInfoAnnotation(typeName, null);
//        annotation.setTextAttributes(SyntaxHighlighterColors.JAVA_BLOCK_COMMENT);
    }
}
