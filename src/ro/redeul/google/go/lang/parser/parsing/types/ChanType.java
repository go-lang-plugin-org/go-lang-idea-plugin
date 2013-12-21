package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class ChanType implements GoElementTypes {
    enum ChannelType {
        Bidirectional(TYPE_CHAN_BIDIRECTIONAL), Sending(TYPE_CHAN_SENDING), Receiving(TYPE_CHAN_RECEIVING);

        public final IElementType elementType;

        ChannelType(IElementType elementType) {
            this.elementType = elementType;
        }
    }

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if ( !ParserUtils.lookAhead(builder, kCHAN) &&
             !ParserUtils.lookAhead(builder, oSEND_CHANNEL))
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ChannelType type;
        if ( builder.getTokenType() == kCHAN ) {
            ParserUtils.getToken(builder, kCHAN);

            type = ChannelType.Bidirectional;

            if ( ParserUtils.getToken(builder, oSEND_CHANNEL) ) {
                type = ChannelType.Sending;
            }
        } else {
            ParserUtils.getToken(builder, oSEND_CHANNEL);

            ParserUtils.getToken(builder, kCHAN, "chan.keyword.expected");
            type = ChannelType.Receiving;
        }

        if ( parser.parseType(builder) == null ) {
            builder.error(GoBundle.message("error.channel.type.expected"));
        }

        marker.done(type.elementType);
        return type.elementType;
    }
}
