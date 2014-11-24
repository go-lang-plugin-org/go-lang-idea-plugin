package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 11:31:29 PM
 */
public class GoImportDeclarationImpl extends GoPsiElementBase implements GoImportDeclaration {
    public GoImportDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoPackageReference getPackageReference() {
        return findChildByClass(GoPackageReference.class);
    }

    public GoLiteralString getImportPath() {
        return findChildByClass(GoLiteralString.class);
    }

    @Override
    public String getPackageName() {
        GoLiteralString importPathLiteral = getImportPath();

        if (importPathLiteral == null)
            return "";

        return getPackage().getName();
    }

    @Override
    @NotNull
    public String getPackageAlias() {
        GoPackageReference packageReference = getPackageReference();

        if (packageReference == null) {
            return getPackageName();
        }

        if (packageReference.isBlank() || packageReference.isLocal()) {
            return "";
        }

        return packageReference.getString();
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitImportDeclaration(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state, PsiElement lastParent,
                                       @NotNull PsiElement place) {

        // import _ "a"; ( no declarations are visible from this import )
        GoPackageReference packageReference = getPackageReference();
        if (packageReference != null && packageReference.isBlank())
            return true;

        // import . "asdfaf" -> exports in the target package should act as declaration in the current one (but only if this is the initial state)
        if ( packageReference != null && packageReference.isLocal() && lastParent != null )
            return getPackage().processDeclarations(processor, ResolveStates.packageExports(), null, place);

        return processor.execute(this, state);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("Not implemented");
    }

    @NotNull
    @Override
    public String getLookupText() {
        return getPackageAlias();
    }

    @Override
    public String getLookupTypeText() {
        GoPackageReference packageReference = getPackageReference();

        if (packageReference != null && !packageReference.isBlank() && !packageReference.isLocal()) {
            return "package alias";
        }

        return "package";
    }

    @Nullable
    @Override
    public String getLookupTailText() {
        GoLiteralString importPathLiteral = getImportPath();
        GoPackage goPackage = getPackage();

        if (importPathLiteral == null)
            return null;

        if (goPackage != GoPackages.Invalid)
            return String.format(" (%s:%s)", goPackage.getName(), importPathLiteral.getValue());

        // TODO: decide if we want to include invalid import statements here
        return String.format(" (<invalid>:%s)", importPathLiteral.getValue());
    }

    @Override
    public boolean isValidImport() {
        String importPathValue = null;
        GoLiteralString importPath = this.getImportPath();
        if (importPath != null) {
            importPathValue = importPath.getValue();
        }

        return !(importPathValue == null || importPathValue.isEmpty()) && !(importPathValue.contains(" ") || importPathValue.contains("\t")) && !importPathValue.contains("\\");
    }

    @NotNull
    @Override
    public GoPackage getPackage() {

        GoLiteralString importPathLiteral = getImportPath();
        if (importPathLiteral == null)
            return GoPackages.Invalid;

        String importPath = importPathLiteral.getValue();

        boolean testPackage = false;
        if ( getContainingFile() instanceof GoFile ) {
            GoFile file = (GoFile) getContainingFile();

            testPackage = file.isTestFile();

            // if it's a relative import we normalize this and hope it falls into our project.
            // we don't support relative import paths from files outside of our project
            if ( importPath.startsWith(".") ) {
                importPath = FileUtil.toCanonicalPath(file.getPackageImportPath() + "/" + importPath, '/');
            }
        }

        return GoPackages.getInstance(getProject()).getPackage(importPath, testPackage);
    }
}
