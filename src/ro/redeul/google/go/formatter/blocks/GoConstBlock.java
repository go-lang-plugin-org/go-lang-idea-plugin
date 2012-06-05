/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.formatter.blocks;

import java.util.ArrayList;
import java.util.List;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.WrapType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.formatter.GoBlockGenerator;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;

/**
 * // TODO: Explain yourself.
 *
 * @author Mihai Claudiu Toader <mtoader@midokura.com>
 *         Date: 6/9/12
 */
public class GoConstBlock extends GoPsiBlock<GoConstDeclarations> {

    public GoConstBlock(GoConstDeclarations goConstDeclarations,
                        Alignment alignment, Indent indent,
                        Wrap wrap, CommonCodeStyleSettings settings) {
        super(goConstDeclarations, alignment, indent, wrap, settings);
    }

    Alignment childAlign;

    @NotNull
    @Override
    public List<Block> getSubBlocks() {

        if (mySubBlocks == null) {
            mySubBlocks = new ArrayList<Block>();

            int childs = psi().getDeclarations().length;
            ASTNode[] children = getGoChildren(getNode());

            boolean makeIndent = false;

            Wrap wrap = GoBlockGenerator.NO_WRAP;

            childAlign = Alignment.createChildAlignment(myAlignment);
            for (ASTNode child : children) {
                if (child.getElementType() == GoTokenTypes.pRPAREN && childs > 0) {
                    wrap = Wrap.createWrap(WrapType.ALWAYS, true);
                    makeIndent = false;
                }

                Indent indent = makeIndent
                    ? Indent.getNormalIndent()
                    : getIndent();

                mySubBlocks.add(
                    GoBlockGenerator.generateBlock(child, makeIndent ? childAlign : null,
                                                   indent,
                                                   wrap,
                                                   mySettings));

                wrap = GoBlockGenerator.NO_WRAP;
                if (child.getElementType() == GoTokenTypes.pLPAREN && childs > 0) {
                    makeIndent = true;
                    wrap = Wrap.createWrap(WrapType.ALWAYS, true);
                }
            }
        }

        return mySubBlocks;
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(Indent.getNormalIndent(), childAlign);
    }
}
