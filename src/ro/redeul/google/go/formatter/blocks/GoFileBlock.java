package ro.redeul.google.go.formatter.blocks;

import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: 6/3/12
 */
class GoFileBlock extends GoSyntheticBlock<GoFile> {

  public GoFileBlock(GoFile goFile, CommonCodeStyleSettings settings) {
    super(goFile, settings, GoBlockUtil.Indents.NONE_ABSOLUTE, GoBlockUtil.Alignments.one(), GoBlockUtil.Alignments.EMPTY_MAP);
  }

  private static final TokenSet NEED_NEW_LINE_TOKENS = TokenSet.create(
    PACKAGE_DECLARATION,
    IMPORT_DECLARATIONS,
    CONST_DECLARATIONS,
    VAR_DECLARATIONS,
    TYPE_DECLARATIONS,
    FUNCTION_DECLARATION,
    METHOD_DECLARATION,
    mSL_COMMENT,
    mML_COMMENT
  );

  protected boolean wantsToBreakLine(IElementType typeChild) {
    return NEED_NEW_LINE_TOKENS.contains(typeChild);
  }

  @Override
  protected boolean isMultiLine() {
    return true;
  }
}

