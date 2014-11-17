package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GoType {

    GoType[] EMPTY_ARRAY = new GoType[0];

    @Nullable
    public <T extends GoType> T underlyingType(Class<T> tClass);

    boolean isAssignableFrom(GoType source);

    boolean canRepresent(GoTypeConstant constantType);

    @Nullable
    GoType castAs(GoType type);

    boolean isIdentical(GoType type);

    @NotNull
    GoType underlyingType();

    <T> T accept(TypeVisitor<T> visitor);

    <T> T accept(UpdatingTypeVisitor<T> visitor);

    <T> T accept(UpdatingTypeVisitor<T> visitor, T initialData);

    static final GoType Any = new GoAbstractType() {
        @Override
        public <T> T accept(TypeVisitor<T> visitor) {
            return visitor.visitAny(this);
        }

        @Override
        public boolean isAssignableFrom(GoType source) { return true; }

        @Override
        public boolean isIdentical(GoType type) { return true; }

        @Nullable
        @Override
        public GoType castAs(GoType type) { return type; }

        @Override
        public String toString() { return "<any>"; }
    };

    static final GoType Unknown = new GoAbstractType() {
        @Override
        public <T> T accept(TypeVisitor<T> visitor) {
            return visitor.visitUnknown(this);
        }

        @Override
        public String toString() {
            return "<unknown>";
        }
    };

    static final GoType Nil = new GoAbstractType() {

        @Override
        public <T> T accept(TypeVisitor<T> visitor) {
            return visitor.visitNil(this);
        }

        @Override
        public String toString() {
            return "<nil>";
        }
    };
}
