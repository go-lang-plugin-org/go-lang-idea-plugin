package ro.redeul.google.go.inspection;

import java.util.HashSet;
import java.util.Set;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
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
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

public class TypeStructDeclarationInspection
    extends AbstractWholeGoFileInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Struct Declaration";
    }

    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result, boolean isOnTheFly) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitStructType(GoPsiTypeStruct type) {
                super.visitStructType(type);

                checkFields(type, result);
            }
        }.visitFile(file);
    }

    // returns true if the definition of "type" contains usage of "struct"
    private static boolean typeContainsStruct(GoPsiType type, GoPsiTypeStruct struct) {
        if (!(type instanceof GoPsiTypeName)) {
            return false;
        }

        PsiReference reference = type.getReference();
        if (reference == null) {
            return false;
        }

        PsiElement resolve = reference.resolve();
        if (!(resolve instanceof GoTypeSpec)) {
            return false;
        }

        GoPsiType typeDefinition = ((GoTypeSpec) resolve).getType();
        if (!(typeDefinition instanceof GoPsiTypeStruct)) {
            return false;
        }

        if (struct.isEquivalentTo(typeDefinition)) {
            return true;
        }

        GoPsiTypeStruct newStruct = (GoPsiTypeStruct) typeDefinition;
        for (GoTypeStructField field : newStruct.getFields()) {
            if (typeContainsStruct(field.getType(), struct)) {
                return true;
            }
        }

        for (GoTypeStructAnonymousField field : newStruct.getAnonymousFields()) {
            if (typeContainsStruct(field.getType(), struct)) {
                return true;
            }
        }
        return false;
    }

    private static void checkFields(GoPsiTypeStruct struct, InspectionResult result) {
        Set<String> fields = new HashSet<String>();
        for (GoTypeStructField field : struct.getFields()) {
            if (typeContainsStruct(field.getType(), struct)) {
                result.addProblem(field, GoBundle.message(
                    "error.invalid.recursive.type", struct.getName()));
            }

            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                String name = identifier.getUnqualifiedName();
                if (fields.contains(name)) {
                    result.addProblem(identifier,
                                      GoBundle.message("error.duplicate.field",
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

                if (typeContainsStruct(type, struct)) {
                    result.addProblem(field, GoBundle.message(
                        "error.invalid.recursive.type", struct.getName()));
                }

                GoLiteralIdentifier identifier = typeName.getIdentifier();
                String name = identifier.getUnqualifiedName();
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
