package ro.redeul.google.go.lang.psi.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.extapi.psi.PsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import java.util.*;

public class GoPackageImpl extends PsiElementBase implements GoPackage {

    private PsiManager myPsiManager;

    private final VirtualFile mySourceRootFile;
    private final VirtualFile myPackageFile;

    public GoPackageImpl(VirtualFile packageFile, VirtualFile packageSourceRoot, PsiManager psiManager) {
        this.myPackageFile = packageFile;
        this.mySourceRootFile = packageSourceRoot;
        this.myPsiManager = psiManager;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitPackage(this);
    }

    @Override
    public <T> T accept(GoElementVisitorWithData<T> visitor) {
        accept((GoElementVisitor) visitor);
        return visitor.getData();
    }

    @Override
    public void acceptChildren(GoElementVisitor visitor) {

    }

    @Override
    public LookupElementBuilder getCompletionPresentation() {
        return null;
    }

    @Override
    public LookupElementBuilder getCompletionPresentation(GoPsiElement child) {
        return null;
    }

    @Override
    public String getPresentationText() {
        return null;
    }

    @Override
    public String getPresentationTailText() {
        return null;
    }

    @Override
    public String getPresentationTypeText() {
        return null;
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return GoLanguage.INSTANCE;
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        // TODO: finish this implementation
        return new PsiElement[0];
    }

    @Override
    public PsiElement getParent() {
        return null;
    }

    @Override
    public PsiElement getFirstChild() {
        // TODO: finish this implementation
        return null;
    }

    @Override
    public PsiElement getLastChild() {
        // TODO: finish this implementation
        return null;
    }

    @Override
    public PsiElement getNextSibling() {
        return null;
    }

    @Override
    public PsiElement getPrevSibling() {
        return null;
    }

    @Override
    public TextRange getTextRange() {
        return null;
    }

    @Override
    public int getStartOffsetInParent() {
        return 0;
    }

    @Override
    public int getTextLength() {
        return 0;
    }

    @Nullable
    @Override
    public PsiElement findElementAt(int offset) {
        return null;
    }

    @Override
    public int getTextOffset() {
        return 0;
    }

    @Override
    public String getText() {
        return null;
    }

    @NotNull
    @Override
    public char[] textToCharArray() {
        return new char[0];
    }

    @Override
    public boolean textContains(char c) {
        return false;
    }

    @Override
    public ASTNode getNode() {
        return null;
    }

    @Override
    @NotNull
    public PsiManager getManager() {
        return myPsiManager;
    }

    @Override
    public String getImportPath() {
        return VfsUtil.getRelativePath(myPackageFile, mySourceRootFile, '/');
    }

    @NotNull
    public String getName() {

        // fix this to handle test packages and other things.
        for (GoFile file : getFiles()) {
            return file.getPackage().getPackageName();
        }

        return "";
    }

    @Override
    public GoFile[] getFiles() {
        GoNamesCache namesCache = GoNamesCache.getInstance(getProject());

        List<GoFile> files = new ArrayList<GoFile>(namesCache.getFilesByPackageImportPath(getImportPath()));

        Collections.sort(files, new Comparator<GoFile>() {
            @Override
            public int compare(GoFile o1, GoFile o2) {
                return o1.getVirtualFile().getName().compareTo(o2.getVirtualFile().getName());
            }
        });

        return files.toArray(new GoFile[files.size()]);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state, PsiElement lastParent,
                                       @NotNull PsiElement place) {

        GoFile files[] = getFiles();

        for (GoFile file : files) {

            ProgressIndicatorProvider.checkCanceled();

            if ( lastParent != file && !file.processDeclarations(processor, state, lastParent, place) )
                return false;
        }

        return true;
    }

    @NotNull
    @Override
    public PsiDirectory[] getDirectories() {
        return new PsiDirectory[] { myPsiManager.findDirectory(myPackageFile) };
    }

    @NotNull
    @Override
    public PsiDirectory[] getDirectories(@NotNull GlobalSearchScope scope) {
        return new PsiDirectory[] { myPsiManager.findDirectory(myPackageFile) };
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }

    @Override
    public GoPsiElement getReferenceContext() {
        return this;
    }
}
