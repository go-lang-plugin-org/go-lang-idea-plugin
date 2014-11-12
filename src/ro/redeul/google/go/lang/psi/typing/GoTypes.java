package ro.redeul.google.go.lang.psi.typing;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.utils.GoFunctionDeclarationUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.services.GoPsiManager;

import javax.help.plaf.gtk.GTKCursorFactory;
import javax.management.Query;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class GoTypes extends AbstractProjectComponent {

    public GoTypes(Project project) {
        super(project);
    }

    public static GoTypes getInstance(Project project) {
        return project.getComponent(GoTypes.class);
    }

    public static final Pattern PRIMITIVE_TYPES_PATTERN =
            Pattern.compile("" +
                    "bool|error|byte|rune|uintptr|string|char|" +
                    "(int|uint)(8|16|32|64)?|" +
                    "float(32|64)|" +
                    "complex(64|128)");

    @Nullable
    public static <T extends GoType> T resolveTo(GoType type, Class<T> targetType) {

        GoType target = type;
        while (target != null && target != GoType.Unknown && !targetType.isAssignableFrom(target.getClass())) {
            if (target instanceof GoTypeName) {
                GoTypeSpec typeSpec = ((GoTypeName)target).getDefinition();
                target = typeSpec != null ? fromPsi(typeSpec.getType()) : GoType.Unknown;
            } else {
                target = GoType.Unknown;
            }
        }

        return (target != null && targetType.isAssignableFrom(target.getClass())) ? targetType.cast(target) : null;
    }

    public static GoTypeInterface resolveToInterface(GoPsiTypeName typeName) {
        return resolveTo(fromPsi(typeName), GoTypeInterface.class);
    }

    public static GoType makePointer(GoPsiType argumentType) {
        return new GoTypePointer(fromPsi(argumentType));
    }

    public static GoType getPackageTypeSimple(GoImportDeclaration declaration) {
        GoPackage goPackage = declaration.getPackage();
        return goPackage != null ? new GoTypePackage(goPackage) : null;
    }

    public static GoType[] getPackageType(GoImportDeclaration declaration) {
        GoPackage goPackage = declaration.getPackage();
        return goPackage != null ? new GoType[]{new GoTypePackage(goPackage)} : GoType.EMPTY_ARRAY;
    }

    public static boolean isAssignableFrom(GoType dstType, GoType srcVarType) {

        if (dstType == null || srcVarType == null)
            return false;

//          x's type is identical to T.
        if (srcVarType.isIdentical(dstType))
            return true;

//        x's type V and T have identical underlying types and at least one of V or T is not a named type.
        if ((!isNamedType(srcVarType) || !isNamedType(dstType)) && srcVarType.getUnderlyingType().isIdentical(dstType.getUnderlyingType()))
            return true;

//        T is an interface type and x implements T.
//        if ((dstType instanceof GoTypeInterface) && ((GoTypeInterface)dstType.isImplementedBy(srcVarType)))
//            return true;

//        x is a bidirectional channel value, T is a channel type, x's type V and T have identical element types, and at least one of V or T is not a named type.
        if (srcVarType.getUnderlyingType() instanceof GoTypeChannel && dstType.getUnderlyingType() instanceof GoTypeChannel
                && (!isNamedType(srcVarType) || !isNamedType(dstType))) {
            GoTypeChannel dstChannel = (GoTypeChannel) dstType.getUnderlyingType();
            GoTypeChannel srcChannel = (GoTypeChannel) srcVarType.getUnderlyingType();

            return srcChannel.getChannelType() == GoTypeChannel.ChannelType.Bidirectional &&
                    srcChannel.getElementType().isIdentical(dstChannel.getElementType());
        }

//        x is the predeclared identifier nil and T is a pointer, function, slice, map, channel, or interface type.
        if ( (srcVarType == GoType.Nil))
            return
                    dstType instanceof GoTypePointer || dstType instanceof GoTypeFunction ||
                    dstType instanceof GoTypeSlice || dstType instanceof GoTypeMap ||
                    dstType instanceof GoTypeChannel || dstType instanceof GoTypeInterface;

//        x is an untyped constant representable by a value of type T.
        if ( srcVarType instanceof GoTypeConstant ) {
            GoTypeConstant constant = (GoTypeConstant) srcVarType;
            if ( constant.getType() == GoType.Unknown)
                return dstType.getUnderlyingType().canRepresent(constant);
        }

        return false;
    }

    public static boolean isNamedType(GoType type) {
        if (type instanceof GoTypeName)
            return true;

        if (type instanceof GoTypeConstant) {
            GoTypeConstant constant = (GoTypeConstant) type;
            return constant.getType() instanceof GoTypeName;
        }

        return false;
    }

    public GoType untypedConstant(GoTypeConstant.Kind kind) {
        return constant(kind, null);
    }

    public static GoType constant(GoTypeConstant.Kind kind, Object value) {
        return new GoTypeConstant(kind, value);
    }

    public static GoType constant(GoTypeConstant.Kind kind, Object value, GoType type) {
        GoTypeConstant typeConstant = new GoTypeConstant(kind, value);
        typeConstant.setType(type);
        return typeConstant;
    }

    public static boolean areIdentical(GoType[] types, GoType[] oTypes) {
        if ( types.length != oTypes.length )
            return false;

        for (int i = 0, typesLength = types.length; i < typesLength; i++)
            if (!types[i].isIdentical(oTypes[i]))
                return false;

        return true;
    }

    public enum Builtin {
        Bool, Byte, Complex64, Complex128, Error, Float32, Float64,
        Int, Int8, Int16, Int32, Int64, Rune, String,
        uInt, uInt8, uInt16, uInt32, uInt64, uIntPtr
    }

    public static final Map<Builtin, GoType> cachedTypes = new HashMap<Builtin, GoType>();

    public GoType getBuiltin(Builtin builtinType) {

        GoNamesCache namesCache = GoNamesCache.getInstance(myProject);

        GoType type = cachedTypes.get(builtinType);
        if (type == null) {
            Collection<GoFile> files = namesCache.getFilesByPackageName("builtin");

            for (GoFile file : files) {
                for (GoTypeDeclaration typeDeclaration : file.getTypeDeclarations()) {
                    for (GoTypeSpec spec : typeDeclaration.getTypeSpecs()) {
                        if (spec != null) {
                            String name = spec.getName();
                            if (name != null && name.equals(builtinType.name().toLowerCase())) {
                                cachedTypes.put(builtinType, fromPsiType(spec.getType()));
                            }
                        }
                    }
                }
            }
        }

        return cachedTypes.get(builtinType);
    }

    @NotNull
    public static GoType fromPsi(final GoPsiType psiType) {
        return getInstance(psiType.getProject()).fromPsiType(psiType);
    }

    @NotNull
    public GoType fromPsiType(final GoPsiType psiType) {
        if (psiType == null)
            return GoType.Unknown;

        return GoPsiManager.getInstance(psiType.getProject()).getOrCompute(psiType, new Function<GoPsiType, GoType>() {
            @Override
            public GoType fun(GoPsiType psiType) {
                return psiType.accept(new GoTypeMakerVisitor());
            }
        });
    }

    public static GoType[] fromPsiType(GoPsiType[] psiTypes) {
        GoType types[] = new GoType[psiTypes.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = fromPsi(psiTypes[i]);
        }

        return types;
    }

    public static <T> T visitFirstType(GoType[] types, GoType.Visitor<T> visitor) {
        if (types != null && types.length >= 1 && types[0] != null)
            return visitor.visit(types[0]);

        return visitor.getData();
    }

    private static class GoTypeMakerVisitor extends GoElementVisitorWithData<GoType> {
        private GoTypeMakerVisitor() {
            data = GoType.Unknown;
        }

        @Override
        public void visitChannelType(GoPsiTypeChannel psiType) {
            data = new GoTypeChannel(psiType);
        }

        @Override
        public void visitArrayType(GoPsiTypeArray psiType) {
            data = new GoTypeArray(psiType);
        }

        @Override
        public void visitTypeName(GoPsiTypeName psiType) {
            data = psiType.isPrimitive() ? new GoTypePrimitive(psiType) : new GoTypeName(psiType);
        }

        @Override
        public void visitMapType(GoPsiTypeMap type) {
            data = new GoTypeMap(type);
        }

        @Override
        public void visitSliceType(GoPsiTypeSlice psiType) {
            data = new GoTypeSlice(psiType);
        }

        @Override
        public void visitPointerType(GoPsiTypePointer psiType) {
            data = new GoTypePointer(psiType);
        }

        @Override
        public void visitStructType(GoPsiTypeStruct psiType) {
            data = new GoTypeStruct(psiType);
        }

        @Override
        public void visitFunctionType(GoPsiTypeFunction type) {
            data = new GoTypeFunction(type);
        }

        @Override
        public void visitInterfaceType(GoPsiTypeInterface type) {
            data = new GoTypeInterface(type);
        }

        @Override
        public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
            visitFunctionType(declaration);
        }

        @Override
        public void visitMethodDeclaration(GoMethodDeclaration declaration) {
            visitFunctionType(declaration);
        }

        @Override
        public void visitFunctionLiteral(GoLiteralFunction declaration) {
            visitFunctionType(declaration);
        }
    }

    public static GoTypeStruct resolveToStruct(GoPsiType type) {
        return resolveToStruct(fromPsi(type));
    }

    public static GoTypeStruct resolveToStruct(@Nullable GoType type) {
        return new GoType.Visitor<GoTypeStruct>() {
            @Override
            public GoTypeStruct visitStruct(GoTypeStruct type) { return type; }

            @Override
            public GoTypeStruct visitName(GoTypeName type) { return visit(type.getUnderlyingType()); }

            @Override
            public GoTypeStruct visitPointer(GoTypePointer type) { return visit(type.getTargetType()); }
        }.visit(type);
    }

    @Nullable
    public static <T extends GoType> T dereferenceTo(Class<T> targetClass, @Nullable GoType type) {
        GoType target = dereference(type);

        return targetClass.isInstance(target) ? targetClass.cast(target) : null;
    }

    @Nullable
    public static GoType dereference(@Nullable GoType type) {
        return dereference(type, 1);
    }

    @Nullable
    public static GoType dereference(@Nullable GoType type, final int dereferences) {
        return new GoType.Visitor<GoType>(null) {
            int myDereferences = dereferences;

            @Override
            public GoType visitName(GoTypeName type) {
                return type == type.getUnderlyingType() ? type : type.getUnderlyingType().accept(this);
            }

            @Override
            public GoType visitPointer(GoTypePointer type) {
                myDereferences--;
                return myDereferences >= 0 ? type.getTargetType().accept(this) : type;
            }

            @Override
            public GoType visitArray(GoTypeArray type) { return type; }

            @Override
            public GoType visitFunction(GoTypeFunction type) { return type; }

            @Override
            public GoType visitChannel(GoTypeChannel type) { return type; }

            @Override
            public GoType visitSlice(GoTypeSlice type) { return type; }

            @Override
            public GoType visitMap(GoTypeMap type) { return type; }

            @Override
            public GoType visitPackage(GoTypePackage type) { return type; }

            @Override
            public GoType visitStruct(GoTypeStruct type) { return type; }

            @Override
            public GoType visitInterface(GoTypeInterface type) { return type; }

            @Override
            public GoType visitNil(GoType type) { return type; }

            @Override
            public GoType visitUnknown(GoType type) { return type; }

            @Override
            public GoType visitVariadic(GoType.GoTypeVariadic type) { return type; }

        }.visit(type);
    }

    public static String getRepresentation(GoType type, final GoFile view) {
        return type.accept(new GoType.ForwardingVisitor<StringBuilder>(new StringBuilder(), new GoType.Second<StringBuilder>() {
            @Override
            public void visitSlice(GoTypeSlice type, StringBuilder builder, GoType.Visitor<StringBuilder> visitor) {
                builder.append("[]");
                type.getElementType().accept(visitor);
            }

            @Override
            public void visitArray(GoTypeArray type, StringBuilder builder, GoType.Visitor<StringBuilder> visitor) {
                builder.append("[").append(type.getLength()).append("]");
                type.getElementType().accept(visitor);
            }

            @Override
            public void visitPointer(GoTypePointer type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
                data.append("*");
                type.getTargetType().accept(visitor);
            }

            @Override
            public void visitFunction(GoTypeFunction type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
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
            public void visitChannel(GoTypeChannel type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
                data.append(GoTypeChannel.ChannelType.getText(type.getChannelType())).append(" ");
                type.getElementType().accept(visitor);
            }

            @Override
            public void visitName(GoTypeName type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
                data.append(type.getName());
            }

            @Override
            public void visitInterface(GoTypeInterface type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
                super.visitInterface(type, data, visitor);
            }

            @Override
            public void visitVariadic(GoType.GoTypeVariadic type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
                data.append("...");
                type.getTargetType().accept(visitor);
            }

            @Override
            public void visitMap(GoTypeMap type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
                data.append("map[");
                type.getKeyType().accept(visitor);
                data.append("]");
                type.getElementType().accept(visitor);
            }

            @Override
            public void visitNil(GoType type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
                super.visitNil(type, data, visitor);
            }

            @Override
            public void visitPackage(GoTypePackage type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
                super.visitPackage(type, data, visitor);
            }

            @Override
            public void visitStruct(GoTypeStruct type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
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
            public void visitUnknown(GoType type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
                super.visitUnknown(type, data, visitor);
            }

            @Override
            public void visitConstant(GoTypeConstant type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
                if ( type.getType() != GoType.Unknown ){
                    type.getType().accept(visitor);
                    return;
                }

                switch (type.getKind()) {
                    case Integer: data.append("int"); break;
                    case Boolean: data.append("bool"); break;
                    case Complex: data.append("complex128"); break;
                    case Float: data.append("float64"); break;
                    case Rune: data.append("rune"); break;
                    case String: data.append("string");break;
                }
            }

            @Override
            public void visitPrimitive(GoTypePrimitive type, StringBuilder data, GoType.Visitor<StringBuilder> visitor) {
                data.append(type.getName());
            }

            private void visitParameterList(GoFunctionParameter[] parameters, StringBuilder builder, GoType.Visitor<StringBuilder> visitor) {
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
            })).toString();
    }
}
