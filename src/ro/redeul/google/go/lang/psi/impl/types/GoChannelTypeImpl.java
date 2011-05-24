package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoChannelType;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 2, 2010
 * Time: 1:22:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoChannelTypeImpl extends GoPsiElementBase implements GoChannelType {

    private ChannelType channelType;

    public GoChannelTypeImpl(@NotNull ASTNode node, ChannelType channelType) {
        super(node);
        this.channelType = channelType;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public GoType getElementType() {
        return findChildByClass(GoType.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitChannelType(this);
    }
}
