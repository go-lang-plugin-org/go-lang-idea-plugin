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

        @Override
        public void accept(Visitor visitor) {
            visitor.visitTypeUnknown(this);
        }
    };

    boolean isIdentical(GoType type);

    GoUnderlyingType getUnderlyingType();

    void accept(Visitor visitor);

    public class Visitor<T> {

        T data;

        public Visitor(T data) {
            this.data = data;
        }

        public T visit(GoType node) {
            node.accept(this);
            return data;
        }
        protected void setData(T data) {
            this.data = data;
        }

        protected void visitTypeUnknown(GoType type) { }

        protected void visitTypeArray(GoTypeArray array) { }

        public void visitTypeInterface(GoTypeInterface iface) { }

        public void visitTypeFunction(GoTypeFunction function) { }

        public void visitTypeChannel(GoTypeChannel channel) { }

        public void visitTypeName(GoTypeName name) { }

        public void visitTypeStruct(GoTypeStruct struct) { }

        public void visitTypeSlice(GoTypeSlice slice) { }

        public void visitTypePointer(GoTypePointer pointer) { }

        public void visitTypeMap(GoTypeMap map) { }
    }
}
