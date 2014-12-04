package ro.redeul.google.go.imports;

import com.intellij.openapi.util.Condition;
import com.intellij.util.containers.ContainerUtil;
import ro.redeul.google.go.lang.parser.parsing.toplevel.packaging.ImportDeclaration;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.visitors.GoImportUsageCheckingVisitor;

import java.util.*;

import static com.intellij.util.containers.ContainerUtil.iterate;
import static com.intellij.util.containers.ContainerUtil.toCollection;

public class UnusedImportsFinder {

    public static final Condition<GoImportDeclaration> TYPED_TRUE_CONDITION = new Condition<GoImportDeclaration>() {
        @Override
        public boolean value(GoImportDeclaration goImportDeclaration) {
            return true;
        }
    };

    public static Collection<GoImportDeclaration> findUnusedImports(GoFile file) {
        Set<GoImportDeclaration> declarationSet = new HashSet<GoImportDeclaration>();
        for (GoImportDeclarations importDeclarations : file.getImportDeclarations()) {
            declarationSet.addAll(toCollection(iterate(importDeclarations.getDeclarations(), TYPED_TRUE_CONDITION)));
        }

        return file.accept(new GoImportUsageCheckingVisitor(declarationSet));
    }
}
