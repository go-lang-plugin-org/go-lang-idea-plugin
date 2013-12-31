package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Indent;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;

import java.util.EnumSet;
import java.util.Set;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;

/**
 * Variable declaration formatting block.
 *
 * <p/>
 * Created on Dec-30-2013 16:58
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoVarsBlock extends GoSyntheticBlock<GoVarDeclarations> {

  private static Set<Alignments.Key> ALIGN_KEYS = EnumSet.of(
    Alignments.Key.Type,
    Alignments.Key.Operator,
    Alignments.Key.Value,
    Alignments.Key.Comments
  );

  private static final TokenSet LINE_BREAKING_TOKENS = TokenSet.create(
    VAR_DECLARATION,
    mML_COMMENT,
    mSL_COMMENT
  );

  public GoVarsBlock(GoVarDeclarations vars, CommonCodeStyleSettings settings) {
    super(vars, settings, Indents.NONE);

    setMultiLineMode(vars.isMulti(), pLPAREN, pRPAREN);
    setAlignmentKeys(ALIGN_KEYS);
    setLineBreakingTokens(LINE_BREAKING_TOKENS);
  }

  @Nullable
  @Override
  protected Indent getChildIndent(@NotNull PsiElement child, @Nullable PsiElement prevChild) {
    if (isMultiLine()) {
      if ( child instanceof GoVarDeclaration || child instanceof PsiComment)
        return Indents.NORMAL_RELATIVE;
    }

    return Indents.NONE;
  }
}
