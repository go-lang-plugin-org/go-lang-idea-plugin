package ro.redeul.google.go.lang.documentation;

import java.util.List;

import com.intellij.lang.documentation.CodeDocumentationProvider;
import com.intellij.lang.documentation.ExternalDocumentationProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;

import static ro.redeul.google.go.lang.documentation.DocumentUtil.getConstDocument;
import static ro.redeul.google.go.lang.documentation.DocumentUtil.getFunctionDocument;
import static ro.redeul.google.go.lang.documentation.DocumentUtil.getMethodDocument;
import static ro.redeul.google.go.lang.documentation.DocumentUtil.getTypeDocument;
import static ro.redeul.google.go.lang.documentation.DocumentUtil.getVarDocument;

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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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

        if (element instanceof GoTypeNameDeclaration) {
            return getTypeDocument((GoTypeNameDeclaration) element);
        } else if (element instanceof GoMethodDeclaration) {
            return getMethodDocument((GoMethodDeclaration) element);
        } else if (element instanceof GoFunctionDeclaration) {
            return getFunctionDocument((GoFunctionDeclaration) element);
        } else if (element instanceof GoLiteralIdentifier) {
            PsiElement parent = element.getParent();
            if (((GoLiteralIdentifier) element).isBlank()) {
                return "";
            }

            if (parent instanceof GoConstDeclaration) {
                return getConstDocument((GoLiteralIdentifier) element);
            } else if (parent instanceof GoVarDeclaration) {
                return getVarDocument((GoLiteralIdentifier) element);
            }
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
        if ( element instanceof GoFunctionDeclaration || element instanceof GoMethodDeclaration ) {
            return true;
        }

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canPromptToConfigureDocumentation(PsiElement element) {
        return true;
    }

    @Override
    public void promptToConfigureDocumentation(PsiElement element) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
