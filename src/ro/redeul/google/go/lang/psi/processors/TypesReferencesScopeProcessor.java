package ro.redeul.google.go.lang.psi.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.resolve.GoResolveUtil;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 21, 2010
 * Time: 4:47:46 AM
 */
public class TypesReferencesScopeProcessor extends BaseScopeProcessor {

    private PsiElement foundType;
    private GoTypeName typeName;

    public TypesReferencesScopeProcessor(GoTypeName typeName) {
        this.typeName = typeName;
    }

    public boolean execute(PsiElement element, ResolveState state) {
        if (!tryTypeDeclaration(element, state)) {
            return false;
        }

        if (!tryImportSpec(element, state)) {
            return false;
        }

        return true;
    }

    private boolean tryImportSpec(PsiElement element, ResolveState state) {

        if (!(element instanceof GoImportSpec)) {
            return true;
        }

        GoImportSpec importSpec = (GoImportSpec) element;

        if ( ! GoResolveUtil.inSamePackage(typeName, importSpec)) {
            return true;
        }


        String importPath = GoPsiUtils.cleanupImportPath(importSpec.getImportPath());

        GoFile[] importedFiles = GoPsiUtils.findFilesForPackage(importPath, (GoFile) element.getContainingFile());

        for (GoFile importedFile : importedFiles) {
            if (!importedFile.processDeclarations(this, state, null, typeName)) {
                return false;
            }
        }

        return true;
    }

    private boolean tryTypeDeclaration(PsiElement element, ResolveState state) {
        if (!(element instanceof GoTypeDeclaration)) {
            return true;
        }

        GoTypeDeclaration typeDeclaration = (GoTypeDeclaration) element;

        for (GoTypeSpec typeSpec : typeDeclaration.getTypeSpecs()) {

            GoTypeNameDeclaration typeNameDeclaration = typeSpec.getTypeNameDeclaration();
            if (typeNameDeclaration != null) {
                String typeName = typeNameDeclaration.getName();
                if (typeName != null && typeName.equals(this.typeName.getName())) {
                    foundType = typeNameDeclaration;
                    return false;
                }
            }
        }

        return true;
    }

    public PsiElement getFoundType() {
        return foundType;
    }
}
