package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public interface GoType {

    GoType[] EMPTY_ARRAY = new GoType[0];

    static final GoType Unknown = new GoType() {

        @Override
        public boolean isIdentical(GoType type) {
            return type != null && type == this;
        }

        @Override
        public GoUnderlyingType getUnderlyingType() {
            return GoUnderlyingType.Undefined;
        }
    };

    boolean isIdentical(GoType type);

    GoUnderlyingType getUnderlyingType();
}
