package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments.Key;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;

/**
 * <p/>
 * Created on Dec-30-2013 16:58
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoTypesBlock extends GoSyntheticBlock<GoTypeDeclaration> {

    private static final Set<Key> ALIGN_KEYS = EnumSet.of(Key.Type, Key.Comments);

    private static final TokenSet LINE_BREAKING_TOKENS = TokenSet.create(
        TYPE_DECLARATION,
        mML_COMMENT,
        mSL_COMMENT
    );

    public GoTypesBlock(GoTypeDeclaration types,
                        CommonCodeStyleSettings settings,
                        Indent indent,
                        Map<GoBlockUtil.Alignments.Key, Alignment> alignmentMap) {
        super(types, settings, indent, null, alignmentMap);

        setMultiLineMode(types.isMulti(), pLPAREN, pRPAREN);
        setLineBreakingTokens(LINE_BREAKING_TOKENS);
        setAlignmentKeys(ALIGN_KEYS);
    }

    @Nullable
    @Override
    protected Indent getChildIndent(@NotNull PsiElement child, @Nullable PsiElement prevChild) {
        if (isMultiLine()) {
            if (child instanceof GoTypeSpec || child instanceof PsiComment)
                return Indents.NORMAL_RELATIVE;
        }

        return Indents.NONE;
    }

    @Nullable
    @Override
    protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild,
                                          Map<GoBlockUtil.Alignments.Key, Alignment> alignments) {
        if (child instanceof PsiComment)
            return alignments.get(GoBlockUtil.Alignments.Key.Comments);

        return super.getChildAlignment(child, prevChild, alignments);
    }

}
