package ro.redeul.google.go.lang.psi.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

public class GoFileUtils {
    public static List<GoConstDeclaration> getConstDeclarations(@Nullable GoFile psiFile) {
        if (psiFile == null) {
            return Collections.emptyList();
        }

        GoConstDeclarations[] constDeclarations = psiFile.getConsts();
        if (constDeclarations == null) {
            return Collections.emptyList();
        }

        List<GoConstDeclaration> consts = new ArrayList<>();
        for (GoConstDeclarations cds : constDeclarations) {
            Collections.addAll(consts, cds.getDeclarations());
        }

        return consts;
    }

    public static List<GoLiteralIdentifier> getConstIdentifiers(@Nullable GoFile psiFile) {
        if (psiFile == null) {
            return Collections.emptyList();
        }

        GoConstDeclarations[] constDeclarations = psiFile.getConsts();
        if (constDeclarations == null) {
            return Collections.emptyList();
        }

        List<GoLiteralIdentifier> consts = new ArrayList<>();
        for (GoConstDeclarations cds : constDeclarations) {
            for (GoConstDeclaration cd : cds.getDeclarations()) {
                Collections.addAll(consts, cd.getIdentifiers());
            }
        }

        return consts;
    }

    public static List<GoLiteralIdentifier> getVariableIdentifiers(@Nullable GoFile psiFile) {
        if (psiFile == null) {
            return Collections.emptyList();
        }

        GoVarDeclarations[] varDeclarations = psiFile.getGlobalVariables();
        if (varDeclarations == null) {
            return Collections.emptyList();
        }

        List<GoLiteralIdentifier> vars = new ArrayList<>();
        for (GoVarDeclarations vds : varDeclarations) {
            for (GoVarDeclaration vd : vds.getDeclarations()) {
                Collections.addAll(vars, vd.getIdentifiers());
            }
        }

        return vars;
    }

    public static List<GoImportDeclaration> getImportDeclarations(@Nullable GoFile psiFile) {
        if (psiFile == null) {
            return Collections.emptyList();
        }

        GoImportDeclarations[] importDeclarations = psiFile.getImportDeclarations();
        if (importDeclarations == null) {
            return Collections.emptyList();
        }

        List<GoImportDeclaration> declarations = new ArrayList<>();
        for (GoImportDeclarations ids : importDeclarations) {
            Collections.addAll(declarations, ids.getDeclarations());
        }
        return declarations;
    }

    public static boolean isPackageNameImported(GoFile file, String packageName) {
        for (GoImportDeclaration id : getImportDeclarations(file)) {
            if (id.getVisiblePackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static List<GoVarDeclaration> getVarDeclarations(@Nullable GoFile psiFile) {
        if (psiFile == null) {
            return Collections.emptyList();
        }

        GoVarDeclarations[] varDeclarations = psiFile.getGlobalVariables();
        if (varDeclarations == null) {
            return Collections.emptyList();
        }

        List<GoVarDeclaration> vars = new ArrayList<>();
        for (GoVarDeclarations vds : varDeclarations) {
            Collections.addAll(vars, vds.getDeclarations());
        }
        return vars;
    }


    public static List<GoFunctionDeclaration> getFunctionDeclarations(@Nullable GoFile psiFile) {
        if (psiFile == null) {
            return Collections.emptyList();
        }

        GoFunctionDeclaration[] functionDeclarations = psiFile.getFunctions();
        if (functionDeclarations == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(Arrays.asList(functionDeclarations));
    }

    public static List<GoMethodDeclaration> getMethodDeclarations(@Nullable GoFile psiFile) {
        if (psiFile == null) {
            return Collections.emptyList();
        }

        GoMethodDeclaration[] methodDeclarations = psiFile.getMethods();
        if (methodDeclarations == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(Arrays.asList(methodDeclarations));
    }

    public static List<GoTypeSpec> getTypeSpecs(@Nullable GoFile psiFile) {
        if (psiFile == null) {
            return Collections.emptyList();
        }

        GoTypeDeclaration[] typeDeclarations = psiFile.getTypeDeclarations();
        if (typeDeclarations == null) {
            return new ArrayList<>();
        }

        List<GoTypeSpec> specs = new ArrayList<>();
        for (GoTypeDeclaration typeDec : typeDeclarations) {
            Collections.addAll(specs, typeDec.getTypeSpecs());
        }

        return specs;
    }
}
