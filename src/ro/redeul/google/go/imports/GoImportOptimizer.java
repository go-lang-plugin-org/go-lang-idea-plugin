package ro.redeul.google.go.imports;

import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.ide.GoProjectSettings;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.services.GoCodeManager;

import java.util.Set;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/15/11
 * Time: 7:43 AM
 */
public class GoImportOptimizer implements ImportOptimizer {

    private static final Logger LOG = Logger.getInstance("#ro.redeul.google.go.imports.GoImportOptimizer");

    @Override
    public boolean supports(PsiFile file) {
        return file instanceof GoFile;
    }

    @NotNull
    @Override
    public Runnable processFile(PsiFile file) {
        if (!(file instanceof GoFile)) {
            return EmptyRunnable.getInstance();
        }

        final GoFile goFile = (GoFile) file;

        //go the enableOptimizeImports declaration and change it to true if you want to see it working.
        if ( ! GoProjectSettings.getInstance(goFile.getProject()).getState().enableOptimizeImports ) {
            return EmptyRunnable.getInstance();
        }

        return new Runnable() {
            @Override
            public void run() {
                Project project = goFile.getProject();

                PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
                Document document = manager.getDocument(goFile);
                if (document != null) {
                    manager.commitDocument(document);
                }

                GoCodeManager goCodeManager = GoCodeManager.getInstance(project);

                try {
                    Set<GoImportDeclaration> usedImports = goCodeManager.findUsedImports(goFile);

                    GoImportDeclarations[] importDeclarations = goFile.getImportDeclarations();

                    for (GoImportDeclarations importDeclaration : importDeclarations) {
                        GoImportDeclaration[] importSpecs = importDeclaration.getDeclarations();

                        for (GoImportDeclaration importSpec : importSpecs) {
                            if (!usedImports.contains(importSpec)) {
                                // get the start of the import spec line
                                PsiElement start = importSpec;
                                IElementType actualToken =  start.getNode().getElementType();
                                while(!(actualToken.equals(GoTokenTypes.wsNLS) || actualToken.equals(GoTokenTypes.pLPAREN) ) && start.getPrevSibling() != null){

                                    start = start.getPrevSibling();
                                    actualToken =  start.getNode().getElementType();
                                }
                                // go forward to after the import
                                start = start.getNextSibling();

                                PsiElement end = importSpec;
                                actualToken =  end.getNode().getElementType();
                                while(!actualToken.equals(GoTokenTypes.wsNLS) && end.getNextSibling() != null){
                                    end = end.getNextSibling();
                                    actualToken =  end.getNode().getElementType();
                                }
                                // go back to before the new line
                                //end = end.getPrevSibling();
                                importDeclaration.deleteChildRange(start, end);
                            }

                        }

                        if (importDeclaration.getDeclarations().length == 0) {
                            goFile.deleteChildRange(importDeclaration, importDeclaration);
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Exception while optimizing go imports", ex);
                }
            }
        };
    }
}
