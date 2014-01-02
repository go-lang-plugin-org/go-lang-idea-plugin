package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;

import java.util.EnumSet;
import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.CustomSpacing;

class GoTypeInterfaceBlock extends GoSyntheticBlock<GoPsiTypeInterface> {

  private Alignment leadingAlignment = null;

  private static final TokenSet SIMILAR_NODES = TokenSet.create(
    FUNCTION_DECLARATION, TYPE_NAME
  );

  private static final TokenSet LINE_BREAKING_TOKS = TokenSet.orSet(
    COMMENTS, SIMILAR_NODES
  );

  private static final EnumSet<Alignments.Key> ALIGNMENT_KEYS = EnumSet.of(
    Alignments.Key.Comments
  );

  private static final CustomSpacing CUSTOM_SPACING_RULES =
    CustomSpacing.Builder()
      .setNone(pLCURLY, pRCURLY)
      .setNone(kINTERFACE, pLCURLY)
      .build();

  public GoTypeInterfaceBlock(GoPsiTypeInterface psi, CommonCodeStyleSettings settings,
                              Alignment alignment,
                              @NotNull Map<Alignments.Key, Alignment> alignmentsMap) {
    super(psi, settings, null, null, alignmentsMap);

    this.leadingAlignment = alignment;
    setMultiLineMode(StringUtil.containsLineBreak(psi.getText()), pLCURLY, pRCURLY);
    setLineBreakingTokens(LINE_BREAKING_TOKS);
    setAlignmentKeys(ALIGNMENT_KEYS);

    if (!isMultiLine())
      setCustomSpacing(CUSTOM_SPACING_RULES);
  }

  @Nullable
  @Override
  protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild,
                                        Map<Alignments.Key, Alignment> alignments) {
    if (child.getNode().getElementType() == kINTERFACE)
      return leadingAlignment;

    return super.getChildAlignment(child, prevChild, alignments);
  }

  @Override
  protected Indent getChildIndent(@NotNull PsiElement child, @Nullable PsiElement prevChild) {
    if (
      child instanceof GoFunctionDeclaration ||
        child instanceof GoPsiTypeName ||
        child instanceof PsiComment)
      return GoBlockUtil.Indents.NORMAL;

    return GoBlockUtil.Indents.NONE;
  }

  @Override
  protected boolean holdTogether(@Nullable IElementType typeChild1, @Nullable IElementType typeChild2, int linesBetween) {
    if(SIMILAR_NODES.contains(typeChild1) && SIMILAR_NODES.contains(typeChild2) &&
      linesBetween <= 1)
      return true;

    return super.holdTogether(typeChild1, typeChild2, linesBetween);
  }
}
