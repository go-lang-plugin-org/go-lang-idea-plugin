package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: 6/3/12
 */
public class File extends Code<GoFile> {

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

    public File(GoFile goFile, CommonCodeStyleSettings settings) {
        super(goFile, settings, Indents.NONE_ABSOLUTE, null, Alignments.EMPTY_MAP);

        setMultiLineMode(true, null, null);
        withDefaultSpacing(GoBlockUtil.Spacings.LINE_HOLD_BREAKS);
        setLineBreakingTokens(LINE_BREAKING_TOKENS);
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(Indents.NONE, null);
    }

    @Override
    protected Indent getChildIndent(@NotNull PsiElement child, @Nullable PsiElement prevChild) {
        return Indents.NONE;
    }
}

