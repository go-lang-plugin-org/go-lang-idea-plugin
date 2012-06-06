package ro.redeul.google.go.lang.psi;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ro.redeul.google.go.GoFileType;
import com.intellij.psi.PsiElement;

class AbstractGoPsiTestCase extends LightCodeInsightFixtureTestCase {

    protected GoFile parse(String fileText) {
        return (GoFile) myFixture.configureByText(GoFileType.INSTANCE, fileText);
    }

    protected  <Psi extends PsiElement> Psi get(Psi[] array, int i) {
        assertTrue(array != null);
        assertTrue(array.length > i);
        return array[i];
    }

    protected  <B extends PsiElement, D extends B> D getAs(B[] array, int i, Class<D> type) {
        assertTrue(array != null);
        assertTrue(array.length > i);
        assertNotNull(type.cast(array[i]));

        return type.cast(array[i]);
    }

    protected  <Psi extends PsiElement> Psi get(Psi node) {
        assertNotNull(node);
        return node;
    }

    protected  <B extends PsiElement, D extends B> D getAs(B node, Class<D> type) {
        assertNotNull(node);
        assertNotNull(type.cast(node));

        return type.cast(node);
    }

}
