/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.types.underlying;

/**
 * // TODO: Explain yourself.
 *
 * @author Mihai Claudiu Toader <mtoader@midokura.com>
 *         Date: 7/3/12
 */
public class GoUnderlyingTypeStruct implements GoUnderlyingType {
    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        if (!(other instanceof GoUnderlyingTypeStruct))
            return false;

        // TODO: implement this.
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
