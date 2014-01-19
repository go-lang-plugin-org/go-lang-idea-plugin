package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.parser.GoElementTypes;

/**
 * <p/>
 * Created on Jan-13-2014 23:13
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public abstract class Base implements ASTBlock, GoElementTypes {

    protected final ASTNode myASTNode;
    protected final CommonCodeStyleSettings mySettings;
    protected final Indent myIndent;
    protected final Wrap myWrap;
    protected final Alignment myAlignment;

    public Base(@NotNull ASTNode node, CommonCodeStyleSettings settings,
                Indent indent, Alignment alignment) {
        this(node, settings, indent, null, alignment);
    }

    public Base(@NotNull ASTNode node, CommonCodeStyleSettings settings,
                Indent indent, Wrap wrap, Alignment alignment) {
        this.myASTNode = node;
        this.mySettings = settings;
        this.myIndent = indent;
        this.myWrap = wrap;
        this.myAlignment = alignment;
    }

    @NotNull
    public ASTNode getNode() {
        return myASTNode;

    }

    @NotNull
    public TextRange getTextRange() {
        return myASTNode.getTextRange();
    }

    @Nullable
    @Override
    public Wrap
    getWrap() {
        return myWrap;
    }

    @Nullable
    @Override
    public Indent getIndent() {
        return myIndent;
    }

    @Nullable
    @Override
    public Alignment getAlignment() {
        return myAlignment;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(GoBlockUtil.Indents.NONE, null);
    }
}
