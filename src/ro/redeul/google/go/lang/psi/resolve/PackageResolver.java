package ro.redeul.google.go.lang.psi.resolve;

import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.resolve.references.PackageReference;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;

public class PackageResolver extends GoPsiReferenceResolver<PackageReference> {

    public PackageResolver(PackageReference reference) {
        super(reference);
    }

    @Override
    public void visitImportDeclaration(GoImportDeclaration declaration) {
        if (isReferenceTo(declaration))
            addDeclaration(declaration);
    }

    boolean isReferenceTo(GoImportDeclaration importDeclaration) {

        GoPackageReference packageReference = importDeclaration.getPackageReference();

        String packageName = null;
        if ( packageReference != null && !(packageReference.isBlank() || packageReference.isLocal()) )
            packageName = packageReference.getString();
        else {
            GoPackage goPackage = importDeclaration.getPackage();
            packageName = goPackage != null ? goPackage.getName() : null;
        }

        return packageName != null && packageName.equals(getReference().getCanonicalText());
    }
}
