package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
import ro.redeul.google.go.lang.psi.typing.GoTypeChannel;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 1:22:29 PM
 */
public class GoPsiTypeChannelImpl extends GoPsiTypeImpl implements GoPsiTypeChannel {

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
    public String getLookupTailText() {
        return GoTypeChannel.ChannelType.getText(getChannelType()) + getElementType().getLookupTailText();
    }
}
