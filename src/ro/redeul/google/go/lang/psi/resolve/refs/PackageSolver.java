package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;

public class PackageSolver extends VisitingReferenceSolver<PackageReference, PackageSolver> {

    @Override
    public PackageSolver self() { return this; }

    public PackageSolver(final PackageReference reference) {
        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {
            public void visitImportDeclaration(GoImportDeclaration declaration) {
                if (isReferenceTo(declaration))
                    addTarget(declaration);
            }

            boolean isReferenceTo(GoImportDeclaration importDeclaration) {

                GoPackageReference packageReference = importDeclaration.getPackageReference();

                String packageName;
                if (packageReference != null && !(packageReference.isBlank() || packageReference.isLocal()))
                    packageName = packageReference.getString();
                else {
                    GoPackage goPackage = importDeclaration.getPackage();
                    packageName = goPackage.getName();
                }

                return packageName != null && matchNames(reference.name(), packageName);
            }
        });
    }

    @Override
    public PsiElement resolve(@NotNull PackageReference reference, boolean incompleteCode) {
        PsiElement resolve = super.resolve(reference, incompleteCode);
        return resolve == null ? GoPackages.Invalid : resolve;
    }
}
