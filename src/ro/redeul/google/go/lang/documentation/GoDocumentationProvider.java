/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.documentation;

import java.util.List;

import com.intellij.lang.documentation.CodeDocumentationProvider;
import com.intellij.lang.documentation.ExternalDocumentationProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;

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
        // TODO: create a proper implementation:
        // - skip the white space siblings
        // - concatenate all the nodes with mSL_COMMENT (after you remove the initial // entries)
        //   or
        // - get the internal test from the mML_COMMENT (after cleaning that up properly)
        // - return the code
        return element.getPrevSibling().getPrevSibling().getText().replaceAll("\\/", "");
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
