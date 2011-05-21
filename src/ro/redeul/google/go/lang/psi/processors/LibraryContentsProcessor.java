package ro.redeul.google.go.lang.psi.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoQualifiedNameElement;
import ro.redeul.google.go.lang.psi.resolve.GoResolveUtil;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import java.util.ArrayList;
import java.util.List;

public class LibraryContentsProcessor extends BaseScopeProcessor {

    private GoQualifiedNameElement qualifiedName;

    private List<Object> objects = new ArrayList<Object>();

    public LibraryContentsProcessor(GoQualifiedNameElement qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public boolean execute(PsiElement element, ResolveState state) {
        return tryTypeDeclaration(element, state);
    }

    private boolean tryTypeDeclaration(PsiElement element, ResolveState state) {
        if ( !(element instanceof GoTypeSpec) ) {
            return true;
        }

        GoTypeSpec typeSpec = (GoTypeSpec) element;

        GoTypeNameDeclaration typeNameDeclaration = typeSpec.getTypeNameDeclaration();

        if (typeNameDeclaration != null ) {
            String typeName = typeNameDeclaration.getName();

            if ( typeName != null && Character.isUpperCase(typeName.charAt(0)) ) {
                objects.add(qualifiedName.getPackageReference().getString() + "." + typeName);
            }
        }

        return true;
    }

    private boolean tryImportSpec(PsiElement element, ResolveState state) {

        if (!(element instanceof GoImportSpec)) {
            return true;
        }

        GoImportSpec importSpec = (GoImportSpec) element;

        if (!GoResolveUtil.inSamePackage(qualifiedName, importSpec)) {
            return true;
        }

        String importPath = GoPsiUtils.cleanupImportPath(importSpec.getImportPath());

        GoFile[] importedFiles = GoPsiUtils.findFilesForPackage(importPath, (GoFile) ((PsiElement)this.qualifiedName).getContainingFile().getOriginalFile());

        for (GoFile importedFile : importedFiles) {
            if (!importedFile.processDeclarations(this, state, null, element)) {
                return false;
            }
        }

        return false;
    }

    public Object[] getPackageContents() {
        return objects.toArray(new Object[objects.size()]);
    }
}
