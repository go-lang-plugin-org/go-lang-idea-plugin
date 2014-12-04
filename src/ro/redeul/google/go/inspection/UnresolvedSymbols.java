package ro.redeul.google.go.inspection;

import com.google.common.collect.ImmutableList;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.QuickFix;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.inspection.fix.*;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.resolve.refs.PackageReference;
import ro.redeul.google.go.lang.psi.resolve.refs.VarOrConstReference;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.util.GoUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
                if (!identifier.isIota() && !identifier.isBlank() && !identifier.isNil()) {
                    tryToResolveReference(identifier);
                }
            }

            private void tryToResolveReference(final GoLiteralIdentifier id) {
                if (GoPsiUtils.hasHardReferences(id) && resolveSafely(id, PsiElement.class) == null) {
                    final PsiReference refs[] = id.getReferences();

                    List<LocalQuickFix> fixes =
                            id.accept(
                                    new GoElementVisitorWithData<ImmutableList.Builder<LocalQuickFix>>(new ImmutableList.Builder<LocalQuickFix>()) {
                                        @Override
                                        public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
                                            boolean hasPackageReference = false;
                                            boolean hasVarReferences = false;
                                            for (PsiReference ref : refs) {
                                                if (ref instanceof PackageReference)
                                                    hasPackageReference = true;

                                                if (ref instanceof VarOrConstReference)
                                                    hasVarReferences = true;
                                            }

                                            if ( hasVarReferences )
                                                if ( isGlobalVariableIdentifier(identifier) )
                                                    getData().add(new CreateGlobalVariableFix(identifier));
                                                else
                                                    getData()
                                                            .add(new CreateLocalVariableFix(identifier))
                                                            .add(new CreateGlobalVariableFix(identifier))
                                                            .add(new CreateFunctionFix(identifier))
                                                            .add(new CreateClosureFunctionFix(identifier));
                                            else if ( hasPackageReference )
                                                getData().add(new AddImportFix(identifier));

                                            ((GoPsiElement) identifier.getParent()).accept(this);
                                        }

                                        @Override
                                        public void visitTypeName(GoPsiTypeName typeName) {
                                            if (!typeName.isQualified())
                                                getData().add(new CreateTypeFix(id));
                                        }

                                        @Override
                                        public void visitLiteralExpression(GoLiteralExpression expression) {
                                            ((GoPsiElement) expression.getParent()).accept(this);
                                        }

                                        @Override
                                        public void visitCallOrConvExpression(GoCallOrConvExpression expression) {
                                            getData()
                                                    .add(new CreateFunctionFix(id))
                                                    .add(new CreateClosureFunctionFix(id));

                                        }
                                    }).build();

                    result.addProblem(
                            id, message("warning.unresolved.symbol", id.getText()),
                            ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                            fixes.toArray(new LocalQuickFix[fixes.size()]));
                }
            }
        }.visitElement(file);
    }

    private static boolean isGlobalVariableIdentifier(GoLiteralIdentifier ident) {
        return findParentOfType(ident, GoSelectorExpression.class) == null &&
               findParentOfType(ident, GoFunctionDeclaration.class) == null &&
               findParentOfType(ident, GoVarDeclarations.class) != null;
    }
}
