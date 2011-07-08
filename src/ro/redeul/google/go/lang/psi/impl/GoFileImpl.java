package ro.redeul.google.go.lang.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import java.util.Collection;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 7:56:42 PM
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
        return ContainerUtil.mapNotNull(findChildrenByClass(GoFunctionDeclaration.class), new Function<GoFunctionDeclaration, GoFunctionDeclaration>() {
            @Override
            public GoFunctionDeclaration fun(GoFunctionDeclaration functionDeclaration) {
                if ( functionDeclaration instanceof GoMethodDeclaration ) {
                    return null;
                }

                return functionDeclaration;
            }
        }, new GoFunctionDeclaration[]{});
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

    public GoTypeDeclaration[] getTypeDeclarations() {
        return findChildrenByClass(GoTypeDeclaration.class);
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
    public String getPackageName() {
        return getPackage().getPackageName();
    }

    @Override
    public String getQualifiedName() {
        return String.format("%s.%s", getPackageName(), getName());
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

        String myPackageName = getPackage().getPackageName();

        ResolveState newState = state.put(GoResolveStates.PackageName, myPackageName);

        if ( ! state.get(GoResolveStates.ResolvingVariables) ) {
            GoTypeDeclaration declarations[] = getTypeDeclarations();
            for (GoTypeDeclaration declaration : declarations) {
                if (declaration != lastParent) {
                    if (!declaration.processDeclarations(processor, newState, null, place)) {
                        return false;
                    }
                }
            }
        }

        if ( ! state.get(GoResolveStates.ResolvingVariables) ) {
            GoMethodDeclaration methods[] = getMethods();
            for (GoMethodDeclaration method : methods) {
                if (method != lastParent) {
                    if (!method.processDeclarations(processor, newState, null, place)) {
                        return false;
                    }
                }
            }
        }

        if ( ! state.get(GoResolveStates.ResolvingVariables)) {
            GoFunctionDeclaration functions[] = getFunctions();
            for (GoFunctionDeclaration function : functions) {
                if (function != lastParent) {
                    if (!function.processDeclarations(processor, newState, null, place)) {
                        return false;
                    }
                }
            }
        }

        if ( newState.get(GoResolveStates.IsOriginalFile) && ! (state.get(GoResolveStates.ResolvingVariables)) ) {

            GoImportDeclaration importDeclarations[] = getImportDeclarations();
            for (GoImportDeclaration importDeclaration : importDeclarations) {
                if (importDeclaration != lastParent) {
                    if (!importDeclaration.processDeclarations(processor, newState.put(GoResolveStates.IsOriginalFile, false), null, place)) {
                        return false;
                    }
                }
            }

            GoNamesCache namesCache = ContainerUtil.findInstance(getProject().getExtensions(PsiShortNamesCache.EP_NAME), GoNamesCache.class);

            if (namesCache != null) {
                Collection<GoFile> files = namesCache.getFilesByPackageName(myPackageName);

                for (GoFile file : files) {

                    PsiDirectory directory = file.getContainingDirectory();

                    if ( directory != null && directory.isEquivalentTo(getOriginalFile().getContainingDirectory()) && ! file.isEquivalentTo(getOriginalFile()))
                    {
                        if (!file.processDeclarations(processor, newState.put(GoResolveStates.IsOriginalFile, false), null, place))
                        {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
