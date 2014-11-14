package ro.redeul.google.go.lang.psi.typing;

/**
* Created by mihai on 11/13/14.
*/
public class UpdatingTypeVisitor<T> {

    public void visitPointer(GoTypePointer type, T data, TypeVisitor<T> visitor) {}

    public void visitArray(GoTypeArray type, T data, TypeVisitor<T> visitor) { }

    public void visitFunction(GoTypeFunction type, T data, TypeVisitor<T> visitor) { }

    public void visitChannel(GoTypeChannel type, T data, TypeVisitor<T> visitor) { }

    public void visitName(GoTypeName type, T data, TypeVisitor<T> visitor) { }

    public void visitSlice(GoTypeSlice type, T data, TypeVisitor<T> visitor) { }

    public void visitInterface(GoTypeInterface type, T data, TypeVisitor<T> visitor) { }

    public void visitVariadic(GoTypeVariadic type, T data, TypeVisitor<T> visitor) { }

    public void visitMap(GoTypeMap type, T data, TypeVisitor<T> visitor) { }

    public void visitNil(GoType type, T data, TypeVisitor<T> visitor) { }

    public void visitPackage(GoTypePackage type, T data, TypeVisitor<T> visitor) { }

    public void visitStruct(GoTypeStruct type, T data, TypeVisitor<T> visitor) { }

    public void visitUnknown(GoType type, T data, TypeVisitor<T> visitor) { }

    public void visitConstant(GoTypeConstant type, T data, TypeVisitor<T> visitor) { }

    public void visitPrimitive(GoTypePrimitive type, T data, TypeVisitor<T> visitor) { }
}
