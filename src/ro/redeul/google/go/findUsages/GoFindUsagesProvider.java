package ro.redeul.google.go.findUsages;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoLexer;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 7:53:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoFindUsagesProvider implements FindUsagesProvider {

    public static final GoFindUsagesProvider INSTANCE = new GoFindUsagesProvider();

    public GoFindUsagesProvider() {
    }

    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new GoLexer(), TokenSet.create(GoTokenTypes.mIDENT), GoTokenTypeSets.COMMENTS, TokenSet.create());
    }

    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof GoTypeNameDeclaration ||
                psiElement instanceof GoLiteralIdentifier;
    }

    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    public String getType(@NotNull PsiElement element) {
        if (element instanceof GoTypeNameDeclaration) return "type";
        if (element instanceof GoLiteralIdentifier) return "variable";
//        if (element instanceof PsiMethod) return "method";
//        if (element instanceof PsiField) return "field";
//        if (element instanceof PsiParameter) return "parameter";
//        if (element instanceof PsiVariable || element instanceof GrReferenceExpression) return "variable";
//        if (element instanceof GrLabeledStatement) return "label";
        return "";
    }

    @NotNull
    public String getDescriptiveName(@NotNull PsiElement element) {
        if ( element instanceof PsiNamedElement ) {
            String elementName = ((PsiNamedElement)element).getName();
            if (elementName == null) {
                return element.toString();
            }
            return elementName;
        }
        return element.toString();
    }

    @NotNull
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return element.toString();
    }
}
