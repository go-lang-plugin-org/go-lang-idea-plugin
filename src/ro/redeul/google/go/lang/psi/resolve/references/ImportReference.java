package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

public class ImportReference extends GoPsiReference<GoImportDeclaration, ImportReference> implements PsiPolyVariantReference {

    public ImportReference(GoImportDeclaration element) {
        super(element);
    }

    @Override
    protected ImportReference self() {
        return this;
    }

    @Override
    public TextRange getRangeInElement() {
        GoLiteralString importPath = getElement().getImportPath();
        if (importPath == null) {
            return TextRange.EMPTY_RANGE;
        }

        return new TextRange(
            importPath.getStartOffsetInParent(),
            importPath.getStartOffsetInParent() + importPath.getTextLength()
        );
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getImportPath().getValue();
    }

    @Override
    public PsiElement resolve() {
        return null;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return this.element == element;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
//        return new Object[] {
//            LookupElementBuilder.create("xx", "\"aaa\""),
//            LookupElementBuilder.create("xx", "\"aab\""),
//            LookupElementBuilder.create("xx", "\"aab\"")
//        };
        return new Object[]{};
//        return null;
    }

    @Override
    public boolean isSoft() {
        return true;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        GoLiteralString importPath = element.getImportPath();
        if (importPath == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        GoNamesCache namesCache = GoNamesCache.getInstance(element.getProject());

        List<ResolveResult> files = new ArrayList<ResolveResult>();
        for (GoFile file : namesCache.getFilesByPackageImportPath(importPath.getValue())) {
            files.add(new PsiElementResolveResult(file.getOriginalFile()));
        }

        Collections.sort(files, new Comparator<ResolveResult>() {
            @Override
            public int compare(ResolveResult o1, ResolveResult o2) {
                PsiFile element1 = (PsiFile) o1.getElement();
                PsiFile element2 = (PsiFile) o2.getElement();
                if (element1 == null || element2 == null) {
                    return 0;
                }

                return element1.getName().compareTo(element2.getName());
            }
        });
        return files.toArray(new ResolveResult[files.size()]);
    }
}
