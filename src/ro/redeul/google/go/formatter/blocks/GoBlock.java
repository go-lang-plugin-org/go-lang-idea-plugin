package ro.redeul.google.go.formatter.blocks;

import java.util.ArrayList;
import java.util.List;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.formatter.GoBlockGenerator;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Sep 27, 2010
 */
public abstract class GoBlock implements Block, GoElementTypes {

    final protected ASTNode myNode;
    final protected Alignment myAlignment;
    final protected Indent myIndent;
    final protected Wrap myWrap;
    final protected CommonCodeStyleSettings mySettings;

    final static TokenSet GO_BLOCK_ELEMENTS =
        TokenSet.not(
            TokenSet.create(
                GoTokenTypes.wsNLS, GoTokenTypes.wsWS, TokenType.WHITE_SPACE));

    protected List<Block> mySubBlocks = null;

    protected final Spacing basicSpacing = Spacing.createSpacing(1, 1, 0, false, 0);

    public GoBlock(ASTNode node, Alignment alignment, Indent indent, Wrap wrap,
                   CommonCodeStyleSettings settings) {
        myNode = node;
        myAlignment = alignment;
        myIndent = indent;
        myWrap = wrap;
        mySettings = settings;

    }

    @NotNull
    public ASTNode getNode() {
        return myNode;
    }

    @NotNull
    public TextRange getTextRange() {
        return myNode.getTextRange();
    }

    @NotNull
    public List<Block> getSubBlocks() {

        if (mySubBlocks == null) {
            mySubBlocks = new ArrayList<Block>();
            ASTNode[] children = getGoChildren(myNode);

            Block childBlock = null;
            for (ASTNode child : children) {
                if (getIndentedElements().contains(child.getElementType())) {
                    childBlock =
                        GoBlockGenerator.generateBlock(
                            child, Indent.getNormalIndent(), mySettings);
                } else {
                    childBlock =
                        GoBlockGenerator.generateBlock(
                            child, mySettings);
                }

                mySubBlocks.add(childBlock);
            }
        }

        return mySubBlocks;
    }

    protected TokenSet getIndentedElements() {
        return TokenSet.EMPTY;
    }

    public Wrap getWrap() {
        return myWrap;
    }

    public Indent getIndent() {
        return myIndent;
    }

    public Alignment getAlignment() {
        return myAlignment;
    }

    public Spacing getSpacing(Block child1, Block child2) {
        return null;
    }

    @NotNull
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    public boolean isIncomplete() {
        return false;
    }

    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

    protected ASTNode[] getGoChildren(ASTNode node) {
        PsiElement psi = node.getPsi();
        if (psi instanceof OuterLanguageElement) {
            TextRange range = node.getTextRange();
            List<ASTNode> childList = new ArrayList<ASTNode>();
            PsiFile goFile = psi.getContainingFile()
                                .getViewProvider()
                                .getPsi(GoLanguage.INSTANCE);
            if (goFile instanceof GoFile) {
                addChildNodes(goFile, childList, range);
            }

            return childList.toArray(new ASTNode[childList.size()]);
        }

        return node.getChildren(GO_BLOCK_ELEMENTS);
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
