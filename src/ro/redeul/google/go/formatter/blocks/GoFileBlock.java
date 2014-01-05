package ro.redeul.google.go.formatter.blocks;

import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.psi.GoFile;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: 6/3/12
 */
class GoFileBlock extends GoSyntheticBlock<GoFile> {

    private static final TokenSet LINE_BREAKING_TOKENS = TokenSet.create(
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

    public GoFileBlock(GoFile goFile, CommonCodeStyleSettings settings) {
        super(goFile, settings, Indents.NONE_ABSOLUTE, null, Alignments.EMPTY_MAP);

        setMultiLineMode(true, null, null);
        setLineBreakingTokens(LINE_BREAKING_TOKENS);
    }
}

