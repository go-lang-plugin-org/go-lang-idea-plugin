/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.formatter.blocks.GoBlock;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: 6/3/12
 */
public class GoBlockWithIdentedChilds extends GoBlock {

    public GoBlockWithIdentedChilds(ASTNode node, Alignment alignment,
                                    Indent indent, Wrap wrap,
                                    CodeStyleSettings settings)
    {
        super(node, alignment, indent, wrap, settings);
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(Indent.getNormalIndent(true), null);
    }
}
