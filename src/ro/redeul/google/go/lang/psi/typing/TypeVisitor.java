package ro.redeul.google.go.lang.psi.typing;

public class TypeVisitor<T> {

    T state;

    public TypeVisitor() { this(null); }

    public TypeVisitor(T state) {
        this.state = state;
    }

    public T visit(GoType node) {
        if ( node != null )
            state = node.accept(this);
        return state;
    }

    protected T setData(T data) {
        this.state = data;
        return data;
    }

    public T getData() {
        return state;
    }

    public T visitArray(GoTypeArray type) { return state; }

    public T visitFunction(GoTypeFunction type) { return state; }

    public T visitChannel(GoTypeChannel type) { return state; }

    public T visitName(GoTypeName type) { return state; }

    public T visitSlice(GoTypeSlice type) { return state; }

    public T visitPointer(GoTypePointer type) { return state; }

    public T visitMap(GoTypeMap type) { return state; }

    public T visitPackage(GoTypePackage type) { return state; }

    public T visitStruct(GoTypeStruct type) { return state; }

    public T visitNil(GoType type) {return state; }

    public T visitUnknown(GoType type) { return state; }

    public T visitVariadic(GoTypeVariadic type) { return type.getTargetType().accept(this); }

    public T visitInterface(GoTypeInterface type) { return state; }

    public T visitConstant(GoTypeConstant constant) { return state; }

    public T visitPrimitive(GoTypePrimitive type) { return visitName(type); }
}
