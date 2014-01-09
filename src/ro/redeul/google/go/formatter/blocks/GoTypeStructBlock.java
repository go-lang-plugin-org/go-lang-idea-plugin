package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;

import java.util.EnumSet;
import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;

class GoTypeStructBlock extends GoSyntheticBlock<GoPsiTypeStruct> {

    private static final TokenSet LINE_BREAKING_TOKS = TokenSet.create(
        mML_COMMENT, mSL_COMMENT,
        TYPE_STRUCT_FIELD, TYPE_STRUCT_FIELD_ANONYMOUS
    );

    private static final EnumSet<Alignments.Key> ALIGNMENT_KEYS = EnumSet.of(
        Alignments.Key.Comments,
        Alignments.Key.Type
    );

    private static final GoBlockUtil.CustomSpacing EMPTY_SPACING_RULES =
        GoBlockUtil.CustomSpacing.Builder()
            .setNone(pLCURLY, pRCURLY)
            .setNone(kSTRUCT, pLCURLY)
            .build();

    private static final GoBlockUtil.CustomSpacing NON_EMPTY_SPACING_RULES =
        GoBlockUtil.CustomSpacing.Builder()
            .set(pLCURLY, pRCURLY, GoBlockUtil.Spacings.ONE_LINE)
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
        setIndentedChildTokens(LINE_BREAKING_TOKS);
        setAlignmentKeys(ALIGNMENT_KEYS);
        setHoldTogetherGroups(COMMENTS, TYPE_STRUCT_FIELDS);
        setCustomSpacing(isMultiLine() ? NON_EMPTY_SPACING_RULES : EMPTY_SPACING_RULES);
    }

    @Nullable
    @Override
    protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild,
                                          Map<Alignments.Key, Alignment> alignments) {
        if (child.getNode().getElementType() == kSTRUCT)
            return leadingAlignment;

        return super.getChildAlignment(child, prevChild, alignments);
    }
}
