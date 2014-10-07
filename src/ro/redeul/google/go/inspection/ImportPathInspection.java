package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.fix.RemoveImportFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.services.GoCodeManager;

import java.util.Collection;
import java.util.HashSet;

import static ro.redeul.google.go.GoBundle.message;

public class ImportPathInspection extends AbstractWholeGoFileInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Import Path";
    }

    @Override
    protected void doCheckFile(@NotNull GoFile file,
                               @NotNull final InspectionResult result) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitFile(GoFile file) {
                super.visitFile(file);
                checkImportPath(file, result);
            }
        }.visitFile(file);
    }

    private static void checkImportPath(GoFile file,InspectionResult result){
        GoNamesCache namecache = GoNamesCache.getInstance(file.getProject());
        Collection<String> allPackage = namecache.getAllPackages();
        allPackage.add("C");
        String selfPackageName = file.getFullPackageName();

        HashSet<String> hasVisitImportPath = new HashSet<String>();
        for (GoImportDeclarations importDeclarations : file.getImportDeclarations()) {
            for (GoImportDeclaration declaration : importDeclarations.getDeclarations()) {
                String importPathValue = null;
                GoLiteralString importPath = declaration.getImportPath();
                if (importPath != null) {
                    importPathValue = importPath.getValue();
                }

                if (importPathValue == null)
                    continue;

                if (importPathValue.isEmpty()) {
                    result.addProblem(declaration, GoBundle.message("error.import.path.is.empty"));
                    continue;
                }

                if (importPathValue.contains(" ") || importPathValue.contains("\t")) {
                    result.addProblem(declaration, GoBundle.message("error.import.path.contains.space"));
                    continue;
                }

                if (importPathValue.contains("\\")) {
                    result.addProblem(declaration, GoBundle.message("error.import.path.contains.backslash"));
                    continue;
                }

                if (!allPackage.contains(importPathValue)) {
                    result.addProblem(declaration, GoBundle.message("error.import.path.notfound", importPathValue));
                    continue;
                }

                if (importPathValue.equals(selfPackageName)) {
                    result.addProblem(declaration, GoBundle.message("error.import.path.equal.self",importPathValue));
                    continue;
                }
                if (hasVisitImportPath.contains(importPathValue)){
                    result.addProblem(declaration, GoBundle.message("error.import.path.repeat",importPathValue));
                    continue;
                }
                hasVisitImportPath.add(importPathValue);
            }
        }
    }
}
