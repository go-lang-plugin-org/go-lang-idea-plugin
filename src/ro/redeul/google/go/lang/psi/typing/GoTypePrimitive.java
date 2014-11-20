package ro.redeul.google.go.lang.psi.typing;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.util.GoNumber;

import java.math.BigDecimal;
import java.math.BigInteger;


public class GoTypePrimitive extends GoTypeName {

    public static final BigInteger INT64_MIN = BigInteger.valueOf(0x8000000000000000L);
    public static final BigInteger INT64_MAX = BigInteger.valueOf(0x7fffffffffffffffL);

    public static final BigInteger INT32_MIN = BigInteger.valueOf(0x80000000);
    public static final BigInteger INT32_MAX = BigInteger.valueOf(0x7fffffff);

    public static final BigInteger INT16_MIN = BigInteger.valueOf(0xffff8000);
    public static final BigInteger INT16_MAX = BigInteger.valueOf(0x00007fff);

    public static final BigInteger INT8_MIN = BigInteger.valueOf(0xffffff80);
    public static final BigInteger INT8_MAX = BigInteger.valueOf(0x0000007f);

    public static final BigInteger UINT64_MAX = new BigInteger("18446744073709551615");
    public static final BigInteger UINT32_MAX = BigInteger.valueOf(4294967295L);
    public static final BigInteger UINT16_MAX = BigInteger.valueOf(65535);
    public static final BigInteger UINT8_MAX = BigInteger.valueOf(255);

    public static final BigInteger UNICODE_MAX = BigInteger.valueOf(0x10FFFF);

    ImmutableMap<String, GoTypes.Builtin> typesMap = ImmutableMap.<String, GoTypes.Builtin>builder()
            .put("complex128", GoTypes.Builtin.Complex128)
            .put("complex64", GoTypes.Builtin.Complex64)
            .put("int64", GoTypes.Builtin.Int64)
            .put("int32", GoTypes.Builtin.Int32)
            .put("int16", GoTypes.Builtin.Int16)
            .put("int8", GoTypes.Builtin.Int8)
            .put("int", GoTypes.Builtin.Int)
            .put("uint64", GoTypes.Builtin.uInt64)
            .put("uint32", GoTypes.Builtin.uInt32)
            .put("uint16", GoTypes.Builtin.uInt16)
            .put("uint8", GoTypes.Builtin.uInt8)
            .put("uint", GoTypes.Builtin.uInt)
            .put("byte", GoTypes.Builtin.Byte)
            .put("float64", GoTypes.Builtin.Float64)
            .put("float32", GoTypes.Builtin.Float32)
            .put("rune", GoTypes.Builtin.Rune)
            .put("string", GoTypes.Builtin.String)
            .put("bool", GoTypes.Builtin.Bool)
            .put("error", GoTypes.Builtin.Error)
            .put("uintptr", GoTypes.Builtin.uIntPtr)
            .build();

    GoTypes.Builtin type;

    public GoTypePrimitive(GoTypeNameDeclaration psiType) {
        super(psiType);
        type = typesMap.get(getName());
    }

    @Override
    public boolean isIdentical(GoType type) {
        return (type instanceof GoTypePrimitive) && getName().equals(((GoTypePrimitive) type).getName());
    }

    @NotNull
    @Override
    public GoType underlyingType() {
        switch (getType()) {
            case Error:
                return super.underlyingType();
            default:
                return this;
        }
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitPrimitive(this);
    }

    public String getName() {
        return getPsiType().getName();
    }

    public GoTypes.Builtin getType() {
        return type;
    }

    @Nullable
    @Override
    public GoType castAs(GoType type) {
        return super.castAs(type);
    }

    @Override
    public boolean canRepresent(GoTypeConstant constant) {

        switch (constant.getKind()) {
            case Complex:
                return canRepresent(constant.getValueAs(GoNumber.class));
            case Float:
                return canRepresent(constant.getValueAs(BigDecimal.class));
            case Rune:
                return canRepresent(constant.getValueAs(BigInteger.class));
            case Integer:
                return canRepresent(constant.getValueAs(BigInteger.class));
            case String:
                return canRepresent(constant.getValueAs(String.class));
            case Boolean:
                return canRepresent(constant.getValueAs(Boolean.class));
        }

        return false;
    }

    private boolean canRepresent(GoNumber value) {
        if (!value.isComplex())
            return canRepresent(value.getReal());

        switch (type) {
            case Complex128:
                double doubleReal = value.getReal().doubleValue();
                double doubleImag = value.getImag().doubleValue();

                return doubleReal != Double.POSITIVE_INFINITY && doubleReal != Double.NEGATIVE_INFINITY &&
                        doubleImag != Double.POSITIVE_INFINITY && doubleImag != Double.NEGATIVE_INFINITY;

            case Complex64:
                float floatReal = value.getReal().floatValue();
                float floatImag = value.getImag().floatValue();

                return floatReal != Double.POSITIVE_INFINITY && floatReal != Double.NEGATIVE_INFINITY &&
                        floatImag != Double.POSITIVE_INFINITY && floatImag != Double.NEGATIVE_INFINITY;
            default:
                return false;
        }
    }

    private boolean canRepresent(BigDecimal value) {
        try {
            return canRepresent(value.toBigIntegerExact());
        } catch (ArithmeticException ex) {
            switch (type) {
                case Float64: case Complex128:
                    double doubleValue = value.doubleValue();
                    return doubleValue != Double.POSITIVE_INFINITY && doubleValue != Double.NEGATIVE_INFINITY;
                case Float32: case Complex64:
                    float floatValue = value.floatValue();
                    return floatValue != Float.POSITIVE_INFINITY && floatValue != Float.NEGATIVE_INFINITY;

                default:
                    return false;
            }
        }
    }

    private boolean canRepresent(BigInteger value) {
        if ( value == null )
            return false;

        switch (type) {
            case Int64: case Float64: case Complex128:
                return INT64_MIN.compareTo(value) <= 0 && value.compareTo(INT64_MAX) <= 0;
            case Int: case Int32: case Rune: case Float32: case Complex64:
                return INT32_MIN.compareTo(value) <= 0 && value.compareTo(INT32_MAX) <= 0;
            case Int16:
                return INT16_MIN.compareTo(value) <= 0 && value.compareTo(INT16_MAX) <= 0;
            case Int8:
                return INT8_MIN.compareTo(value) <= 0 && value.compareTo(INT8_MAX) <= 0;

            case uInt64:
                return BigInteger.ZERO.compareTo(value) <= 0 && value.compareTo(UINT64_MAX) <= 0;
            case uInt32: case uInt:
                return BigInteger.ZERO.compareTo(value) <= 0 && value.compareTo(UINT32_MAX) <= 0;
            case uInt16:
                return BigInteger.ZERO.compareTo(value) <= 0 && value.compareTo(UINT16_MAX) <= 0;
            case uInt8: case Byte:
                return BigInteger.ZERO.compareTo(value) <= 0 && value.compareTo(UINT8_MAX) <= 0;
            default:
                return false;
        }
    }

    private boolean canRepresent(String value) {
        return type == GoTypes.Builtin.String;
    }

    private boolean canRepresent(Boolean value) {
        return type == GoTypes.Builtin.Bool;
    }
}
