package ro.redeul.google.go.lang.psi;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;

import java.util.List;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-02-2014 14:10
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public interface GoDocumentedPsiElement {

  boolean isDocumentationPart(PsiElement child);

  List<PsiComment> getDocumentation();
}
