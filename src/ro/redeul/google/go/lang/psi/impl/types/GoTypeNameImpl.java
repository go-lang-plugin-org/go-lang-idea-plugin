package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.util.GoSdkUtil;

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
        return getText();
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

    public PsiElement resolve() {

        NamedTypesScopeProcessor namedTypesProcessor = new NamedTypesScopeProcessor();

        if ( ! PsiScopesUtil.treeWalkUp(namedTypesProcessor, this, this.getContainingFile()) ) {
            return namedTypesProcessor.getFoundType();
        }

        PsiFile psiFile = this.getContainingFile();
        if ( ! (psiFile instanceof GoFile) ) {
            return null;
        }

        GoFile goFile = (GoFile) psiFile;

        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(getProject()).getFileIndex();
        Module module = projectFileIndex.getModuleForFile(goFile.getVirtualFile());

        Sdk sdk = GoSdkUtil.getGoogleGoSdkForModule(module);

        if ( sdk == null ) {
            return null;
        }

        GoImportDeclaration[] importDeclarations = goFile.getImportDeclarations();
        for (GoImportDeclaration importDeclaration : importDeclarations) {
            GoImportSpec[] importSpecs = importDeclaration.getImports();

            for (GoImportSpec importSpec : importSpecs) {
                GoIdentifier identifier = importSpec.getPackageName();
                if ( identifier != null && identifier.getString().equals(".") ) {
                    VirtualFile files[] = sdk.getRootProvider().getFiles(OrderRootType.SOURCES);

                    for (VirtualFile file : files) {
                        VirtualFile packageFile = VfsUtil.findRelativeFile(importSpec.getImportPath().replaceAll("\"",""), file);
                        if ( packageFile != null ) {
                            VirtualFile []children = packageFile.getChildren();
                            for (VirtualFile child : children) {
                                if ( child.getFileType() != GoFileType.GO_FILE_TYPE || child.getNameWithoutExtension().endsWith("_test") ) {
                                    continue;
                                }

                                GoFile packageGoFile = (GoFile) PsiManager.getInstance(getProject()).findFile(child);
                                assert packageGoFile != null;

                                GoTypeDeclaration[] typeDeclarations =  packageGoFile.getTypeDeclarations();

                                for (GoTypeDeclaration typeDeclaration : typeDeclarations) {
                                    if ( ! namedTypesProcessor.execute(typeDeclaration, ResolveState.initial()) ) {
                                        return namedTypesProcessor.getFoundType();
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }

    public boolean isSoft() {
        return true;
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeName(this);
    }

    private class NamedTypesScopeProcessor extends BaseScopeProcessor {
        private PsiElement foundType;

        public boolean execute(PsiElement element, ResolveState state) {
            if (element instanceof GoTypeDeclaration) {
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
            }

            return true;
        }

        public PsiElement getFoundType() {
            return foundType;
        }
    }
}
