package ro.redeul.google.go.ide.structureview;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.Icons;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: jhonny
 * Date: 06/07/11
 */
public class GoStructureViewElement implements StructureViewTreeElement {

    private boolean isRoot;
    private PsiElement element;

    public GoStructureViewElement(PsiElement element, boolean isRoot) {
        this.isRoot = isRoot;
        this.element = element;
    }

    @Override
    public PsiElement getValue() {
        return element;
    }

    @Override
    public void navigate(boolean b) {
        if (element instanceof NavigationItem)
            ((NavigationItem) element).navigate(b);
    }

    @Override
    public boolean canNavigate() {
        return element instanceof NavigationItem;
    }

    @Override
    public boolean canNavigateToSource() {
        return element instanceof NavigationItem;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            public String getPresentableText() {

                if (element instanceof PsiNamedElement)
                    return ((PsiNamedElement) element).getName();

                return "Unknown element - " + element.getText();
            }

            public TextAttributesKey getTextAttributesKey() {
                return null;
            }

            public String getLocationString() {
                return null;
            }

            public Icon getIcon(boolean open) {
                if (element instanceof GoMethodDeclaration)
                    return Icons.METHOD_ICON;
                if (element instanceof GoFunctionDeclaration)
                    return Icons.FUNCTION_ICON;
                if (element instanceof GoTypeNameDeclaration)
                    return Icons.CLASS_ICON;
                return Icons.EXCLUDED_FROM_COMPILE_ICON;
            }
        };
    }

    @Override
    public TreeElement[] getChildren() {
        TreeElement[] children;
        if (isRoot) {
            GoFile psiFile = (GoFile) element;
            GoFunctionDeclaration[] functionDeclarations = psiFile.getFunctions();
            GoMethodDeclaration[] methodDeclarations = psiFile.getMethods();
            GoTypeDeclaration[] typeDeclarations = psiFile.getTypeDeclarations();

            ArrayList<PsiElement> items = new ArrayList<PsiElement>();//Arrays.asList(array)
            // add just the typeNameDec to be listed at root
            for (GoTypeDeclaration typeDec : typeDeclarations) {
                for (GoTypeSpec spec : typeDec.getTypeSpecs()) {
                    items.add(spec.getTypeNameDeclaration());
                }
            }

            items.addAll(Arrays.asList(methodDeclarations));
            items.addAll(Arrays.asList(functionDeclarations));

            children = new TreeElement[items.size()];

            for (int i = 0; i < items.size(); i++) {
                children[i] = new GoStructureViewElement(items.get(i), false);
            }
        } else {
            children = new TreeElement[0];
        }
        return children;
    }
}
