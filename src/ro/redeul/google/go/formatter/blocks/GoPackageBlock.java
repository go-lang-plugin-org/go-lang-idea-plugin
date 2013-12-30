package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;

class GoPackageBlock extends GoBlock {

    private Runnable a = new Runnable() {
        @Override
        public void run() {

        }
    };
    private float    b = 10;

    String c = "dsfas" + "adfa" +
            "asdfasdf" + "adfa" + "asdfasdf" + "adfa" + "asdfasdf" + "adfa" +
            "asdfasdf" + "adfa" + "asdfasdf" + "adfa" + "asdfasdf" + "adfa" + "asdfasdf";
    int    x = 10;


    public GoPackageBlock(ASTNode node, Alignment alignment, Indent indent,
                          Wrap wrap, CommonCodeStyleSettings settings) {
        super(node, alignment, indent, wrap, settings);
    }

    @Override
    public Spacing getSpacing(Block child1, @NotNull Block child2) {
        if ( child1 != null && child1 instanceof GoBlock ) {
            GoBlock block1 = (GoBlock) child1;
            if ( block1.getNode() != null && block1.getNode().getElementType() == GoTokenTypes.kPACKAGE) {
                return BASIC_SPACING;
            }
        }

        return EMPTY_SPACING;
    }
}
