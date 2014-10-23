package ro.redeul.google.go.lang.psi.types.underlying;

public interface GoUnderlyingType {

    public static GoUnderlyingType[] EMPTY_ARRAY = new GoUnderlyingType[0];

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

            @Override
            public void accept(Visitor visitor) {
                visitor.visitUndefined(this);
            }
        };

    boolean isIdentical(GoUnderlyingType other);

    // method sets

    public void accept(Visitor visitor);

    public class Visitor<T> {

        T data;

        public Visitor(T data) {
            this.data = data;
        }

        public T visit(GoUnderlyingType type) {
            type.accept(this);
            return data;
        }

        protected void setData(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public void visitArray(GoUnderlyingTypeArray type) { }

        public void visitFunction(GoUnderlyingTypeFunction type) { }

        public void visitChannel(GoUnderlyingTypeChannel type) { }

        public void visitInterface(GoUnderlyingTypeInterface type) { }

        public void visitMap(GoUnderlyingTypeMap type) { }

        public void visitPointer(GoUnderlyingTypePointer type) { }

        public void visitStruct(GoUnderlyingTypeStruct type) { }

        public void visitSlice(GoUnderlyingTypeSlice type) { }

        public void visitUndefined(GoUnderlyingType type) { }

        public void visitPredeclared(GoUnderlyingTypePredeclared typePredeclared) { }
    }
}
