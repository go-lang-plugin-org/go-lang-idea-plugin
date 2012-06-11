package ro.redeul.google.go.ide.structureview;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.PlatformIcons;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeInterface;

import javax.swing.Icon;

/**
 * User: jhonny
 * Date: 06/07/11
 */
public class GoStructureViewElement implements StructureViewTreeElement {

    private PsiElement element;
    private TreeElement[] children;

    public GoStructureViewElement(PsiElement element) {
        this(element, new TreeElement[0]);
    }

    public GoStructureViewElement(PsiElement element, TreeElement[] children) {
        this.element = element;
        this.children = children;
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
                    return PlatformIcons.METHOD_ICON;
                if (element instanceof GoFunctionDeclaration)
                    return PlatformIcons.FUNCTION_ICON;
                if (element instanceof GoTypeSpec) {
                    GoType type = ((GoTypeSpec) element).getType();
                    if (type instanceof GoTypeInterface)
                        return PlatformIcons.INTERFACE_ICON;
                    return PlatformIcons.CLASS_ICON;
                }

                if (element instanceof GoLiteralIdentifier) {
                    return PlatformIcons.FIELD_ICON;
                }

                return PlatformIcons.EXCLUDED_FROM_COMPILE_ICON;
            }
        };
    }

    @Override
    public TreeElement[] getChildren() {
        return children;
    }
}
