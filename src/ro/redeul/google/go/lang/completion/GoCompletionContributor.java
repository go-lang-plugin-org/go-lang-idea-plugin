package ro.redeul.google.go.lang.completion;

import java.util.Collection;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.GoExpressionTypeResolver;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.lang.completion.GoCompletionUtil.keywordLookup;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 9:09:20 PM
 */
public class GoCompletionContributor extends CompletionContributor {


    CompletionProvider<CompletionParameters> packageCompletionProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(
                @NotNull CompletionParameters parameters,
                ProcessingContext context,
                @NotNull CompletionResultSet result) {
                result.addElement(keywordLookup("package"));
            }
        };

    CompletionProvider<CompletionParameters> blockStatementsCompletionProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(
                @NotNull CompletionParameters parameters,
                ProcessingContext context,
                @NotNull CompletionResultSet result) {
                result.addElement(keywordLookup("for"));
                result.addElement(keywordLookup("var"));
                result.addElement(keywordLookup("return"));
                result.addElement(keywordLookup("if"));
                result.addElement(keywordLookup("switch"));
                result.addElement(keywordLookup("go"));
                result.addElement(keywordLookup("select"));
                result.addElement(keywordLookup("defer"));
            }
        };

    CompletionProvider<CompletionParameters> topLevelKeywordsProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(
                @NotNull CompletionParameters parameters,
                ProcessingContext context,
                @NotNull CompletionResultSet result) {
                result.addElement(keywordLookup("const"));
                result.addElement(keywordLookup("var"));
                result.addElement(keywordLookup("func"));
                result.addElement(keywordLookup("import"));
            }
        };

    CompletionProvider<CompletionParameters> importPathCompletionProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(
                @NotNull CompletionParameters parameters,
                ProcessingContext context,
                @NotNull CompletionResultSet result) {

                Project project = parameters.getOriginalFile().getProject();

                GoNamesCache packageNamesCache = ContainerUtil.findInstance(
                    project.getExtensions(PsiShortNamesCache.EP_NAME),
                    GoNamesCache.class);

                if (packageNamesCache != null) {
                    Collection<String> goSdkPackages = packageNamesCache.getSdkPackages();

                    for (String goPackage : goSdkPackages) {
                        result.addElement(LookupElementBuilder.create(goPackage)
                                                              .setTypeText(
                                                                  "via sdk"));
                    }

                    Collection<String> goProjectPackages = packageNamesCache.getProjectPackages();

                    for (String goPackage : goProjectPackages) {
                        result.addElement(LookupElementBuilder.create(goPackage)
                                                              .setBold()
                                                              .setTypeText(
                                                                  "via project"));
                    }
                }
            }
        };

    CompletionProvider<CompletionParameters> typeNameCompletionProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(
                @NotNull CompletionParameters parameters,
                ProcessingContext context,
                @NotNull CompletionResultSet result) {

                PsiElement element = parameters.getOriginalPosition();
                if (element == null) {
                    return;
                }

                LookupElement elements[];
//            if (currentPath.startsWith("\"./")) {
//                elements = GoCompletionUtil.resolveLocalPackagesForPath(element.getProject(), element.getContainingFile(), currentPath);
//            } else {
//                elements = GoCompletionUtil.resolveSdkPackagesForPath(element.getProject(), element.getContainingFile(), currentPath);
//            }

                elements = GoCompletionUtil.getImportedPackagesNames(
                    element.getContainingFile());
                for (LookupElement lookupElement : elements) {
                    result.addElement(lookupElement);
                }

            }
        };

    CompletionProvider<CompletionParameters> packageMethodCompletionProvider = new CompletionProvider<CompletionParameters>() {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      ProcessingContext context,
                                      @NotNull CompletionResultSet result) {

            PsiElement node = parameters.getOriginalPosition();
            if (node == null || node.getParent() == null || !(node.getParent() instanceof GoSelectorExpression))
                return;

            GoSelectorExpression expression = (GoSelectorExpression) node.getParent();

            if (expression.getExpressionContext() == null)
                return;

            GoExpr expressionContext = expression.getExpressionContext();

            GoExpressionTypeResolver expressionTypeResolver = new GoExpressionTypeResolver(
                expressionContext);

            PsiScopesUtil.treeWalkUp(expressionTypeResolver, expressionContext,
                                     expressionContext.getContainingFile());

            for (PsiNamedElement psiElement : expressionTypeResolver.getFunctions()) {
                result.addElement(LookupElementBuilder.create(psiElement));
            }
        }
    };

    CompletionProvider<CompletionParameters> debuggingCompletionProvider = new CompletionProvider<CompletionParameters>() {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      ProcessingContext context,
                                      @NotNull CompletionResultSet result) {
            String originalFile = DebugUtil.psiToString(
                parameters.getOriginalFile(), false);
            String currentFile = DebugUtil.psiToString(
                parameters.getPosition().getContainingFile(), false);

            System.out.println(currentFile);
            int a = 10;
        }
    };

    public GoCompletionContributor() {

//        extend(CompletionType.BASIC,
//               psiElement(),
//               debuggingCompletionProvider);

        extend(
            CompletionType.BASIC,
            psiElement().withParent(
                psiElement(
                    GoPackageDeclaration.class).withFirstNonWhitespaceChild(
                    psiElement(PsiErrorElement.class)
                )
            ),
            packageCompletionProvider);

        extend(CompletionType.BASIC,
               psiElement(GoTokenTypes.litSTRING).withParent(
                   GoImportDeclaration.class
               ),
               importPathCompletionProvider);

        extend(CompletionType.BASIC,
               psiElement().withParent(
                   psiElement(GoLiteralIdentifier.class).withParent(
                       psiElement(GoLiteralExpression.class).withParent(
                           psiElement(GoExpressionStatement.class).withParent(
                               GoBlockStatement.class
                           )
                       )
                   )
               ),
               blockStatementsCompletionProvider);

        extend(CompletionType.BASIC,
               psiElement().withParent(
                   psiElement(PsiErrorElement.class).withParent(
                       psiElement(GoFile.class).withChild(
                           psiElement(GoPackageDeclaration.class)
                       )
                   )
               ),
               topLevelKeywordsProvider);
    }

    @Override
    public void beforeCompletion(
        @NotNull CompletionInitializationContext context) {
//        if (context.getCompletionType() != CompletionType.SMART) return;
//    PsiElement lastElement = context.getFile().findElementAt(context.getStartOffset() - 1);
//    if (lastElement != null && lastElement.getText().equals("(")) {
//      final PsiElement parent = lastElement.getParent();
//      if (parent instanceof GrTypeCastExpression) {
//        context.setFileCopyPatcher(new FileCopyPatcher() {
//            @Override
//            public void patchFileCopy(@NotNull PsiFile fileCopy, @NotNull Document document, @NotNull OffsetMap map) {
//
//            }
//        });
//      }
//      else if (parent instanceof GrParenthesizedExpression) {
//        context.setFileCopyPatcher(new DummyIdentifierPatcher("xxx)yyy ")); // to handle type cast
//      }
//    }
    }
}
