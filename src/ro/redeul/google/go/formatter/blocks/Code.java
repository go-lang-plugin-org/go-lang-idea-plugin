package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.formatter.builder.Builder;
import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;

import java.util.*;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Spacings;
import static ro.redeul.google.go.formatter.GoFormatterUtil.*;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isWhiteSpaceNode;

/**
 * Base go formatting block.
 * <p/>
 * <p/>
 * Created on Dec-30-2013 22:41
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class Code<GoPsiType extends GoPsiElement> extends Base {

    private final GoPsiType myPsiNode;

    private Spacing myDefaultSpacing = null;

    private List<Block> mySubBlocks = null;
    private Map<Alignments.Key, Alignment> knownAlignments = null;

    // multiline mode support
    private boolean myMultiLineMode = false;
    private IElementType myLeftBreakElement = null;
    private IElementType myRightBreakElement = null;

    // column alignment support
    private Set<Alignments.Key> myAlignmentKeys = Alignments.EMPTY_KEY_SET;

    // vertical line breaking tokens
    private TokenSet myLineBreakingTokens = TokenSet.EMPTY;

    // indented child blocks
    private TokenSet myIndentedChildBlocks = TokenSet.EMPTY;

    // vertical grouping support
    private TokenSet[] myHoldTogetherGroups = new TokenSet[]{COMMENTS};

    // custom spacing entries;
    private GoBlockUtil.CustomSpacing myCustomSpacing;

    private Indent leadingCommentGroupIndent = Indents.NONE;
    private Alignment leadingCommentGroupAlign = null;

    public Code(@NotNull GoPsiType myNode, CommonCodeStyleSettings mySettings) {
        this(myNode, mySettings, null, Alignments.NONE, Alignments.EMPTY_MAP);
    }

    public Code(@NotNull GoPsiType myNode, CommonCodeStyleSettings mySettings,
                Indent indent) {
        this(myNode, mySettings, indent, Alignments.NONE, Alignments.EMPTY_MAP);
    }

    public Code(@NotNull GoPsiType node, CommonCodeStyleSettings settings,
                Indent indent, Alignment alignment,
                @NotNull Map<Alignments.Key, Alignment> alignsToUse) {
        super(node.getNode(), settings, indent, alignment);

        this.myPsiNode = node;
        this.knownAlignments = alignsToUse;
    }

    protected GoPsiType getPsi() {
        return myPsiNode;
    }

    public Code<GoPsiType> setMultiLineMode(boolean multilineMode,
                                                      IElementType leftBreakElement,
                                                      IElementType rightBreakElement) {
        this.myMultiLineMode = multilineMode;
        this.myLeftBreakElement = leftBreakElement;
        this.myRightBreakElement = rightBreakElement;
        return this;
    }

    protected void withAlignmentKeys(@NotNull Set<Alignments.Key> alignmentKeys) {
        this.myAlignmentKeys = alignmentKeys;
    }

    public Code<GoPsiType> setLineBreakingTokens(@NotNull TokenSet lineBreakingTokens) {
        this.myLineBreakingTokens = lineBreakingTokens;
        return this;
    }

    public Code<GoPsiType> setIndentedChildTokens(@NotNull TokenSet indentedChildTokens) {
        this.myIndentedChildBlocks = indentedChildTokens;
        return this;
    }

    public Code<GoPsiType> withDefaultSpacing(@NotNull Spacing defaultSpacing) {
        this.myDefaultSpacing = defaultSpacing;
        return this;
    }

    @NotNull
    protected Set<Alignments.Key> getAlignmentKeys() {
        return myAlignmentKeys;
    }

    protected Block customizeBlock(@NotNull Block childBlock, @NotNull PsiElement childPsi) {
        return childBlock;
    }

    public Code<GoPsiType> withCustomSpacing(@NotNull GoBlockUtil.CustomSpacing customSpacing) {
        this.myCustomSpacing = customSpacing;
        return this;
    }

    @Nullable
    protected Spacing getCustomSpacing(@Nullable IElementType typeChild1,
                                       @Nullable IElementType typeChild2) {
        return myCustomSpacing != null
            ? myCustomSpacing.getSpacingBetween(typeChild1, typeChild2)
            : null;
    }

    protected Code<GoPsiType> setLeadingCommentGroupAlignment(Alignment alignment) {
        this.leadingCommentGroupAlign = alignment;
        return this;
    }

    protected Code<GoPsiType> setLeadingCommentGroupIndent(Indent indent) {
        this.leadingCommentGroupIndent = indent;
        return this;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable com.intellij.formatting.Block child1,
                              @NotNull com.intellij.formatting.Block child2) {
        IElementType typeChild1 = getASTElementType(child1);
        IElementType typeChild2 = getASTElementType(child2);

        if (child1 == null)
            return Spacings.NONE;

        if (child1 instanceof CommentGroupPart ||
            child2 instanceof CommentGroupPart)
            return Spacings.LINE_HOLD_BREAKS;

        Spacing customSpacing = getCustomSpacing(typeChild1, typeChild2);
        if (customSpacing != null)
            return customSpacing;

        if (typeChild1 == mSL_COMMENT)
            return Spacings.LINE_HOLD_BREAKS;

        if (!isMultiLine())
            return myDefaultSpacing;

        if (isMultiLine() && isLeftBreak(typeChild1) && isRightBreak(typeChild2))
            return Spacings.LINE_HOLD_BREAKS;

        if (isMultiLine() && (isLeftBreak(typeChild1) || isRightBreak(typeChild2)))
            return Spacings.LINE;

        if (!wantsToBreakLine(typeChild1))
            return typeChild1 == mSL_COMMENT ? Spacings.LINE : myDefaultSpacing;

        if (holdTogether(typeChild1, typeChild2, getLineCount(getTextBetween(child1, child2))))
            return Spacings.LINE;

        return Spacings.EMPTY_LINE;
    }

    protected boolean isRightBreak(IElementType typeChild) { return typeChild == myRightBreakElement; }

    protected boolean isLeftBreak(IElementType typeChild) { return typeChild == myLeftBreakElement; }

    protected boolean isMultiLine() { return myMultiLineMode; }

    protected boolean wantsToBreakLine(IElementType child) {
        return this.myLineBreakingTokens.contains(child);
    }

    protected boolean holdTogether(@Nullable IElementType typeChild1,
                                   @Nullable IElementType typeChild2,
                                   int linesBetween) {
        if (linesBetween > 1)
            return false;

        if (typeChild1 == typeChild2)
            return true;

        if (myHoldTogetherGroups != null)
            for (TokenSet holdTogetherGroup : myHoldTogetherGroups)
                if (holdTogetherGroup != null &&
                    holdTogetherGroup.contains(typeChild1) &&
                    holdTogetherGroup.contains(typeChild2))
                    return true;

        return false;
    }

    public Code<GoPsiType> setHoldTogetherGroups(TokenSet... holdTogetherGroups) {
        this.myHoldTogetherGroups = holdTogetherGroups;
        return this;
    }

    @Nullable
    protected Alignment getChildAlignment(@NotNull PsiElement child,
                                          @Nullable PsiElement prevChild,
                                          Map<Alignments.Key, Alignment> alignments) {
        return null;
    }

    protected Indent getChildIndent(@NotNull PsiElement child, @Nullable PsiElement prevChild) {
        return
            prevChild != null
                ? myIndentedChildBlocks.contains(child.getNode().getElementType())
                    ? Indents.NORMAL
                    : Indents.NONE
                : Indents.NONE;
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return newChildIndex == 0
            ? new ChildAttributes(Indents.NONE, null)
            : ChildAttributes.DELEGATE_TO_NEXT_CHILD;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Nullable
    protected List<Block> buildChildren() {
        List<Block> children = new ArrayList<Block>();

        Map<Alignments.Key, Alignment> alignmentsMap = Alignments.set(getAlignmentKeys());

        ASTNode prevChild = null;
        IElementType prevChildType = null;

        boolean isPartOfLeadingCommentGroup = getPsi() instanceof GoDocumentedPsiElement;
        for (ASTNode child : getGoChildren()) {
            PsiElement childPsi = child.getPsi();

            if (child.getTextRange().getLength() == 0 || isWhiteSpaceNode(childPsi))
                continue;

            if (!(childPsi instanceof PsiComment))
                isPartOfLeadingCommentGroup = false;

            int linesBetween = getLineCount(getTextBetween(prevChild, child));

            PsiElement prevChildPsi = prevChild != null ? prevChild.getPsi() : null;

            Indent childIndent = isPartOfLeadingCommentGroup
                ? leadingCommentGroupIndent
                : getChildIndent(childPsi, prevChildPsi);

            Alignment childAlign = isPartOfLeadingCommentGroup
                ? leadingCommentGroupAlign
                : getChildAlignment(childPsi, prevChildPsi, knownAlignments);

            if (wantsToBreakLine(prevChildType) &&
                !holdTogether(prevChildType, child.getElementType(), linesBetween))
                alignmentsMap = Alignments.set(getAlignmentKeys());

            Block childBlock = Builder.make(child, mySettings,
                childIndent, childAlign, alignmentsMap,
                isPartOfLeadingCommentGroup);

            children.add(customizeBlock(childBlock, childPsi));

            prevChild = child;
            prevChildType = getASTElementType(prevChild);
        }

        return children;
    }

    @NotNull
    public List<Block> getSubBlocks() {
        if (mySubBlocks == null) {
            List<Block> children = buildChildren();
            if (children == null || children.isEmpty()) {
                mySubBlocks = Collections.emptyList();
            } else {
                mySubBlocks = children;
            }
        }

        return mySubBlocks;
    }

    protected ASTNode[] getGoChildren() {
        PsiElement psi = myASTNode.getPsi();
        if (psi instanceof OuterLanguageElement) {
            TextRange range = myASTNode.getTextRange();
            List<ASTNode> childList = new ArrayList<ASTNode>();
            PsiFile goFile = psi.getContainingFile().getViewProvider().getPsi(GoLanguage.INSTANCE);

            if (goFile instanceof GoFile) {
                addChildNodes(goFile, childList, range);
            }

            return childList.toArray(new ASTNode[childList.size()]);
        }

        return myASTNode.getChildren(null);
    }

    private static void addChildNodes(PsiElement elem,
                                      List<ASTNode> childNodes,
                                      TextRange range) {
        ASTNode node = elem.getNode();
        if (range.contains(elem.getTextRange()) && node != null) {
            childNodes.add(node);
        } else {
            for (PsiElement child : elem.getChildren()) {
                addChildNodes(child, childNodes, range);
            }
        }
    }
}
