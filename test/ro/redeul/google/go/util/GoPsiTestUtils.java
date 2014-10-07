package ro.redeul.google.go.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import static com.intellij.testFramework.UsefulTestCase.assertInstanceOf;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class GoPsiTestUtils {

    public static <Psi extends PsiElement> Psi childAt(int i, Psi[] array) {
        assertTrue(array != null);
        assertTrue(array.length > i);
        return array[i];
    }

    public static <B extends PsiElement, D extends B> D castAs(Class<D> type,
                                                               int i, B[] array) {
        assertTrue(array != null);
        assertTrue(array.length > i);
        assertNotNull(type.cast(array[i]));

        return type.cast(array[i]);
    }

    public static <Psi extends PsiElement> Psi get(Psi node) {
        assertNotNull(node);
        return node;
    }

    public static <B extends PsiElement, D extends B> D getAs(
        Class<D> type, B node) {
        assertNotNull(node);
        assertNotNull(type.cast(node));

        return type.cast(node);
    }

    public static <T> T assertAs(Class<T> type, PsiElement element) {
        assertInstanceOf(element, type);
        return type.cast(element);
    }

    public static <T> T assertParentType(Class<T> type, PsiElement node) {
        assertNotNull(node);
        assertNotNull(node.getParent());

        return assertAs(type, node.getParent());
    }

    public static PsiElement assertParentType(IElementType type,
                                              PsiElement node) {
        assertNotNull(node);

        PsiElement parent = node.getParent();
        assertNotNull(parent);

        assertEquals(type, parent.getNode().getElementType());
        return parent;
    }
}
