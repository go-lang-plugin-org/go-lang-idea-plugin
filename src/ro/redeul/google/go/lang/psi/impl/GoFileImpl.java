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
import ro.redeul.google.go.components.GoSdkParsingHelper;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.utils.GoTokenSets;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
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

    @Override
    public String getPackageImportPath() {
        return GoSdkParsingHelper.getInstance().getPackageImportPath(getProject(), this);
    }

    public GoPackageDeclaration getPackage() {
        return findChildByClass(GoPackageDeclaration.class);
    }

    public GoImportDeclarations[] getImportDeclarations() {
        return findChildrenByClass(GoImportDeclarations.class);
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

        if ( state.get(GoResolveStates.ResolvingVariables) ) {
            return true;
        }

        String myPackageName = getPackage().getPackageName();

        ResolveState newState = state.put(GoResolveStates.PackageName, myPackageName);

        // process current file
        PsiElement child = lastParent != null ? lastParent.getPrevSibling() : this.getLastChild();
        while ( child != null ) {

            if ( GoPsiUtils.isNodeOfType(child, GoTokenSets.GO_FILE_ENTRY_POINT_TYPES) ) {

                boolean shouldProcessDeclarations = true;

                if ( child instanceof GoImportDeclarations) {
                    shouldProcessDeclarations = state.get(GoResolveStates.IsOriginalFile);
                    newState = newState.put(GoResolveStates.IsOriginalFile, false);
                }

                if ( shouldProcessDeclarations ) {
                    if ( ! child.processDeclarations(processor, newState, null, place)) {
                        return false;
                    }
                }
            }

            child = child.getPrevSibling();
        }

        if ( state.get(GoResolveStates.IsOriginalFile) ) {
            GoNamesCache namesCache = ContainerUtil.findInstance(getProject().getExtensions(PsiShortNamesCache.EP_NAME), GoNamesCache.class);

            if (namesCache != null) {
                Collection<GoFile> files = namesCache.getFilesByPackageName(myPackageName);

                for (GoFile file : files) {

                    PsiDirectory directory = file.getContainingDirectory();

                    if ( directory != null && directory.isEquivalentTo(getOriginalFile().getContainingDirectory()) && ! file.isEquivalentTo(getOriginalFile())) {
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
