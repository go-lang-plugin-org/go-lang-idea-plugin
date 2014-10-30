package ro.redeul.google.go.lang.psi.types.underlying;

import ro.redeul.google.go.lang.psi.typing.GoTypeChannel;

public class GoUnderlyingTypeChannel implements GoUnderlyingType {

    private final GoTypeChannel.ChannelType channelType;
    private final GoUnderlyingType elementType;

    public GoUnderlyingTypeChannel(GoTypeChannel.ChannelType channelType, GoUnderlyingType elementType) {
        this.channelType = channelType;
        this.elementType = elementType;
    }

    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        if (!(other instanceof GoUnderlyingTypeChannel))
            return false;

        GoUnderlyingTypeChannel otherChannel = (GoUnderlyingTypeChannel) other;
        return channelType == otherChannel.channelType
                && elementType.isIdentical(otherChannel.elementType);
    }

    @Override
    public String toString() {
        return String.format("%s%s", channelType.toString(), elementType);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitChannel(this);
    }
}
