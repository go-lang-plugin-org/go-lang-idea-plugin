package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GoAbstractType implements GoType {

    @NotNull
    @Override
    public GoType underlyingType() { return this; }

    @Override
    public <T> T accept(UpdatingTypeVisitor<T> visitor) {
        return accept(visitor, null);
    }

    @Override
    public <T> T accept(UpdatingTypeVisitor<T> visitor, T initialData) {
        return accept(new ForwardingVisitor<T>(visitor, initialData));
    }

    @Nullable
    public <T extends GoType> T underlyingType(Class<T> tClass) {
        GoType underlying = underlyingType();
        return tClass.isInstance(underlying) ? tClass.cast(underlying) : null;
    }

    @Override
    public boolean isAssignableFrom(GoType source) {
        return GoTypes.isAssignableFrom(this, source);
    }

    @Override
    public boolean isIdentical(GoType type) { return this == type; }

    @Override
    public boolean canRepresent(GoTypeConstant constantType) {
        return false;
    }

    class ForwardingVisitor<T> extends TypeVisitor<T> {

        private UpdatingTypeVisitor<T> second;

        public ForwardingVisitor(UpdatingTypeVisitor<T> second, T data) {
            setData(data);
            this.second = second;
        }

        public ForwardingVisitor(UpdatingTypeVisitor<T> second) {
            this(second, null);
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
