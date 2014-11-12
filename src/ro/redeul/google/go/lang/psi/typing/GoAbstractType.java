/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.Nullable;

public abstract class GoAbstractType implements GoType {

    @Override
    public GoType getUnderlyingType() { return this; }

    @Nullable
    public <T extends GoType> T getUnderlyingType(Class<T> tClass) {
        GoType underlying = getUnderlyingType();
        return tClass.isInstance(underlying) ? tClass.cast(underlying) : null;
    }

    @Override
    public boolean isAssignableFrom(GoType source) {
        return GoTypes.isAssignableFrom(this, source);
    }

    @Override
    public boolean canRepresent(GoTypeConstant constantType) {
        return false;
    }
}
