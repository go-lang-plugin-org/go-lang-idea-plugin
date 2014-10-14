package ro.redeul.google.go.lang;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiNamedElement;

public class PresentationUtil {

    public static LookupElementBuilder elementToLookupElementBuilder(PsiNamedElement element) {
        return LookupElementBuilder.create(element);
    }
}
