package ro.redeul.google.go.codeInsight.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import java.util.Collection;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.cleanupImportPath;

public class GotoPackageHandler extends GotoDeclarationHandlerBase {
    @Override
    public PsiElement getGotoDeclarationTarget(PsiElement sourceElement, Editor editor) {
        if (sourceElement == null) {
            return null;
        }

        ASTNode node = sourceElement.getNode();
        if (node == null) {
            return null;
        }

        if ("LITERAL_STRING".equals(node.getElementType().toString())) {
            PsiElement parent = sourceElement.getParent();
            if (parent instanceof GoImportDeclaration) {
                return getPackageDefinition((GoImportDeclaration) parent);
            }
        }
        return null;
    }

    private PsiElement getPackageDefinition(GoImportDeclaration id) {
        String packageName = cleanupImportPath(id.getImportPath());
        GoNamesCache namesCache = GoNamesCache.getInstance(id.getProject());

        Collection<GoFile> files = namesCache.getFilesByPackageName(packageName);
        return files.isEmpty() ? null : files.iterator().next();
    }
}
