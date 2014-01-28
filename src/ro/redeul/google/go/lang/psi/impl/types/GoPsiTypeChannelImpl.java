package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.typing.GoTypeChannel;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 1:22:29 PM
 */
public class GoPsiTypeChannelImpl extends GoPsiPackagedElementBase implements
        GoPsiTypeChannel {

    private final GoTypeChannel.ChannelType channelType;

    public GoPsiTypeChannelImpl(@NotNull ASTNode node, GoTypeChannel.ChannelType channelType) {
        super(node);
        this.channelType = channelType;
    }

    public GoTypeChannel.ChannelType getChannelType() {
        return channelType;
    }

    public GoPsiType getElementType() {
        return findChildByClass(GoPsiType.class);
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
    public boolean isIdentical(GoPsiType goType) {
        if (!(goType instanceof GoPsiTypeChannel))
            return false;

        GoPsiTypeChannel otherChannel = (GoPsiTypeChannel) goType;
        GoPsiType elementType = this.getElementType();
        if (elementType == null)
            return false;
        GoTypeChannel.ChannelType chanType = this.getChannelType();
        GoTypeChannel.ChannelType otherChanType = otherChannel.getChannelType();
        return (chanType == otherChanType || chanType == GoTypeChannel.ChannelType.Bidirectional) &&
                elementType.isIdentical(otherChannel.getElementType());
    }

    @Override
    public String getPresentationTailText() {
        return GoTypeChannel.ChannelType.getText(getChannelType()) + getElementType().getPresentationTailText();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
