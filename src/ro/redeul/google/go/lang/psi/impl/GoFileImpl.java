package ro.redeul.google.go.lang.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.toplevel.*;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 7:56:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoFileImpl extends PsiFileBase implements GoFile {

    public GoFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, GoFileType.GO_LANGUAGE);
    }

    @NotNull
    public FileType getFileType() {
        return GoFileType.GO_FILE_TYPE;
    }

    @Override
    public String toString() {
        return "Go file";
    }

    public GoPackageDeclaration getPackage() {
        return findChildByClass(GoPackageDeclaration.class);
    }

    public GoImportDeclaration[] getImportDeclarations() {
        return findChildrenByClass(GoImportDeclaration.class);
    }

    public GoFunctionDeclaration[] getFunctions() {
        return findChildrenByClass(GoFunctionDeclaration.class);
    }

    public GoMethodDeclaration[] getMethods() {
        return findChildrenByClass(GoMethodDeclaration.class);
    }

    public GoFunctionDeclaration getMainFunction() {
        GoFunctionDeclaration functionDeclarations[] = getFunctions();
        for (GoFunctionDeclaration functionDeclaration : functionDeclarations) {
            if (functionDeclaration.isMain()) {
                return functionDeclaration;
            }
        }

        return null;
    }

    public IElementType getTokenType() {
        return null;
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitFile(this);
    }

    public void acceptChildren(GoElementVisitor visitor) {
        PsiElement child = getFirstChild();
        while (child != null) {
            if (child instanceof GoPsiElement) {
                ((GoPsiElement) child).accept(visitor);
            }

            child = child.getNextSibling();
        }
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        PsiElement[] childs = getChildren();
        for (PsiElement child : childs) {
            if ( child instanceof GoTypeDeclaration || child instanceof GoMethodDeclaration ) {
                child.processDeclarations(processor, state, null, place);
            } else if ( child instanceof GoFunctionDeclaration ) {
                GoFunctionDeclaration functionDeclaration = (GoFunctionDeclaration) child;
                child.processDeclarations(processor, state, null, place);

                if ( lastParent == functionDeclaration ) {
                    break;
                }
            }
        }

        return super.processDeclarations(processor, state, lastParent, place);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
