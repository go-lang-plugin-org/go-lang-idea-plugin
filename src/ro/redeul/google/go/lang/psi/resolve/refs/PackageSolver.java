package ro.redeul.google.go.lang.psi.resolve.refs;

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

                String packageName = null;
                if (packageReference != null && !(packageReference.isBlank() || packageReference.isLocal()))
                    packageName = packageReference.getString();
                else {
                    GoPackage goPackage = importDeclaration.getPackage();
                    packageName = goPackage != null ? goPackage.getName() : "";
                }

                return packageName != null && matchNames(referenceName(), packageName);
            }
        });
    }
}
