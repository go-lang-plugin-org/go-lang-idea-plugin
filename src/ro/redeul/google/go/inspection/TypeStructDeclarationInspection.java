package ro.redeul.google.go.inspection;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.HashSet;
import java.util.Set;

public class TypeStructDeclarationInspection extends AbstractWholeGoFileInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Struct Declaration";
    }

    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitStructType(GoPsiTypeStruct type) {
                super.visitStructType(type);

                checkFields(type, result);
            }
        }.visitFile(file);
    }

    // returns true if the definition of "psiType" contains usage of "struct"
    private static boolean typeContainsStruct(@NotNull GoPsiTypeName psiType, @NotNull GoPsiTypeStruct struct) {

        GoType goType = GoTypes.fromPsi(psiType);
        if (!(goType instanceof GoTypeName))
            return false;

        GoTypeName type = (GoTypeName) goType;

        GoTypeSpec resolve = type.getDefinition();
        if (resolve == null) {
            return false;
        }

        GoPsiType definition = resolve.getType();
        if (!(definition instanceof GoPsiTypeStruct))
            return false;

        if (struct.isEquivalentTo(definition))
            return true;

        GoPsiTypeStruct structDefinition = (GoPsiTypeStruct) definition;
//        for (GoTypeStructField field : structDefinition.getFields()) {
//            if (typeContainsStruct(field.getType(), struct)) {
//                return true;
//            }
//        }


        for (GoTypeStructAnonymousField field : structDefinition.getAnonymousFields()) {
            GoPsiType fieldType = field.getType();
            if (fieldType instanceof GoPsiTypeName) {
                if (typeContainsStruct((GoPsiTypeName) fieldType, struct)) return true;
            }
        }

        return false;
    }

    private static void checkFields(GoPsiTypeStruct struct, InspectionResult result) {
        Set<String> fields = new HashSet<String>();
        for (GoTypeStructField field : struct.getFields()) {
            GoPsiType fieldType = field.getType();
            if (fieldType instanceof GoPsiTypeName && typeContainsStruct((GoPsiTypeName) fieldType, struct))
                result.addProblem(field, GoBundle.message(
                        "error.invalid.recursive.type", struct.getName()));

            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                String name = identifier.getName();
                if (fields.contains(name)) {
                    result.addProblem(identifier, GoBundle.message("error.duplicate.field",
                                    name));
                } else {
                    fields.add(name);
                }
            }
        }

        for (GoTypeStructAnonymousField field : struct.getAnonymousFields()) {
            GoPsiType type = field.getType();

            if (type instanceof GoPsiTypeName) {
                GoPsiTypeName typeName = (GoPsiTypeName) type;

                if (typeContainsStruct(typeName, struct))
                    result.addProblem(field, GoBundle.message("error.invalid.recursive.type", struct.getName()));

                GoLiteralIdentifier identifier = typeName.getIdentifier();
                String name = identifier.getName();
                if (fields.contains(name)) {
                    result.addProblem(identifier, GoBundle.message(
                            "error.duplicate.field", name));
                } else {
                    fields.add(name);
                }
            }
        }
    }
}
