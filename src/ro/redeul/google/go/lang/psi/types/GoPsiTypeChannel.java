package ro.redeul.google.go.lang.psi.types;

import ro.redeul.google.go.lang.psi.typing.GoTypeChannel;

/**
 * User: mtoader
 * Date: Sep 2, 2010
 * Time: 1:20:40 PM
 */
public interface GoPsiTypeChannel extends GoPsiType {

    GoTypeChannel.ChannelType getChannelType();

    GoPsiType getElementType();
}
