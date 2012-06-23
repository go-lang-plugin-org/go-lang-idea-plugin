package ro.redeul.google.go.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.WrapType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import ro.redeul.google.go.formatter.blocks.GoBlock;
import ro.redeul.google.go.formatter.blocks.GoFileBlock;
import ro.redeul.google.go.formatter.blocks.GoLeafBlock;
import ro.redeul.google.go.formatter.blocks.GoPackageBlock;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Sep 27, 2010
 */
public class GoBlockGenerator {

    public static final Wrap NO_WRAP = Wrap.createWrap(WrapType.NONE, false);

    public static Block generateBlock(ASTNode node,
                                      CommonCodeStyleSettings settings) {
        return generateBlock(node, Indent.getNoneIndent(), settings);
    }


    public static Block generateBlock(ASTNode node, Indent indent, CommonCodeStyleSettings styleSettings) {
        PsiElement psi = node.getPsi();
        if (psi instanceof GoFile)
            return generateGoFileBlock(node, styleSettings);

        if (psi instanceof GoPackageDeclaration)
            return generatePackageBlock(node, styleSettings);

        if (node.getElementType() == GoTokenTypes.kPACKAGE ||
            node.getElementType() == GoTokenTypes.oSEMI) {
            return new GoLeafBlock(node,
                                   null,
                                   Indent.getAbsoluteNoneIndent(),
                                   Wrap.createWrap(WrapType.NONE, false),
                                   styleSettings);
        }

        return new GoBlock(node, null, indent, NO_WRAP, styleSettings);
    }

    private static Block generatePackageBlock(ASTNode node,
                                              CommonCodeStyleSettings settings) {
        return new GoPackageBlock(node,
                                  Alignment.createAlignment(),
                                  Indent.getNoneIndent(),
                                  Wrap.createWrap(WrapType.NONE, false),
                                  settings);
    }

    private static Block generateGoFileBlock(ASTNode node,
                                             CommonCodeStyleSettings settings) {
        return new GoFileBlock(node,
                               Alignment.createAlignment(),
                               Indent.getAbsoluteNoneIndent(),
                               Wrap.createWrap(WrapType.NONE, false),
                               settings);
    }
}
