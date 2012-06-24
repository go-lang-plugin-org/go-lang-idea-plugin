package ro.redeul.google.go.lang.psi.types.underlying;

public interface GoUnderlyingType {

    public static GoUnderlyingType[] EMPTY_ARRAY =
        new GoUnderlyingType[0];

    public static GoUnderlyingType Undefined =
        new GoUnderlyingType() {
            @Override
            public boolean isIdentical(GoUnderlyingType other) {
                return false;
            }

            @Override
            public String toString() {
                return "undefined";
            }
        };

    boolean isIdentical(GoUnderlyingType other);

    // method sets
}
