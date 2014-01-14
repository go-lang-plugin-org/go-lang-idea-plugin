package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;

/**
 * Type declaration block.
 * <p/>
 * Created on Dec-30-2013 16:58
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class TypeSpec extends Code<GoTypeSpec> {
  public TypeSpec(GoTypeSpec typeSpec, CommonCodeStyleSettings settings,
                  Indent indent,
                  Map<Alignments.Key, Alignment> alignmentsMap) {
    super(typeSpec, settings, indent, null, alignmentsMap);
  }

  @Override
  protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild,
                                        Map<Alignments.Key, Alignment> alignments) {
    if (child instanceof GoPsiType)
      return alignments.get(Alignments.Key.Type);

    if (child instanceof PsiComment)
      return alignments.get(Alignments.Key.Comments);

    return super.getChildAlignment(child, prevChild, alignments);
  }
}
