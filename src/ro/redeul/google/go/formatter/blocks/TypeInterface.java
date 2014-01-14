package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;

import java.util.EnumSet;
import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Spacings;

public class TypeInterface extends Code<GoPsiTypeInterface> {

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

    private static final GoBlockUtil.CustomSpacing EMPTY_SPACING_RULES =
        GoBlockUtil.CustomSpacing.Builder()
            .setNone(pLCURLY, pRCURLY)
            .setNone(kINTERFACE, pLCURLY)
            .build();

    private static final GoBlockUtil.CustomSpacing NON_EMPTY_SPACING_RULES =
        GoBlockUtil.CustomSpacing.Builder()
            .set(pLCURLY, pRCURLY, Spacings.LINE)
            .build();

    public TypeInterface(GoPsiTypeInterface psi, CommonCodeStyleSettings settings,
                         Alignment alignment,
                         @NotNull Map<Alignments.Key, Alignment> alignmentsMap) {
        super(psi, settings, null, null, alignmentsMap);

        this.leadingAlignment = alignment;
        setMultiLineMode(StringUtil.containsLineBreak(psi.getText()), pLCURLY, pRCURLY);
        setLineBreakingTokens(LINE_BREAKING_TOKS);
        setIndentedChildTokens(LINE_BREAKING_TOKS);
        setAlignmentKeys(ALIGNMENT_KEYS);
        setHoldTogetherGroups(COMMENTS, SIMILAR_NODES);

        setCustomSpacing(isMultiLine() ? NON_EMPTY_SPACING_RULES : EMPTY_SPACING_RULES);
    }

    @Nullable
    @Override
    protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild,
                                          Map<Alignments.Key, Alignment> alignments) {
        if (child.getNode().getElementType() == kINTERFACE)
            return leadingAlignment;

        return super.getChildAlignment(child, prevChild, alignments);
    }
}
