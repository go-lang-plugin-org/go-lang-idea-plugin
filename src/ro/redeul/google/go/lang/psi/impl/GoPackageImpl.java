package ro.redeul.google.go.lang.psi.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.extapi.psi.PsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.util.LookupElementUtil;

import java.util.*;

public class GoPackageImpl extends PsiElementBase implements GoPackage {

    private PsiManager myPsiManager;

    private final VirtualFile mySourceRootFile;
    private final VirtualFile myPackageFile;
    private final boolean myTestPackage;

    public GoPackageImpl(VirtualFile packageFile, VirtualFile packageSourceRoot, PsiManager psiManager, boolean testPackage) {
        this.myPackageFile = packageFile;
        this.mySourceRootFile = packageSourceRoot;
        this.myPsiManager = psiManager;
        this.myTestPackage = testPackage;
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
    @Nullable
    public LookupElementBuilder getLookupPresentation() {
        return LookupElementUtil.createLookupElement(this);
    }

    @Override
    @Nullable
    public LookupElementBuilder getLookupPresentation(GoPsiElement child) {
        return LookupElementUtil.createLookupElement(this, child);
    }

    @Override
    public String getLookupText() {
        return null;
    }

    @Override
    public String getLookupTailText() {
        return null;
    }

    @Override
    public String getLookupTypeText() {
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
        GoFile[] files = getFiles();
        return files.length > 0 ? files[0].getPackage().getPackageName() : "";
    }

    @Override
    public GoFile[] getFiles() {
        return getFiles(isTestPackage());
    }

    @Override
    public boolean isTestPackage() {
        return myTestPackage;
    }

    private GoFile[] getFiles(final boolean includeTestFiles) {
        GoNamesCache namesCache = GoNamesCache.getInstance(getProject());

        List<GoFile> files = new ArrayList<GoFile>(namesCache.getFilesByPackageImportPath(getImportPath()));

        files = ContainerUtil.filter(files, new Condition<GoFile>() {
            @Override
            public boolean value(GoFile file) {
                return includeTestFiles || !(file.getVirtualFile().getName().endsWith("_test.go"));
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

    @Override
    public String toString(){
        return "GoPackageImpl("+getImportPath()+")";
    }
}
