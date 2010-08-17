package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 2:52:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChanType implements GoElementTypes {
    enum ChannelType {
        Bidirectional(TYPE_CHAN_BIDIRECTIONAL), Sending(TYPE_CHAN_SENDING), Receiving(TYPE_CHAN_RECEIVING);

        public IElementType elementType;

        ChannelType(IElementType elementType) {
            this.elementType = elementType;
        }
    }

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if ( builder.getTokenType() != kCHAN && builder.getTokenType() != oSEND_CHANNEL ) {
            marker.drop();
            builder.error("chan.keyword.or.channel.operator.expected");
            return false;
        }

        ChannelType type = null;
        if ( builder.getTokenType() == kCHAN ) {
            ParserUtils.getToken(builder, kCHAN);

            type = ChannelType.Bidirectional;

            ParserUtils.skipNLS(builder);
            if ( ParserUtils.getToken(builder, oSEND_CHANNEL) ) {
                type = ChannelType.Sending;
            }
        } else {
            ParserUtils.getToken(builder, oSEND_CHANNEL);

            ParserUtils.skipNLS(builder);
            ParserUtils.getToken(builder, kCHAN, "chan.keyword.expected");
            type = ChannelType.Receiving;
        }

        ParserUtils.skipNLS(builder);
        parser.parseType(builder);


        marker.done(type.elementType);
        return true;
    }
}
