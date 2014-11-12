package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.util.GoUtil;

public interface GoType {

    GoType[] EMPTY_ARRAY = new GoType[0];

    @Nullable
    public <T extends GoType> T getUnderlyingType(Class<T> tClass);

    static final GoType Unknown = new GoAbstractType() {

        @Override
        public boolean isIdentical(GoType type) {
            return this == type;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitUnknown(this);
        }

        @Override
        public String toString() {
            return "unknown";
        }
    };

    static final GoType Nil = new GoAbstractType() {

        @Override
        public boolean isIdentical(GoType type) { return this == type; }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitNil(this);
        }

        @Override
        public String toString() {
            return "nil";
        }
    };

    boolean isAssignableFrom(GoType source);

    boolean canRepresent(GoTypeConstant constantType);

    static final class GoTypeVariadic extends GoAbstractType implements GoType {
        private GoType targetType;

        GoTypeVariadic(GoType type) {
            setTargetType(type);
        }

        @Override
        public boolean isIdentical(GoType type) {
            if (!(type instanceof GoTypeVariadic))
                return false;

            return getTargetType().isIdentical(((GoTypeVariadic) type).getTargetType());
        }

        @Override
        public GoType getUnderlyingType() {
            return this;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitVariadic(this);
        }

        public GoType getTargetType() {
            return targetType;
        }

        public void setTargetType(GoType targetType) {
            this.targetType = targetType;
        }
    }

    boolean isIdentical(GoType type);

    GoType getUnderlyingType();

    <T> T accept(Visitor<T> visitor);

    public class Visitor<T> {

        T data;

        public Visitor() { this(null); }

        public Visitor(T data) {
            this.data = data;
        }

        public T visit(GoType node) {
            if ( node != null )
                data = node.accept(this);
            return data;
        }

        protected T setData(T data) {
            this.data = data;
            return data;
        }

        public T getData() {
            return data;
        }

        public T visitArray(GoTypeArray type) { return null; }

        public T visitFunction(GoTypeFunction type) { return null; }

        public T visitChannel(GoTypeChannel type) { return null; }

        public T visitName(GoTypeName type) { return null; }

        public T visitSlice(GoTypeSlice type) { return null; }

        public T visitPointer(GoTypePointer type) { return null; }

        public T visitMap(GoTypeMap type) { return null; }

        public T visitPackage(GoTypePackage type) { return null; }

        public T visitStruct(GoTypeStruct type) { return null; }

        public T visitNil(GoType type) {return null; }

        public T visitUnknown(GoType type) { return null; }

        public T visitVariadic(GoTypeVariadic type) { return type.getTargetType().accept(this); }

        public T visitInterface(GoTypeInterface type) { return null; }

        public T visitConstant(GoTypeConstant constant) {
            return null;
        }

        public T visitPrimitive(GoTypePrimitive typePrimitive) {
            return null;
        }
    }

    public class Second<T> {

        public void visitPointer(GoTypePointer type, T data, Visitor<T> visitor) {}

        public void visitArray(GoTypeArray type, T data, Visitor<T> visitor) { }

        public void visitFunction(GoTypeFunction type, T data, Visitor<T> visitor) { }

        public void visitChannel(GoTypeChannel type, T data, Visitor<T> visitor) { }

        public void visitName(GoTypeName type, T data, Visitor<T> visitor) { }

        public void visitSlice(GoTypeSlice type, T data, Visitor<T> visitor) { }

        public void visitInterface(GoTypeInterface type, T data, Visitor<T> visitor) { }

        public void visitVariadic(GoTypeVariadic type, T data, Visitor<T> visitor) { }

        public void visitMap(GoTypeMap type, T data, Visitor<T> visitor) { }

        public void visitNil(GoType type, T data, Visitor<T> visitor) { }

        public void visitPackage(GoTypePackage type, T data, Visitor<T> visitor) { }

        public void visitStruct(GoTypeStruct type, T data, Visitor<T> visitor) { }

        public void visitUnknown(GoType type, T data, Visitor<T> visitor) { }

        public void visitConstant(GoTypeConstant type, T data, Visitor<T> visitor) { }

        public void visitPrimitive(GoTypePrimitive type, T data, Visitor<T> visitor) { }
    }


    public class ForwardingVisitor<T> extends Visitor<T> {
        private Second<T> second;

        public ForwardingVisitor(T data, Second<T> second) {
            setData(data);
            this.second = second;
        }
        public ForwardingVisitor(Second<T> second) {
            this(null, second);
        }

        @Override
        public T visitArray(GoTypeArray type) {
            T data = getData();
            second.visitArray(type, data, this);
            return setData(data);
        }

        @Override
        public T visitFunction(GoTypeFunction type) {
            T data = getData();
            second.visitFunction(type, data, this);
            return setData(data);
        }

        @Override
        public T visitChannel(GoTypeChannel type) {
            T data = getData();
            second.visitChannel(type, data, this);
            return setData(data);
        }

        @Override
        public T visitName(GoTypeName type) {
            T data = getData();
            second.visitName(type, data, this);
            return setData(data);
        }

        @Override
        public T visitSlice(GoTypeSlice type) {
            T data = getData();
            second.visitSlice(type, data, this);
            return setData(data);
        }

        @Override
        public T visitPointer(GoTypePointer type) {
            T data = getData();
            second.visitPointer(type, data, this);
            return setData(data);

        }

        @Override
        public T visitMap(GoTypeMap type) {
            T data = getData();
            second.visitMap(type, data, this);
            return setData(data);
        }

        @Override
        public T visitPackage(GoTypePackage type) {
            T data = getData();
            second.visitPackage(type, data, this);
            return setData(data);
        }

        @Override
        public T visitStruct(GoTypeStruct type) {
            T data = getData();
            second.visitStruct(type, data, this);
            return setData(data);
        }

        @Override
        public T visitNil(GoType type) {
            T data = getData();
            second.visitNil(type, data, this);
            return setData(data);
        }

        @Override
        public T visitUnknown(GoType type) {
            T data = getData();
            second.visitUnknown(type, data, this);
            return setData(data);
        }

        @Override
        public T visitVariadic(GoTypeVariadic type) {
            T data = getData();
            second.visitVariadic(type, data, this);
            return setData(data);
        }

        @Override
        public T visitInterface(GoTypeInterface type) {
            T data = getData();
            second.visitInterface(type, data, this);
            return setData(data);
        }

        @Override
        public T visitConstant(GoTypeConstant constant) {
            T data = getData();
            second.visitConstant(constant, data, this);
            return setData(data);
        }

        @Override
        public T visitPrimitive(GoTypePrimitive type) {
            T data = getData();
            second.visitPrimitive(type, data, this);
            return setData(data);
        }
    }
}
