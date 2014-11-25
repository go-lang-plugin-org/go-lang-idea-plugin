package ro.redeul.google.go.lang.psi.typing;

import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.utils.GoFunctionDeclarationUtils;

/**
* Created by mihai on 11/16/14.
*/
class RepresentingTypeVisitor extends UpdatingTypeVisitor<StringBuilder> {
    private GoFile context;

    public RepresentingTypeVisitor(GoFile context) {
        this.context = context;
    }

    @Override
    public void visitSlice(GoTypeSlice type, StringBuilder builder, TypeVisitor<StringBuilder> visitor) {
        builder.append("[]");
        type.getElementType().accept(visitor);
    }

    @Override
    public void visitArray(GoTypeArray type, StringBuilder builder, TypeVisitor<StringBuilder> visitor) {
        builder.append("[").append(type.getLength()).append("]");
        type.getElementType().accept(visitor);
    }

    @Override
    public void visitPointer(GoTypePointer type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        data.append("*");
        type.getTargetType().accept(visitor);
    }

    @Override
    public void visitFunction(GoTypeFunction type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        data.append("func");

        data.append("(");
        visitParameterList(type.getPsiType().getParameters(), data, visitor);
        data.append(")");

        GoFunctionParameter[] results = type.getPsiType().getResults();
        int resultCount = GoFunctionDeclarationUtils.getResultCount(results);
        if (resultCount > 0) {
            if (resultCount > 1) data.append("(");
            visitParameterList(results, data, visitor);
            if (resultCount > 1) data.append(")");
        }
    }

    @Override
    public void visitChannel(GoTypeChannel type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        data.append(GoTypeChannel.ChannelType.getText(type.getChannelType())).append(" ");
        type.getElementType().accept(visitor);
    }

    @Override
    public void visitName(GoTypeName type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        data.append(type.getName());
    }

    @Override
    public void visitInterface(GoTypeInterface type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        data.append("interface{}");
//        super.visitInterface(type, data, visitor);
    }

    @Override
    public void visitVariadic(GoTypeVariadic type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        data.append("...");
        type.getTargetType().accept(visitor);
    }

    @Override
    public void visitMap(GoTypeMap type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        data.append("map[");
        type.getKeyType().accept(visitor);
        data.append("]");
        type.getElementType().accept(visitor);
    }

    @Override
    public void visitNil(GoType type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        data.append("nil");
    }

    @Override
    public void visitPackage(GoTypePackage type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        super.visitPackage(type, data, visitor);
    }

    @Override
    public void visitStruct(GoTypeStruct type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        data.append("struct{");

        boolean needsSemi = false;

        for (PsiElement element : type.getPsiType().getAllFields()) {
            if (needsSemi)
                data.append(";");
            else
                needsSemi = true;

            boolean needsComma = false;
            if (element instanceof GoTypeStructField) {
                GoTypeStructField field = (GoTypeStructField) element;
                for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                    if (needsComma)
                        data.append(",");
                    else
                        needsComma = true;

                    data.append(identifier.getName());
                }

                GoTypes.fromPsi(field.getType()).accept(visitor);

                if (field.getTag() != null) {
                    data.append(" ").append(field.getTag());
                }
            } else if (element instanceof GoTypeStructAnonymousField) {
                GoTypeStructAnonymousField field = (GoTypeStructAnonymousField) element;
                GoTypes.fromPsi(field.getType()).accept(visitor);

                if (field.getTag() != null) {
                    data.append(" ").append(field.getTag());
                }
            }
        }

        data.append("}");
    }


    @Override
    public void visitUnknown(GoType type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        data.append("unknown");
    }

    @Override
    public void visitConstant(GoTypeConstant type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        if (type.getType() != GoType.Unknown) {
            type.getType().accept(visitor);
            return;
        }

//        data.append("untyped ")

        switch (type.kind()) {
            case Integer:
                data.append("int");
                break;
            case Boolean:
                data.append("bool");
                break;
            case Complex:
                data.append("complex128");
                break;
            case Float:
                data.append("float64");
                break;
            case Rune:
                data.append("rune");
                break;
            case String:
                data.append("string");
                break;
        }
    }

    @Override
    public void visitPrimitive(GoTypePrimitive type, StringBuilder data, TypeVisitor<StringBuilder> visitor) {
        data.append(type.getName());
    }

    private void visitParameterList(GoFunctionParameter[] parameters, StringBuilder builder, TypeVisitor<StringBuilder> visitor) {
        boolean needsComma = false;
        for (GoFunctionParameter parameter : parameters) {
            int count = Math.max(1, parameter.getIdentifiers().length);
            while (count > 0) {
                if (needsComma)
                    builder.append(",");
                else
                    needsComma = true;

                if (parameter.isVariadic())
                    builder.append("...");

                GoTypes.fromPsi(parameter.getType()).accept(visitor);
                count--;
            }
        }
    }




        /*
    } else if (type instanceof GoPsiTypeInterface) {
        GoPsiTypeName[] typeNames = ((GoPsiTypeInterface) type).getTypeNames();
        if (typeNames.length == 0)
            return "interface{}";
        type = typeNames[0];


    StringBuilder stringBuilder = new StringBuilder();
    GoTypeSpec goTypeSpec = resolveTypeSpec((GoPsiTypeName) type);
    if (goTypeSpec == null)
        return type.getName();
    PsiDirectory containingDirectory = goTypeSpec.getContainingFile().getContainingDirectory();
    boolean isInSameDir = currentFile.getContainingDirectory().equals(containingDirectory);
    if (((GoPsiTypeName) type).isPrimitive() || isInSameDir) {
        stringBuilder.append(type.getName());
    } else {
        FORLOOP:
        for (GoImportDeclarations declarations : currentFile.getImportDeclarations())
            for (GoImportDeclaration declaration : declarations.getDeclarations()) {
                String canonicalPath = containingDirectory.getVirtualFile().getCanonicalPath();
                GoLiteralString importPath = declaration.getImportPath();
                if (importPath != null && canonicalPath != null && canonicalPath.endsWith(importPath.getValue())) {
                    String visiblePackageName = declaration.getPackageAlias();
                    if (visiblePackageName.equals(".")) {
                        stringBuilder.append(type.getName());
                    } else {
                        stringBuilder.append(visiblePackageName).append(".").append(type.getName());
                    }
                    break FORLOOP;
                }
            }
    }
    return stringBuilder.toString();

  */
}
