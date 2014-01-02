package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;

class GoTypeStructBlock extends GoSyntheticBlock<GoPsiTypeStruct> {

  private static final TokenSet LINE_BREAKING_TOKS = TokenSet.create(
    mML_COMMENT, mSL_COMMENT,
    TYPE_STRUCT_FIELD, TYPE_STRUCT_FIELD_ANONYMOUS
  );

  private static final EnumSet<Alignments.Key> ALIGNMENT_KEYS = EnumSet.of(
    Alignments.Key.Comments,
    Alignments.Key.Type
  );

  private static final GoBlockUtil.CustomSpacing CUSTOM_SPACING_RULES =
    GoBlockUtil.CustomSpacing.Builder()
      .setNone(pLCURLY, pRCURLY)
      .setNone(kSTRUCT, pLCURLY)
      .build();

  private Alignment leadingAlignment = null;

  GoTypeStructBlock(@NotNull GoPsiTypeStruct node,
                    CommonCodeStyleSettings settings,
                    Alignment alignment,
                    @NotNull Map<Alignments.Key, Alignment> alignmentsMap) {
    super(node, settings, null, null, alignmentsMap);

    this.leadingAlignment = alignment;
    setMultiLineMode(StringUtil.containsLineBreak(node.getText()), pLCURLY, pRCURLY);
    setLineBreakingTokens(LINE_BREAKING_TOKS);
    setAlignmentKeys(ALIGNMENT_KEYS);
    if(!isMultiLine())
      setCustomSpacing(CUSTOM_SPACING_RULES);
  }

  @Nullable
  @Override
  protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild,
                                        Map<Alignments.Key, Alignment> alignments) {
    if (child.getNode().getElementType() == kSTRUCT)
      return leadingAlignment;

    return super.getChildAlignment(child, prevChild, alignments);
  }

  @Override
  protected Indent getChildIndent(@NotNull PsiElement child, @Nullable PsiElement prevChild) {
    if (
      child instanceof GoTypeStructField ||
      child instanceof GoTypeStructAnonymousField ||
      child instanceof PsiComment)
      return Indents.NORMAL;

    return Indents.NONE;
  }

  @Override
  protected boolean holdTogether(@Nullable IElementType typeChild1, @Nullable IElementType typeChild2,
                                 int linesBetween) {
    if (linesBetween <= 1 &&
      TYPE_STRUCT_FIELDS.contains(typeChild1) &&
      TYPE_STRUCT_FIELDS.contains(typeChild2))
      return true;

    return super.holdTogether(typeChild1, typeChild2, linesBetween);
  }
}
