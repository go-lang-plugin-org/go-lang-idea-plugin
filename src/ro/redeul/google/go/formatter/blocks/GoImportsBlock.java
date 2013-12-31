package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Indent;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;

/**
 * TODO: Document this
 * <p/>
 * Created on Dec-30-2013 16:58
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoImportsBlock extends GoSyntheticBlock<GoImportDeclarations> {

  boolean hasMultipleImports;

  public GoImportsBlock(GoImportDeclarations imports,
                        CommonCodeStyleSettings settings) {
    super(imports, settings, Indents.NONE, null);

    hasMultipleImports = imports.isMulti();
  }

  @Override
  protected boolean isMultiLine() {
    return hasMultipleImports;
  }

  @Override
  protected boolean isLeftBreak(IElementType typeChild) {
    return typeChild == pLPAREN;
  }

  @Override
  protected boolean isRightBreak(IElementType typeChild) {
    return typeChild == pRPAREN;
  }

  private static final TokenSet WANT_BREAK_TOKS = TokenSet.create(
    IMPORT_DECLARATION,
    mML_COMMENT,
    mSL_COMMENT
  );

  @Override
  protected boolean wantsBreakup(IElementType typeChild1) {
    return WANT_BREAK_TOKS.contains(typeChild1);
  }

  @Nullable
  @Override
  protected Indent getChildIndent(@NotNull PsiElement child, @Nullable PsiElement prevChild) {
    if (isMultiLine()) {
      if ( child instanceof GoImportDeclaration || child instanceof PsiComment)
        return Indents.NORMAL_RELATIVE;
    }

    return Indents.NONE;
  }

}
