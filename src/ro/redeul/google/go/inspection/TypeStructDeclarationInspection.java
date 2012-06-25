package ro.redeul.google.go.inspection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.types.GoTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TypeStructDeclarationInspection extends AbstractWholeGoFileInspection {
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
            public void visitStructType(GoTypeStruct type) {
                super.visitStructType(type);

                checkFields(type, result);
            }
        }.visitFile(file);
    }

    // returns true if the definition of "type" contains usage of "struct"
    private static boolean typeContainsStruct(GoType type, GoTypeStruct struct) {
        if (!(type instanceof GoTypeName)) {
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

        GoType typeDefinition = ((GoTypeSpec) resolve).getType();
        if (!(typeDefinition instanceof GoTypeStruct)) {
            return false;
        }

        if (struct.isEquivalentTo(typeDefinition)) {
            return true;
        }

        GoTypeStruct newStruct = (GoTypeStruct) typeDefinition;
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

    private static void checkFields(GoTypeStruct struct, InspectionResult result) {
        Set<String> fields = new HashSet<String>();
        for (GoTypeStructField field : struct.getFields()) {
            if (typeContainsStruct(field.getType(), struct)) {
                result.addProblem(field, GoBundle.message("error.invalid.recursive.type", struct.getName()));
            }

            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                String name = identifier.getUnqualifiedName();
                if (fields.contains(name)) {
                    result.addProblem(identifier, GoBundle.message("error.duplicate.field", name));
                } else {
                    fields.add(name);
                }
            }
        }

        for (GoTypeStructAnonymousField field : struct.getAnonymousFields()) {
            GoTypeName type = field.getType();
            if (type != null) {
                if (typeContainsStruct(type, struct)) {
                    result.addProblem(field, GoBundle.message("error.invalid.recursive.type", struct.getName()));
                }

                GoLiteralIdentifier identifier = type.getIdentifier();
                String name = identifier.getUnqualifiedName();
                if (fields.contains(name)) {
                    result.addProblem(identifier, GoBundle.message("error.duplicate.field", name));
                } else {
                    fields.add(name);
                }
            }
        }
    }
}
