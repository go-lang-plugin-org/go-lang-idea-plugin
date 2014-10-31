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
            visitor.visitUnknown(this);
        }
    };

    static final GoType Nil = new GoType() {
        @Override
        public boolean isIdentical(GoType type) {
            return true;
        }

        @Override
        public GoUnderlyingType getUnderlyingType() {
            return GoUnderlyingType.Undefined;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitNil(this);
        }
    };

    boolean isIdentical(GoType type);

    GoUnderlyingType getUnderlyingType();

    void accept(Visitor visitor);

    public class Visitor<T> {

        T data;

        public Visitor() { this(null); }
        public Visitor(T data) {
            this.data = data;
        }

        public T visit(GoType node) {
            if ( node != null )
                node.accept(this);
            return data;
        }

        protected void setData(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public void visitArray(GoTypeArray type) { }

        public void visitFunction(GoTypeFunction type) { }

        public void visitChannel(GoTypeChannel type) { }

        public void visitName(GoTypeName type) { }

        public void visitSlice(GoTypeSlice type) { }

        public void visitPointer(GoTypePointer type) { }

        public void visitMap(GoTypeMap type) { }

        public void visitPackage(GoTypePackage type) { }

        public void visitStruct(GoTypeStruct type) { }

        public void visitNil(GoType nil) { }

        public void visitUnknown(GoType unknown) { }
    }
}
