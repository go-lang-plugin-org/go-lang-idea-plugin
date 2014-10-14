/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;

public abstract class GoAbstractType<UnderlyingType extends GoUnderlyingType> implements GoType {

    private UnderlyingType underlyingType;

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return
            underlyingType != null
                ? underlyingType
                : GoUnderlyingType.Undefined;
    }

    void setUnderlyingType(UnderlyingType underlyingType) {
        this.underlyingType = underlyingType;
    }
}
