package ro.redeul.google.go.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 27, 2010
 * Time: 7:02:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoBlock implements Block, GoElementTypes {

    final protected ASTNode myNode;
    final protected Alignment myAlignment;
    final protected Indent myIndent;
    final protected Wrap myWrap;
    final protected CodeStyleSettings mySettings;

    protected List<Block> mySubBlocks = null;

    public GoBlock(ASTNode node, Alignment alignment, Indent indent, Wrap wrap, CodeStyleSettings settings) {
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
    public CodeStyleSettings getSettings() {
      return mySettings;
    }

    @NotNull
    public TextRange getTextRange() {
        return myNode.getTextRange();
    }

    @NotNull
    public List<Block> getSubBlocks() {
        if (mySubBlocks == null) {
          mySubBlocks = GoBlockGenerator.generateSubBlocks(myNode, myAlignment, myWrap, mySettings, this);
        }

        return mySubBlocks;
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
//        if ((child1 instanceof GroovyBlock) && (child2 instanceof GroovyBlock)) {
//          Spacing spacing = GroovySpacingProcessor.getSpacing(((GroovyBlock) child1), ((GroovyBlock) child2), mySettings);
//          return spacing != null ? spacing : GroovySpacingProcessorBasic.getSpacing(((GroovyBlock) child1), ((GroovyBlock) child2), mySettings);
//        }
//        return null;

        return null;
    }

    @NotNull
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(Indent.getContinuationIndent(), null);
    }

    public boolean isIncomplete() {
        return false;
    }

    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }
}
