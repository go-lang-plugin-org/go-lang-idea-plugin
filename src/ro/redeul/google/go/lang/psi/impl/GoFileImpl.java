package ro.redeul.google.go.lang.psi.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScopes;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.IndexingDataKeys;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.components.GoSdkParsingHelper;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.util.GoUtil;
import ro.redeul.google.go.util.LookupElementUtil;

import java.util.Collection;

public class GoFileImpl extends PsiFileBase implements GoFile {

    private static final Logger LOG = Logger.getInstance(GoFileImpl.class);

    public GoFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, GoLanguage.INSTANCE);
    }

    @NotNull
    public FileType getFileType() {
        return GoFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Go file";
    }

    @Override
    @NotNull
    public String getPackageImportPath() {

        // look in the current project to find the current package import path.
        VirtualFile virtualFile = getCurrentOrIndexedVirtualFile();

        // look to see if the file is part of the sdk and extract the import path from there.
        String importPath =
            GoSdkParsingHelper.getInstance()
                              .getPackageImportPath(getProject(), this,
                                                    virtualFile);

        // the current file was part of the sdk
        if (importPath != null) {
            return importPath;
        }

        ProjectFileIndex projectFileIndex =
            ProjectRootManager.getInstance(getProject()).getFileIndex();

        if (!projectFileIndex.isInSource(virtualFile) ||
            projectFileIndex.isLibraryClassFile(virtualFile)) {
            return "";
        }

        VirtualFile sourceRoot =
            projectFileIndex.getSourceRootForFile(virtualFile);

        if (sourceRoot == null) {
            return "";
        }

        String path = VfsUtil.getRelativePath(virtualFile.getParent(),
                                              sourceRoot, '/');

        if (path == null || path.equals(""))
            path = getPackageName();

        if (path != null && !isApplicationPart() && !path.endsWith(getPackageName()) && !path.toLowerCase().endsWith(getPackageName())) {
            path = path + "/" + getPackageName();
        }

        String makefileTarget =
            GoUtil.getTargetFromMakefile(
                virtualFile.getParent().findChild("Makefile"));

        if (makefileTarget != null) {
            path = makefileTarget;
        }

        if (path == null)
            path = "";

        return path;
    }

    public boolean isApplicationPart() {
        return getPackageName().equals("main");
    }

    public GoPackageDeclaration getPackage() {
        return findChildByClass(GoPackageDeclaration.class);
    }

    @Override
    public String getFullPackageName() { // HACK having to do with package names
        PsiDirectory parent = this.getContainingDirectory();
        if (parent == null) {
            return getPackageName();
        }
        String packageDir = parent.getVirtualFile().getCanonicalPath();
        if (packageDir == null){
            return getPackageName();
        }
        if (!packageDir.contains("/src/")) {
            return getPackageName();
        }
        int begin = packageDir.lastIndexOf("/src/") + 5;
        return packageDir.substring(begin);
    }

    public GoImportDeclarations[] getImportDeclarations() {
        return findChildrenByClass(GoImportDeclarations.class);
    }

    public GoFunctionDeclaration[] getFunctions() {
        return
            ContainerUtil.mapNotNull(
                findChildrenByClass(GoFunctionDeclaration.class),
                new Function<GoFunctionDeclaration, GoFunctionDeclaration>() {
                    @Override
                    public GoFunctionDeclaration fun(
                        GoFunctionDeclaration functionDeclaration) {
                        if (functionDeclaration instanceof GoMethodDeclaration) {
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

        if (virtualFile == null) {
            virtualFile = getOriginalFile().getVirtualFile();
        }
        // if the GoFile Psi tree was built from a memory data check to see
        // if it was built from an indexed copy
        if (virtualFile == null) {
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

    @Override
    public <T> T accept(GoElementVisitorWithData<T> visitor) {
        accept((GoElementVisitor) visitor);
        return visitor.getData();
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
                                       @NotNull PsiElement place) {
        if (lastParent == this)
            return false;

        PsiElement child = this.getLastChild();

        while (child != null) {
            if (!(child instanceof GoImportDeclarations) &&
                !child.processDeclarations(processor, state, null, place))
                return false;

            child = child.getPrevSibling();
        }

        if (state.get(GoResolveStates.IsOriginalFile)) {
            ResolveState newState =
                state.put(GoResolveStates.IsOriginalFile, false);

            GoNamesCache names = GoNamesCache.getInstance(getProject());

            PsiDirectory parentDirectory = getParent();
            Collection<GoFile> goFiles;

            if (isApplicationPart() && parentDirectory != null) {
                goFiles =
                    names.getFilesByPackageImportPath(
                        getPackageImportPath(),
                        GlobalSearchScopes.directoryScope(parentDirectory, false));
            } else {
                goFiles = names.getFilesByPackageImportPath(getPackageImportPath());
            }

            for (GoFile goFile : goFiles) {
                if (!goFile.getOriginalFile().equals(this.getOriginalFile())) {
                    if (!goFile.processDeclarations(processor, newState, null, place))
                        return false;
                }
            }

            for (GoImportDeclarations importDeclarations : getImportDeclarations()) {
                if (!importDeclarations.processDeclarations(processor, state, null, place))
                    return false;
            }
        }

        return true;
    }

    @Override
    final public LookupElementBuilder getCompletionPresentation() {
        return LookupElementUtil.createLookupElement(this);
    }

    @Override
    public LookupElementBuilder getCompletionPresentation(GoPsiElement child) {
        return LookupElementUtil.createLookupElement(this);
    }

    @Override
    public String getPresentationText() {
        return "";
    }

    @Override
    public String getPresentationTailText() {
        return "";
    }

    @Override
    public String getPresentationTypeText() {
        return "";
    }
}
