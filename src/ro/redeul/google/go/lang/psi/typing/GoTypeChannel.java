package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;

public class GoTypeChannel extends GoTypePsiBacked<GoPsiTypeChannel> implements GoType {

    private final GoType elementType;
    private final ChannelType channelType;

    public GoTypeChannel(GoPsiTypeChannel psiType) {
        super(psiType);

        elementType = types().fromPsiType(psiType.getElementType());
        channelType = psiType.getChannelType();
    }

    @Override
    public boolean isIdentical(GoType type) {
        if ( !(type instanceof GoTypeChannel))
            return false;

        GoTypeChannel other = (GoTypeChannel) type;

        return getChannelType() == other.getChannelType() && getElementType().isIdentical(other.getElementType());
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitChannel(this);
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

    @Override
    public String toString() {
        return String.format("%s %s", ChannelType.getText(getChannelType()), getElementType());
    }
}
