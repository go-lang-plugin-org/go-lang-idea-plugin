package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.CustomSpacing;

class GoFunctionDeclarationBlock extends GoSyntheticBlock<GoFunctionDeclaration> {

  private static CustomSpacing CUSTOM_SPACING_RULES = CustomSpacing.Builder()
    .setNone(LITERAL_IDENTIFIER, pLPAREN) // func main|()

    .setNone(pLPAREN, FUNCTION_PARAMETER_LIST) // func main(|a int)
    .setNone(FUNCTION_PARAMETER_LIST, pRPAREN) // func main(a, b int|)

    .setNone(pLPAREN, METHOD_RECEIVER) // func (|a int) main()
    .setNone(METHOD_RECEIVER, pRPAREN) // func (a int|) main()

    .setNone(pLPAREN, pRPAREN) // (|)
    .build();

  public GoFunctionDeclarationBlock(GoFunctionDeclaration psi,
                                    CommonCodeStyleSettings settings,
                                    Indent indent,
                                    Alignment alignment,
                                    Map<Alignments.Key, Alignment> alignmentsMap) {
    super(psi, settings, indent, null, alignmentsMap);

    setCustomSpacing(CUSTOM_SPACING_RULES);
  }

  @Nullable
  @Override
  protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild, Map<Alignments.Key, Alignment> alignments) {
    if (child instanceof PsiComment)
      return alignments.get(Alignments.Key.Comments);

    return super.getChildAlignment(child, prevChild, alignments);
  }
}
