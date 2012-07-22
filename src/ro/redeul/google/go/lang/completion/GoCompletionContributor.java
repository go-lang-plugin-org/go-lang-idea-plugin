package ro.redeul.google.go.lang.completion;

import java.util.Collection;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.completion.insertHandler.ConstInsertHandler;
import ro.redeul.google.go.lang.completion.insertHandler.CurlyBracesInsertHandler;
import ro.redeul.google.go.lang.completion.insertHandler.ImportInsertHandler;
import ro.redeul.google.go.lang.completion.insertHandler.LiteralFunctionInsertHandler;
import ro.redeul.google.go.lang.completion.insertHandler.ReturnInsertHandler;
import ro.redeul.google.go.lang.completion.insertHandler.VarInsertHandler;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoDeferStatement;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.statements.GoGoStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
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
                result.addElement(
                    keywordLookup("const", new ConstInsertHandler()));
                result.addElement(
                    keywordLookup("var", new VarInsertHandler()));
                result.addElement(
                    keywordLookup("return", new ReturnInsertHandler()));
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
                result.addElement(
                    keywordLookup("const", new ConstInsertHandler()));
                result.addElement(
                    keywordLookup("var", new VarInsertHandler()));
                result.addElement(
                    keywordLookup("func"));
                result.addElement(
                    keywordLookup("type"));
                result.addElement(
                    keywordLookup("import", new ImportInsertHandler()));
            }
        };

    CompletionProvider<CompletionParameters> importPathCompletionProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters params,
                                          ProcessingContext context,
                                          @NotNull CompletionResultSet result) {

                Project project = params.getOriginalFile().getProject();

                GoNamesCache packageNamesCache =
                    GoNamesCache.getInstance(project);
                Collection<String> goSdkPackages = packageNamesCache.getSdkPackages();

                for (String goPackage : goSdkPackages) {
                    result.addElement(
                        LookupElementBuilder.create("\"" + goPackage + "\"")
                                            .setIcon(PlatformIcons.PACKAGE_ICON)
                                            .setTypeText("via sdk"));
                }

                Collection<String> goProjectPackages = packageNamesCache.getProjectPackages();

                for (String goPackage : goProjectPackages) {
                    result.addElement(
                        LookupElementBuilder.create("\"" + goPackage + "\"")
                                            .setIcon(PlatformIcons.PACKAGE_ICON)
                                            .setBold()
                                            .setTypeText("via project"));
                }
            }
        };

    CompletionProvider<CompletionParameters> goAndDeferStatementCompletionProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters params,
                                          ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                result.addElement(
                    keywordLookup("func", new LiteralFunctionInsertHandler()));
            }
        };

    CompletionProvider<CompletionParameters> typeDeclarationCompletionProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters params,
                                          ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                result.addElement(
                    keywordLookup("interface", new CurlyBracesInsertHandler()));
                result.addElement(
                    keywordLookup("struct", new CurlyBracesInsertHandler()));

                for (GoTypes.Builtin builtin : GoTypes.Builtin.values()) {
                    result.addElement(
                        keywordLookup(builtin.name().toLowerCase())
                    );
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

        extend(CompletionType.BASIC,
               psiElement()
                   .withParent(
                       psiElement(GoPackageDeclaration.class)
                           .withFirstNonWhitespaceChild(
                               psiElement(PsiErrorElement.class)
                           )
                   ),
               packageCompletionProvider);

        extend(CompletionType.BASIC,
               psiElement(GoTokenTypes.litSTRING)
                   .withParent(
                       psiElement(GoLiteralString.class)
                           .withParent(GoImportDeclaration.class)),
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

        extend(CompletionType.BASIC,
               psiElement().withParent(
                   psiElement(GoLiteralIdentifier.class).withParent(
                       psiElement(GoLiteralExpression.class).withParent(
                           psiElement(GoGoStatement.class)
                       )
                   )
               ),
               goAndDeferStatementCompletionProvider);

        extend(CompletionType.BASIC,
               psiElement().withParent(
                   psiElement(GoLiteralIdentifier.class).withParent(
                       psiElement(GoLiteralExpression.class).withParent(
                           psiElement(GoDeferStatement.class)
                       )
                   )
               ),
               goAndDeferStatementCompletionProvider);

        extend(CompletionType.BASIC,
               psiElement().withParent(
                   psiElement(GoLiteralIdentifier.class).withParent(
                       psiElement(GoPsiTypeName.class)
                   )
               ),
               typeDeclarationCompletionProvider);
    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        int a = context.getIdentifierEndOffset();
        super.beforeCompletion(context);
        int b = context.getIdentifierEndOffset();
        int c = 10;
    }
}
