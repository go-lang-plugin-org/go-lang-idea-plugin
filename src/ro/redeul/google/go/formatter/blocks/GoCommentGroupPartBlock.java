package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiComment;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;

/**
 * <p/>
 * Created on Jan-02-2014 14:44
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoCommentGroupPartBlock extends GoLeafBlock {
  public GoCommentGroupPartBlock(PsiComment psi, CommonCodeStyleSettings settings, Alignment alignment, Indent indent) {
    super(psi.getNode(), alignment, indent, null, settings);
  }
}
