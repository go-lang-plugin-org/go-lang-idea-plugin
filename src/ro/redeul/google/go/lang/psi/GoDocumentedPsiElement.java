package ro.redeul.google.go.lang.psi;

import com.intellij.psi.PsiComment;

/**
 * <p/>
 * Created on Jan-02-2014 14:10
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public interface GoDocumentedPsiElement extends GoPsiElement {

    PsiComment[] getDocumentation();

    PsiComment[] getComment();
}
