package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeChannel;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 1:22:29 PM
 */
public class GoTypeChannelImpl extends GoPsiPackagedElementBase implements GoTypeChannel {

    private ChannelType channelType;

    public GoTypeChannelImpl(@NotNull ASTNode node, ChannelType channelType) {
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

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return GoUnderlyingType.Undefined;
    }

    @Override
    public boolean isIdentical(GoType goType) {
        if (!(goType instanceof GoTypeChannel))
            return false;

        GoTypeChannel otherChannel = (GoTypeChannel) goType;
        return this.getChannelType() == otherChannel.getChannelType() &&
                    this.getElementType().isIdentical(otherChannel.getElementType());
    }
}
