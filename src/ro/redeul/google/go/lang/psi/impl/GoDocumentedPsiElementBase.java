package ro.redeul.google.go.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.util.GoPsiTextUtil;

import java.util.List;

/**
 * <p/>
 * Created on Jan-16-2014 15:24
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public abstract class GoDocumentedPsiElementBase extends GoPsiElementBase implements GoDocumentedPsiElement {

    protected GoDocumentedPsiElementBase(@NotNull ASTNode node) {
        super(node);
    }

//    @NotNull
//    @Override
//    public TextRange getTextRangeWithoutDocumentation(boolean skipLeading, boolean skipTrailing) {
//        return GoPsiTextUtil.getTextRangeWithoutComments(this, skipLeading, skipTrailing);
//    }

    @Override
    public PsiComment[] getComment() {
        return new PsiComment[0];
    }

    @Override
    public PsiComment[] getDocumentation() {
        return new PsiComment[0];
    }
}

