package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.util.GoNumber;

import java.math.BigDecimal;
import java.math.BigInteger;

public class GoTypeConstant extends GoAbstractType implements GoType {

    private Kind kind;
    private Object value;
    private GoType type = GoType.Unknown;

    public Kind getKind() {
        return kind;
    }

    @Nullable
    public <T> T getValueAs(Class<T> clazz) {
        if ( value != null && clazz.isAssignableFrom(value.getClass()))
            return clazz.cast(value);

        if (clazz.equals(GoNumber.class))
            return clazz.cast(GoNumber.buildFrom(value));

        if (clazz.equals(BigDecimal.class)) {
            switch (kind) {
                case Integer:
                    BigInteger integer = getValueAs(BigInteger.class);
                    return integer == null ? null : clazz.cast(new BigDecimal(integer));
                case Rune:
                    Character character = getValueAs(Character.class);
                    return character == null ? null : clazz.cast(new BigDecimal(character.charValue()));
            }
        }

        if ( clazz.equals(BigInteger.class) ) {
            switch (getKind()) {
                case Rune:
                    Character character = getValueAs(Character.class);
                    return character == null ? null : clazz.cast(BigInteger.valueOf(character));
            }
        }

        return null;
    }

    public Object getValue() {
        return value;
    }

    public GoTypeConstant retypeAs(GoType newType) {
        return (GoTypeConstant) GoTypes.constant(getKind(), getValue(), newType);
    }

    public enum Kind {
        Boolean, Rune, Integer, Float, Complex, String;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public GoTypeConstant(Kind kind, Object value) {
        this.kind = kind;
        this.value = value;
    }

    @NotNull
    public GoType getType() {
        return type;
    }

    public void setType(@NotNull GoType type) {
        this.type = type;
    }

    @Override
    public boolean isIdentical(GoType type) {
        return getType().isIdentical(type);
    }

    public boolean isNumeric() {
        switch (kind) {
            case Rune:
            case Integer:
            case Float:
            case Complex:
                return true;
            default:
                return false;
        }
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitConstant(this);
    }

    @NotNull
    @Override
    public GoType underlyingType() {
        return type != GoType.Unknown ? type.underlyingType() : this;
    }
}
