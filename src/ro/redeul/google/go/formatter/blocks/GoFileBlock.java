/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: 6/3/12
 */
public class GoFileBlock extends GoBlock {
    public GoFileBlock(ASTNode node, Alignment alignment, Indent indent,
                       Wrap wrap, CommonCodeStyleSettings settings) {
        super(node, alignment, indent, wrap, settings);
    }
}
