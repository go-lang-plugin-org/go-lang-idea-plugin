package ro.redeul.google.go.ide.structureview;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

import java.util.ArrayList;

/**
 * User: jhonny
 * Date: 06/07/11
 */
public class GoStructureViewModel extends TextEditorBasedStructureViewModel implements StructureViewModel {

    public GoStructureViewModel(@NotNull PsiFile psiFile) {
        super(psiFile);
    }

    private StructureViewTreeElement generateTypeStructureTree(GoTypeSpec typeDec) {

        ArrayList<TreeElement> children = new ArrayList<TreeElement>();
        for (GoPsiElement psi : typeDec.getType().getMembers()) {
            children.add(new GoStructureViewElement(psi));
        }

        return new GoStructureViewElement(typeDec, children.toArray(new TreeElement[children.size()]));
    }

    private StructureViewTreeElement generateFileStructureTree() {
        GoFile psiFile = (GoFile) this.getPsiFile();
        GoFunctionDeclaration[] functionDeclarations = psiFile.getFunctions();
        GoTypeDeclaration[] typeDeclarations = psiFile.getTypeDeclarations();

        ArrayList<TreeElement> children = new ArrayList<TreeElement>();
        // add just the typeNameDec to be listed at root
        for (GoTypeDeclaration typeDec : typeDeclarations) {
            for (GoTypeSpec ts : typeDec.getTypeSpecs()) {
                children.add(generateTypeStructureTree(ts));
            }
        }

        for (GoFunctionDeclaration fd : functionDeclarations) {
            children.add(new GoStructureViewElement(fd));
        }

        return new GoStructureViewElement(psiFile, children.toArray(new TreeElement[children.size()]));
    }

    @NotNull
    @Override
    public StructureViewTreeElement getRoot() {
        return generateFileStructureTree();
    }
}
