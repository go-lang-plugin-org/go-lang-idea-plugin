package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.CustomSpacing;

class GoTypeStructFieldBlock extends GoSyntheticBlock<GoTypeStructField> {

  private static final TokenSet FIELD_TYPE_SET = TokenSet.create(
    TYPE_SLICE,
    TYPE_NAME,
    TYPE_INTERFACE,
    TYPE_CHAN_BIDIRECTIONAL,
    TYPE_CHAN_RECEIVING,
    TYPE_CHAN_SENDING,
    TYPE_STRUCT,
    TYPE_POINTER,
    TYPE_FUNCTION,
    TYPE_PARENTHESIZED
  );

  private static CustomSpacing CUSTOM_SPACING_RULES = CustomSpacing.Builder()
    .setNone(LITERAL_IDENTIFIER, oCOMMA)  // x|, y, z int
    .setBasic(oCOMMA, LITERAL_IDENTIFIER) // x,| y, z int
    .build();

  GoTypeStructFieldBlock(@NotNull GoTypeStructField node,
                         CommonCodeStyleSettings settings,
                         Indent indent,
                         @NotNull Map<Alignments.Key, Alignment> alignsToUse) {
    super(node, settings, indent, null, alignsToUse);

    setCustomSpacing(CUSTOM_SPACING_RULES);
  }

  @Nullable
  @Override
  protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild,
                                        Map<Alignments.Key, Alignment> alignments) {

    if (FIELD_TYPE_SET.contains(child.getNode().getElementType()))
      return alignments.get(Alignments.Key.Type);

    if (child instanceof PsiComment)
      return alignments.get(Alignments.Key.Comments);

    return super.getChildAlignment(child, prevChild, alignments);
  }
}
