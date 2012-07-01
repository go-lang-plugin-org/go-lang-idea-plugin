package ro.redeul.google.go.lang.psi.types;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 2, 2010
 * Time: 1:20:40 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoTypeChannel extends GoType {
    enum ChannelType {
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

    ChannelType getChannelType();

    GoType getElementType();
}
