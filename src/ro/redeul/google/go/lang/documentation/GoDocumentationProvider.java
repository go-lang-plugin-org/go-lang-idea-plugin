package ro.redeul.google.go.lang.documentation;

import com.intellij.lang.documentation.CodeDocumentationProvider;
import com.intellij.lang.documentation.ExternalDocumentationProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

import java.util.List;

import static ro.redeul.google.go.lang.documentation.DocumentUtil.*;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

/**
 * Documentation provider for Go.
 *
 * @author Mihai Claudiu Toader <mtoader@google.com>
 *         Date: 4/15/12
 */
public class GoDocumentationProvider implements CodeDocumentationProvider,
                                                ExternalDocumentationProvider {
    @Override
    public PsiComment findExistingDocComment(PsiComment contextElement) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String generateDocumentationContentStub(PsiComment contextComment) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getQuickNavigateInfo(PsiElement element,
                                       PsiElement originalElement) {
        if (element instanceof GoFunctionDeclaration) {
            return getFunctionQuickNavigationInfo((GoFunctionDeclaration) element);
        }

        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element,
                                  PsiElement originalElement) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (!(element.getContainingFile() instanceof GoFile)) {
            return "";
        }

        GoLiteralIdentifier identifier = null;
        if (element instanceof GoLiteralIdentifier) {
            identifier = (GoLiteralIdentifier) element;
            element = element.getParent();
        }

        if (element instanceof GoSelectorExpression) {
            element = resolveSafely(element, PsiElement.class);
            if (element instanceof GoLiteralIdentifier) {
                identifier = (GoLiteralIdentifier) element;
                element = element.getParent();
            }
        }


        if (element instanceof GoTypeStructAnonymousField) {
            return getTailingOrHeaderDocument(element);
        } else if (element instanceof GoTypeStructField) {
            return getTailingOrHeaderDocument(element);
        } else if (element instanceof GoTypeSpec) {
            return getTypeDocument(((GoTypeSpec) element).getTypeNameDeclaration());
        } else if (element instanceof GoTypeNameDeclaration) {
            return getTypeDocument((GoTypeNameDeclaration) element);
        } else if (element instanceof GoMethodDeclaration) {
            return getMethodDocument((GoMethodDeclaration) element);
        } else if (element instanceof GoFunctionDeclaration) {
            return getFunctionDocument((GoFunctionDeclaration) element);
        } else if (element instanceof GoConstDeclaration) {
            return getConstDocument(identifier);
        } else if (element instanceof GoVarDeclaration) {
            return getVarDocument(identifier);
        }
        return "";
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(
        PsiManager psiManager, Object object, PsiElement element) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager,
                                                     String link,
                                                     PsiElement context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String fetchExternalDocumentation(Project project,
                                             PsiElement element,
                                             List<String> docUrls) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasDocumentationFor(PsiElement element,
                                       PsiElement originalElement) {
        //To change body of implemented methods use File | Settings | File Templates.
        return element instanceof GoFunctionDeclaration || element instanceof GoPsiTypeName;
    }

    @Override
    public boolean canPromptToConfigureDocumentation(PsiElement element) {
        return false;
    }

    @Override
    public void promptToConfigureDocumentation(PsiElement element) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nullable
    public Pair<PsiElement, PsiComment> parseContext(@NotNull PsiElement startPoint) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
