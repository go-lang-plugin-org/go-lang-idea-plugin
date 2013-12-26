package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.references.ImportReference;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import java.util.Collection;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getAbsoluteImportPath;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getContainingGoFile;

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
        GoLiteralString importPath = getImportPath();

        if (importPath != null)
            return GoPsiUtils.findDefaultPackageName(importPath.getValue());

        return "";
    }

    @Override
    @NotNull
    public String getVisiblePackageName() {
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
        if (packageReference != null && packageReference.isBlank()) {
            return true;
        }

        GoNamesCache namesCache = GoNamesCache.getInstance(getProject());

        GoLiteralString importPath = getImportPath();
        //Some times import path can be null
        if (importPath == null)
            return true;
        GoFile goFile = getContainingGoFile(this);

        String importPathValue = getAbsoluteImportPath(importPath.getValue(), goFile);

        // get the file included in the imported package name
        Collection<GoFile> files =
                namesCache.getFilesByPackageImportPath(importPathValue);

        for (GoFile file : files) {
            ResolveState newState =
                    GoResolveStates.imported(getPackageName(), getVisiblePackageName());

            if (!file.processDeclarations(processor, newState, lastParent, place))
                return false;
        }

        return true;
    }

    @Override
    public PsiReference getReference() {
        return new ImportReference(this);
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
}
