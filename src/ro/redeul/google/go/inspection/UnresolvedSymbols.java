package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.inspection.fix.*;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.resolve.references.BuiltinCallOrConversionReference;
import ro.redeul.google.go.lang.psi.resolve.references.CallOrConversionReference;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.utils.GoFileUtils;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.util.GoUtil;

import static ro.redeul.google.go.GoBundle.message;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

public class UnresolvedSymbols extends AbstractWholeGoFileInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Highlights unresolved symbols";
    }

    @Override
    protected void doCheckFile(@NotNull GoFile file,
                               @NotNull final InspectionResult result) {

        new GoRecursiveElementVisitor() {
            @Override
            public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
                visitVarDeclaration(declaration);
            }

            @Override
            public void visitVarDeclaration(GoVarDeclaration declaration) {
                for (GoExpr expr : declaration.getExpressions()) {
                    visitElement(expr);
                }
            }

            @Override
            public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
                if (!identifier.isIota() && !identifier.isBlank()) {
                    tryToResolveReference(identifier, identifier.getName());
                }
            }

            @Override
            public void visitLiteralExpression(GoLiteralExpression expression) {
                if (CallOrConversionReference.MATCHER.accepts(expression) ||
                        BuiltinCallOrConversionReference.MATCHER.accepts(expression)) {
                    tryToResolveReference(expression, expression.getText());
                } else {
                    super.visitLiteralExpression(expression);
                }
            }

            @Override
            public void visitTypeName(GoPsiTypeName typeName) {
                if (!typeName.isPrimitive()) {
                    tryToResolveReference(typeName, typeName.getName());
                }
            }

            private void tryToResolveReference(PsiElement element, String name) {
                if (GoPsiUtils.hasHardReferences(element) &&
                        resolveSafely(element, PsiElement.class) == null &&
                        !isCgoUsage(element)) {

                    LocalQuickFix[] fixes = null;
                    if (GoUtil.isFunctionNameIdentifier(element)) {
                        fixes = new LocalQuickFix[]{new CreateFunctionFix(element), new CreateClosureFunctionFix(element)};
                    } else if (isLocalVariableIdentifier(element)) {
                        fixes = new LocalQuickFix[]{
                                new CreateLocalVariableFix(element),
                                new CreateGlobalVariableFix(element),
                                new CreateFunctionFix(element),
                                new CreateClosureFunctionFix(element),
                        };
                    } else if (isGlobalVariableIdentifier(element)) {
                        fixes = new LocalQuickFix[]{new CreateGlobalVariableFix(element)};
                    } else if (isUnqualifiedTypeName(element)) {
                        fixes = new LocalQuickFix[]{new CreateTypeFix(element)};
                    } else {
                        String text = element.getLastChild().getText();
                        if (text.length() != 0 && Character.isUpperCase(text.charAt(0))) {
                         /*
                         * We can also resolve nested packages
                         */

                            final GoFile containingFile = (GoFile) element.getContainingFile();
                            final GoImportDeclarations[] importDeclarations = containingFile.getImportDeclarations();
                            for (GoImportDeclarations goImportDeclarations : importDeclarations) {
                                final GoImportDeclaration[] declarations = goImportDeclarations.getDeclarations();
                                for (final GoImportDeclaration declaration : declarations) {

                                    final String visiblePackageName = declaration.getVisiblePackageName();
                                    if (visiblePackageName.equals(element.getFirstChild().getText())) {

                                        final Project project = element.getProject();


                                        PsiFile[] files = null;

                                        VirtualFile packageFile = null;
                                        GoLiteralString importPath = declaration.getImportPath();
                                        if (importPath != null)
                                            packageFile = project.getBaseDir().findFileByRelativePath("src/" + importPath.getValue());

                                        if (packageFile != null) {
                                            PsiDirectory directory = PsiManager.getInstance(project).findDirectory(packageFile);
                                            if (directory != null)
                                                files = directory.getFiles();

                                        }


                                        PsiFile workingFile = null;
                                        if (files != null && files.length != 0) {
                                            for (PsiFile psiFile : files) {
                                                if (psiFile.getName().equals(visiblePackageName) || psiFile.getName().equals(declaration.getPackageName())) {
                                                    workingFile = psiFile;
                                                    break;
                                                }
                                            }
                                            if (workingFile == null) {
                                                workingFile = files[0];
                                            }

                                            if (workingFile != null) {

                                                fixes = new LocalQuickFix[]{
                                                        new CreateFunctionFix(element, workingFile)
                                                };
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                    if (fixes == null)
                        fixes = LocalQuickFix.EMPTY_ARRAY;
                    result.addProblem(
                            element,
                            message("warning.unresolved.symbol", name),
                            ProblemHighlightType.LIKE_UNKNOWN_SYMBOL, fixes);
                }
            }
        }.visitElement(file);
    }

    private boolean isCgoUsage(PsiElement element) {
        PsiFile file = element.getContainingFile();
        if (!(file instanceof GoFile)) {
            return false;
        }

        if (element instanceof GoPsiTypeName) {
            element = ((GoPsiTypeName) element).getIdentifier();
        }

        if (element instanceof GoLiteralExpression) {
            element = ((GoLiteralExpression) element).getLiteral();
        }

        if (!(element instanceof GoLiteralIdentifier)) {
            return false;
        }

        GoLiteralIdentifier identifier = (GoLiteralIdentifier) element;
        return "C".equals(identifier.getLocalPackageName()) && GoFileUtils.isPackageNameImported((GoFile) file, "C");

    }

    private static boolean isUnqualifiedTypeName(PsiElement element) {
        return element instanceof GoPsiTypeName &&
                !((GoPsiTypeName) element).getIdentifier().isQualified();
    }

    private static boolean isGlobalVariableIdentifier(PsiElement element) {
        return element instanceof GoLiteralIdentifier &&
                findParentOfType(element, GoSelectorExpression.class) == null &&
                findParentOfType(element, GoFunctionDeclaration.class) == null &&
                findParentOfType(element, GoVarDeclarations.class) != null;
    }

    private static boolean isLocalVariableIdentifier(PsiElement element) {
        return element instanceof GoLiteralIdentifier &&
                findParentOfType(element, GoSelectorExpression.class) == null &&
                findParentOfType(element, GoFunctionDeclaration.class) != null &&
                !element.textContains('.');
    }
}
