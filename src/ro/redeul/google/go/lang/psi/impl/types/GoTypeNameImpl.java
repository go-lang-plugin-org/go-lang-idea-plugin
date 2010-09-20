package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 7:12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoTypeNameImpl extends GoPsiElementImpl implements GoTypeName {

    public GoTypeNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "TypeName";
    }

    @Override
    public String getName() {

        GoIdentifier identifier = findChildByClass(GoIdentifier.class);

        return identifier != null ? identifier.getText() : getText();
    }

    public PsiElement setName(@NonNls String name) throws IncorrectOperationException {
        return null;
    }

    public PsiElement getElement() {
        return this;
    }

    public TextRange getRangeInElement() {
        return new TextRange(0, getTextLength());
    }

    public GoPackageReference getPackageReference() {
        return findChildByClass(GoPackageReference.class);
    }

    public PsiElement resolve() {

        NamedTypesScopeProcessor namedTypesProcessor = new NamedTypesScopeProcessor();

        if ( ! PsiScopesUtil.treeWalkUp(namedTypesProcessor, this, this.getContainingFile()) ) {
            return namedTypesProcessor.getFoundType();
        }

        return null;
    }

    @NotNull
    public String getCanonicalText() {
        return getText();
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return this;
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if (isReferenceTo(element))
            return this;

        throw new IncorrectOperationException("Cannot bind to:" + element + " of class " + element.getClass());
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    public boolean isReferenceTo(PsiElement element) {
        return true;
    }

    @NotNull
    public Object[] getVariants() {
//        NamedTypesScopeProcessor2 namedTypesProcessor = new NamedTypesScopeProcessor2();
//
//        PsiScopesUtil.treeWalkUp(namedTypesProcessor, this, this.getContainingFile());
//
//        return namedTypesProcessor.references();
        return new Object[0];
    }

    public boolean isSoft() {
        return false;
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeName(this);
    }

    private class NamedTypesScopeProcessor2 extends BaseScopeProcessor {
        List<PsiElement> references = new ArrayList<PsiElement>();

        public boolean execute(PsiElement element, ResolveState state) {

            collectTypeDeclarations(element, state);

            collectImports(element, state);

            return true;
        }

        private void collectImports(PsiElement element, ResolveState state) {
            if ( ! (element instanceof GoImportSpec) ) {
                return;
            }

            GoImportSpec importSpec = (GoImportSpec) element;

            if ( ! importSpec.getPackageReference().isLocal() ) {
                if ( ! importSpec.getPackageReference().isBlank() ) {
                    references.add(importSpec.getPackageReference());
                }
            }
        }

        private void collectTypeDeclarations(PsiElement element, ResolveState state) {
            if ( ! (element instanceof GoTypeDeclaration) ) {
                return;
            }

            GoTypeDeclaration typeDeclaration = (GoTypeDeclaration) element;

            for (GoTypeSpec typeSpec : typeDeclaration.getTypeSpecs()) {

                GoTypeNameDeclaration typeNameDeclaration = typeSpec.getTypeNameDeclaration();

                if (typeNameDeclaration != null) {
                    references.add(typeNameDeclaration);
                }
            }
        }

        public PsiElement[] references() {
            return  references.toArray(new PsiElement[references.size()]);
        }
    }

    private class NamedTypesScopeProcessor extends BaseScopeProcessor {

        private PsiElement foundType;

        public boolean execute(PsiElement element, ResolveState state) {
            if ( ! tryTypeDeclaration(element, state) ) {
                return false;
            }

            if ( ! tryImportSpec(element, state) ) {
                return false;
            }

            return true;
        }

        private boolean tryImportSpec(PsiElement element, ResolveState state) {
            if ( ! (element instanceof GoImportSpec) ) {
                return true;
            }

            GoImportSpec importSpec = (GoImportSpec) element;

            String importPath = GoPsiUtils.cleanupImportPath(importSpec.getImportPath());

            GoFile[] importedFiles = GoPsiUtils.findFilesForPackage(importPath, (GoFile) element.getContainingFile());

            for (GoFile importedFile : importedFiles) {
                if ( ! importedFile.processDeclarations(this, state, null, GoTypeNameImpl.this) ) {
                    return false;
                }
            }

            return true;
        }

        private boolean tryTypeDeclaration(PsiElement element, ResolveState state) {
            if ( ! (element instanceof GoTypeDeclaration) ) {
                return true;
            }

            GoTypeDeclaration typeDeclaration = (GoTypeDeclaration) element;

            for (GoTypeSpec typeSpec : typeDeclaration.getTypeSpecs()) {

                GoTypeNameDeclaration typeNameDeclaration = typeSpec.getTypeNameDeclaration();
                if (typeNameDeclaration != null) {
                    String typeName = typeNameDeclaration.getName();
                    if (typeName != null && typeName.equals(getName())) {
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
}
