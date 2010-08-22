package ro.redeul.google.go.lang.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrParenthesizedExpression;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrTypeCastExpression;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoPackageDeclarationImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDefinition;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 9:09:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoCompletionContributor extends CompletionContributor {

    public GoCompletionContributor() {
        extend(
                CompletionType.BASIC,
                // psiElement().withParent(psiElement(PsiErrorElement.class).withParent(GoPackageDefinition.class)),
                psiElement().withParent(psiElement(PsiErrorElement.class).withParent(GoFile.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {

//                        final PsiElement position = parameters.getPosition();
//                        final PsiElement reference = position.getParent();
//
//                        if (reference == null || ! (reference instanceof GoFile) )
//                            return;
//
//                        GoFile file = (GoFile) reference;
//
//                        if ( file.getPackage() == null || !file.getPackage().getText().contains("package") ) {
                        result.addElement(LookupElementBuilder.create("package"));
//                        }
                    }
                });

//        extend(
//                CompletionType.BASIC,
//                psiElement().withParent(GoFile.class),
//                new CompletionProvider<CompletionParameters>() {
//                    @Override
//                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
//
//                        final PsiElement position = parameters.getPosition();
//                        final PsiElement reference = position.getParent();
//
//                        if (reference == null) return;
//
//                        if (reference.getParent() instanceof GoFile) {
//                            result.addElement(LookupElementBuilder.create("package"));
//                        }
//                    }
//                });
    }

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
//        if (context.getCompletionType() != CompletionType.SMART) return;
//    PsiElement lastElement = context.getFile().findElementAt(context.getStartOffset() - 1);
//    if (lastElement != null && lastElement.getText().equals("(")) {
//      final PsiElement parent = lastElement.getParent();
//      if (parent instanceof GrTypeCastExpression) {
        // context.setFileCopyPatcher(new DummyIdentifierPatcher(""));
//      }
//      else if (parent instanceof GrParenthesizedExpression) {
//        context.setFileCopyPatcher(new DummyIdentifierPatcher("xxx)yyy ")); // to handle type cast
//      }
//    }
    }
}
