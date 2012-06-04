package ro.redeul.google.go.lang.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.IndexingDataKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.components.GoSdkParsingHelper;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.utils.GoTokenSets;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.util.GoUtil;

import java.util.Collection;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 7:56:42 PM
 */
public class GoFileImpl extends PsiFileBase implements GoFile {

    private static final Logger LOG = Logger.getInstance(GoFileImpl.class);

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
    @Nullable
    public String getPackageImportPath() {

        if ( isApplicationPart() ) {
            return null;
        }

        // look in the current project to find the current package import path.
        VirtualFile virtualFile = getCurrentOrIndexedVirtualFile();

        // look to see if the file is part of the sdk and extract the import path from there.
        String importPath =
            GoSdkParsingHelper.getInstance()
                              .getPackageImportPath(getProject(), this, virtualFile);

        // the current file was part of the sdk
        if ( importPath != null ) {
            return importPath;
        }

        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(getProject()).getFileIndex();

        if ( ! projectFileIndex.isInSource(virtualFile) || projectFileIndex.isLibraryClassFile(virtualFile) ) {
            return null;
        }

        VirtualFile sourceRoot = projectFileIndex.getSourceRootForFile(virtualFile);
        if ( sourceRoot == null ) {
            return null;
        }

        String path = VfsUtil.getRelativePath(virtualFile.getParent(), sourceRoot, '/');

        if ( path == null || path.equals("") ) {
            path = getPackageName();
        }

        if ( path != null && ! path.endsWith(getPackageName()) ) {
            path = path + "/" + getPackageName();
        }

        String makefileTarget = GoUtil.getTargetFromMakefile(virtualFile.getParent().findChild("Makefile"));
        if ( makefileTarget != null ) {
            path = makefileTarget;
        }

        LOG.debug(String.format("%s -> %s", VfsUtil.getRelativePath(virtualFile, sourceRoot, '/'), path));

        return path;
    }

    public boolean isApplicationPart() {
        return getPackageName().equals("main");
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

    @Override
    public GoConstDeclarations[] getConsts() {
        return findChildrenByClass(GoConstDeclarations.class);
    }

    @Override
    public GoVarDeclarations[] getGlobalVariables() {
        return findChildrenByClass(GoVarDeclarations.class);
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

    private VirtualFile getCurrentOrIndexedVirtualFile() {
        VirtualFile virtualFile = getVirtualFile();

        // if the GoFile Psi tree was built from a memory data check to see
        // if it was built from an indexed copy
        if ( virtualFile == null ) {
            virtualFile = getUserData(IndexingDataKeys.VIRTUAL_FILE);
        }

        return virtualFile;
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
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place)
    {
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

                    if ( directory != null
                        && directory.isEquivalentTo(getOriginalFile().getContainingDirectory())
                        && ! file.isEquivalentTo(getOriginalFile()))
                    {
                        if (!file.processDeclarations(processor,
                                                      newState.put(GoResolveStates.IsOriginalFile, false),
                                                      null, place)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
