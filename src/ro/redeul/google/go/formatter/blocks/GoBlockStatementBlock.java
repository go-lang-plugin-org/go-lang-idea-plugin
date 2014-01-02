package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;

class GoBlockStatementBlock extends GoSyntheticBlock<GoBlockStatement> {

  private static final GoBlockUtil.CustomSpacing MULTI_LINE_SPACING =
    GoBlockUtil.CustomSpacing.Builder()
      .set(pLCURLY, pRCURLY, Spacing.createSpacing(0, 0, 1, true, 1))
      .build();

  private static final GoBlockUtil.CustomSpacing SAME_LINE_SPACING =
    GoBlockUtil.CustomSpacing.Builder()
      .setNone(pLCURLY, pRCURLY)
      .build();

  public GoBlockStatementBlock(GoBlockStatement blockStatement,
                               CommonCodeStyleSettings settings,
                               Indent indent) {
    super(blockStatement, settings, indent);

    setMultiLineMode(StringUtil.containsLineBreak(blockStatement.getText()), pLCURLY, pRCURLY);

    setLineBreakingTokens(GoElementTypes.STATEMENTS);
    if (isMultiLine()) {
      setCustomSpacing(MULTI_LINE_SPACING);
    } else {
      setCustomSpacing(SAME_LINE_SPACING);
    }
  }

  @Override
  protected Indent getChildIndent(@NotNull PsiElement child, @Nullable PsiElement prevChild) {
    if ( GoElementTypes.STATEMENTS.contains(child.getNode().getElementType()))
      return Indents.NORMAL;

    return super.getChildIndent(child, prevChild);
  }

  //    @Override
//    protected Indent getChildIndent(@Nullable PsiElement child) {
//        if (child == null) {
//            return Indent.getNormalIndent();
//        }
//
//        String text = child.getText();
//        if ("{".equals(text) || "}".equals(text)) {
//            return Indent.getNoneIndent();
//        }
//        return Indent.getNormalIndent();
//    }
//
//    @Override
//    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
//        if (child1 instanceof GoBlockStatementBlock || child2 instanceof GoBlockStatementBlock) {
//            return ONE_LINE_SPACING_KEEP_LINE_BREAKS;
//        }
//        return super.getGoBlockSpacing(child1, child2);
//    }
}
