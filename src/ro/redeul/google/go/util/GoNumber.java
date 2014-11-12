package ro.redeul.google.go.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class GoNumber extends Number {

    public static GoNumber ZERO = new GoNumber(BigDecimal.ZERO, BigDecimal.ZERO);

    private BigDecimal real;
    private BigDecimal imag;

    public GoNumber(BigDecimal imagPart) {
        this(BigDecimal.ZERO, imagPart);
    }

    public GoNumber(BigDecimal real, BigDecimal imag) {
        this.real = real;
        this.imag = imag;
    }

    @NotNull
    public BigDecimal getReal() {
        return real;
    }

    @NotNull
    public BigDecimal getImag() {
        return imag;
    }

    @Override
    public int intValue() {
        if ( !isComplex() )
            try {
                return real.toBigIntegerExact().intValueExact();
            } catch (ArithmeticException ex) {
                //
            }

        throw new ArithmeticException(String.format("%s can't be represented as a int", this));
    }

    @Override
    public long longValue() {
        if ( !isComplex() )
            try {
                return real.toBigIntegerExact().longValueExact();
            } catch (ArithmeticException ex) {
                //
            }

        throw new ArithmeticException(String.format("%s can't be represented as a long", this));
    }

    @Override
    public float floatValue() {
        if ( !isComplex())
            return real.floatValue();

        throw new ArithmeticException(String.format("%s can't be represented as a float", this));
    }

    @Override
    public double doubleValue() {
        if ( !isComplex())
            return real.doubleValue();

        throw new ArithmeticException(String.format("%s can't be represented as a double", this));
    }

    @Override
    public String toString() {
        return isComplex()
                ? String.format("%s %s %si", real.toString(), imag.signum() >= 0 ? "+" : "", imag.toString())
                : real.toString();
    }

    public boolean isComplex() {
        try {
            return imag.toBigIntegerExact().compareTo(BigInteger.ZERO) == 0;
        } catch (ArithmeticException ex) {
            return true;
        }
    }

    public static GoNumber parseLiteralInt(String textValue) {
        BigInteger value = BigInteger.ZERO;

        String text = textValue;
        int radix = 10;
        if (text.length() > 1) {
            if (text.startsWith("0x") || text.startsWith("0X")) {
                radix = 16;
                text = text.substring(2);
            } else if (text.startsWith("0")) {
                radix = 8;
                text = text.substring(1);
            }
        }
        try {
            value = new BigInteger(text, radix);
        } catch (NumberFormatException e) {
            //
        }

        return new GoNumber(new BigDecimal(value), BigDecimal.ZERO);
    }

    public static GoNumber parseLiteralFloat(String text) {
        try {
            return new GoNumber(new BigDecimal(text), BigDecimal.ZERO);
        } catch (NumberFormatException e) {
            return ZERO;
        }
    }

    @Nullable
    public static GoNumber buildFrom(Object value) {
        if ( value == null )
            return null;

        if ( value instanceof BigInteger )
            return new GoNumber(new BigDecimal((BigInteger) value), BigDecimal.ZERO);

        if ( value instanceof BigDecimal)
            return new GoNumber((BigDecimal) value, BigDecimal.ZERO);

        return null;
    }

    public GoNumber add(GoNumber other) {
        return new GoNumber(real.add(other.real), imag.add(other.imag));
    }

    public GoNumber substract(GoNumber other) {
        return new GoNumber(real.subtract(other.real), imag.subtract(other.imag));
    }

    public GoNumber negate() {
        return new GoNumber(real.negate(), imag.negate());
    }

    public GoNumber multiply(GoNumber other) {
        return new GoNumber(
                real.multiply(other.real).subtract(imag.multiply(other.imag)),
                imag.max(other.real).add(real.multiply(other.imag))
        );
    }

    public GoNumber divide(GoNumber other) {
        BigDecimal divisor = other.real.multiply(other.real).add(other.imag.multiply(other.real));

        BigDecimal newReal = real.multiply(other.real).add(imag.multiply(other.imag)).divide(divisor, MathContext.DECIMAL128);
        BigDecimal newImag = imag.multiply(other.real).subtract(real.multiply(other.imag)).divide(divisor, MathContext.DECIMAL128);

        return new GoNumber(newReal, newImag);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GoNumber))
            return false;

        GoNumber other = (GoNumber) obj;

        return real.compareTo(other.real) == 0 && imag.compareTo(other.imag) == 0;
    }
}
