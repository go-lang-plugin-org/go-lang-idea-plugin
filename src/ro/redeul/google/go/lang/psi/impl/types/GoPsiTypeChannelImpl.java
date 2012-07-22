package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 1:22:29 PM
 */
public class GoPsiTypeChannelImpl extends GoPsiPackagedElementBase implements
                                                                GoPsiTypeChannel {

    private ChannelType channelType;

    public GoPsiTypeChannelImpl(@NotNull ASTNode node, ChannelType channelType) {
        super(node);
        this.channelType = channelType;
    }

    public ChannelType getChannelType() {
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
        return this.getChannelType() == otherChannel.getChannelType() &&
                    this.getElementType().isIdentical(otherChannel.getElementType());
    }

    @Override
    public String getPresentationTailText() {
        return ChannelType.getText(getChannelType()) + getElementType().getPresentationTailText();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
