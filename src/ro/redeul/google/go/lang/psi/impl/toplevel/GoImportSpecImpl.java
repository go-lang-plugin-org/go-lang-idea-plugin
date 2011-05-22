package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveUtil;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import java.util.Collection;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 11:31:29 PM
 */
public class GoImportSpecImpl extends GoPsiElementImpl implements GoImportSpec {
    public GoImportSpecImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoPackageReference getPackageReference() {
        return findChildByClass(GoPackageReference.class);
    }

    public String getImportPath() {
        PsiElement stringLiteral = findChildByType(GoTokenTypes.litSTRING);

        return stringLiteral != null ? stringLiteral.getText() : "";
    }

    @Override
    public String getPackageName() {
        return GoResolveUtil.defaultPackageNameFromImport(getImportPath());
    }

    @Override
    public String getVisiblePackageName() {
        if (getPackageReference() == null) {
            return getPackageName();
        }

        GoPackageReference packageReference = getPackageReference();

        if (packageReference.isBlank() || packageReference.isLocal()) {
            return "";
        }

        return packageReference.getString();
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitImportSpec(this);
    }

    @Override
    public boolean processDeclarations(
            @NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        // import _ "a"; ( no declarations are visible from this import )
        if (getPackageReference() != null && getPackageReference().isBlank()) {
            return true;
        }

        GoNamesCache namesCache = ContainerUtil.findInstance(getProject().getExtensions(PsiShortNamesCache.EP_NAME), GoNamesCache.class);

        if (namesCache == null) {
            return true;
        }

        // get the file included in the imported package name
        Collection<GoFile> files = namesCache.getFilesByPackageName(getPackageName());

        for (GoFile file : files) {
            if (!file.processDeclarations(processor, GoResolveStates.imported(getPackageName(), getVisiblePackageName()), null, place))
            {
                return false;
            }
        }

        return true;
    }
}
