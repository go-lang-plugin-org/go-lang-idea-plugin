package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-13-2014 23:32
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class Leaf extends Base {

    public Leaf(@NotNull ASTNode node, Indent indent, Alignment alignment) {
        super(node, null, indent, alignment);
    }

    @NotNull
    @Override
    public List<Block> getSubBlocks() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return null;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
