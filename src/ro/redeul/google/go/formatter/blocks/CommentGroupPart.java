package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiComment;

/**
 * <p/>
 * Created on Jan-02-2014 14:44
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class CommentGroupPart extends Leaf {
    public CommentGroupPart(PsiComment psi, Indent indent, Alignment alignment) {
        super(psi.getNode(), indent, alignment);
    }
}
