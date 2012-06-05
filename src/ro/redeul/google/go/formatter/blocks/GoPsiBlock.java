/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.formatter.blocks;

import java.util.List;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * // TODO: Explain yourself.
 *
 * @author Mihai Claudiu Toader <mtoader@midokura.com>
 *         Date: 6/9/12
 */
public abstract class GoPsiBlock<GoPsi extends GoPsiElement>
    extends GoBlock
{

    public GoPsiBlock(GoPsi node, Alignment alignment, Indent indent,
                      Wrap wrap, CommonCodeStyleSettings settings) {
        super(node.getNode(), alignment, indent, wrap, settings);
    }


    protected GoPsi psi() {
        //noinspection unchecked
        return (GoPsi) getNode().getPsi();
    }

    @NotNull
    @Override
    public List<Block> getSubBlocks() {
        return super.getSubBlocks();
    }
}
