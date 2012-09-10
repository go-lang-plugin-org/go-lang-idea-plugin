package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeChannel;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

public class GoTypeChannel extends GoTypePsiBacked<GoPsiTypeChannel, GoUnderlyingTypeChannel> implements GoType {

    GoType elementType;
    ChannelType channelType;

    public GoTypeChannel(GoPsiTypeChannel psiType) {
        super(psiType);

        elementType = GoTypes.fromPsiType(psiType.getElementType());
        channelType = psiType.getChannelType();
        setUnderlyingType(
            GoUnderlyingTypes.getChannel(channelType,
                                         elementType.getUnderlyingType()));
    }

    @Override
    public boolean isIdentical(GoType type) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeChannel(this);
    }

    public GoType getElementType() {
        return elementType;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public static enum ChannelType {
        Bidirectional,
        Sending,
        Receiving;

        public static String getText(ChannelType channelType) {
            switch (channelType) {
                case Bidirectional: return "chan";
                case Sending: return "chan<-";
                case Receiving: return "<-chan";
            }

            return "";
        }
    }
}
