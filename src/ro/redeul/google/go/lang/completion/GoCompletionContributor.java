package ro.redeul.google.go.lang.completion;

import java.util.Collection;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.completion.insertHandler.BlockWithCursorBeforeInsertHandler;
import ro.redeul.google.go.lang.completion.insertHandler.ConstInsertHandler;
import ro.redeul.google.go.lang.completion.insertHandler.CurlyBracesInsertHandler;
import ro.redeul.google.go.lang.completion.insertHandler.IfInsertHandler;
import ro.redeul.google.go.lang.completion.insertHandler.ImportInsertHandler;
import ro.redeul.google.go.lang.completion.insertHandler.InlineCurlyBracesInsertHandler;
import ro.redeul.google.go.lang.completion.insertHandler.KeywordInsertionHandler;
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
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;
import static ro.redeul.google.go.lang.completion.GoCompletionUtil.getImportedPackagesNames;
import static ro.redeul.google.go.lang.completion.GoCompletionUtil.keyword;
import static ro.redeul.google.go.lang.completion.GoCompletionUtil.packageElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 9:09:20 PM
 */
public class GoCompletionContributor extends CompletionContributor {

    public static final String DUMMY_IDENTIFIER = CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED;

    public static final PsiElementPattern.Capture<PsiElement> TYPE_DECLARATION =
            psiElement().withParent(
                    psiElement(GoLiteralIdentifier.class).withParent(
                            psiElement(GoPsiTypeName.class)
                    )
            );

    public static final ElementPattern<? extends PsiElement> BLOCK_STATEMENT =
            psiElement().withParent(
                    psiElement(GoLiteralIdentifier.class).withParent(
                            psiElement(GoLiteralExpression.class).withParent(
                                    psiElement(GoExpressionStatement.class).withParent(
                                            GoBlockStatement.class
                                    )
                            )
                    )
            );

    CompletionProvider<CompletionParameters> packageCompletionProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(
                @NotNull CompletionParameters parameters,
                ProcessingContext context,
                @NotNull CompletionResultSet result) {
                result.addElement(AutoCompletionPolicy.ALWAYS_AUTOCOMPLETE.applyPolicy(keyword("package")));
            }
        };

    CompletionProvider<CompletionParameters> blockStatementsCompletionProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(
                @NotNull CompletionParameters parameters,
                ProcessingContext context,
                @NotNull CompletionResultSet result) {
                result.addElement(
                    keyword("for", new BlockWithCursorBeforeInsertHandler()));
                result.addElement(
                    keyword("const", new ConstInsertHandler()));
                result.addElement(
                    keyword("var", new VarInsertHandler()));
                result.addElement(
                    keyword("return", new ReturnInsertHandler()));
                result.addElement(
                    keyword("if", new IfInsertHandler()));
                result.addElement(
                    keyword("switch", new BlockWithCursorBeforeInsertHandler()));
                result.addElement(keyword("go"));
                result.addElement(
                    keyword("select", new CurlyBracesInsertHandler()));
                result.addElement(
                    keyword("defer"));

                addPackageAutoCompletion(parameters, result);
            }
        };

    CompletionProvider<CompletionParameters> topLevelKeywordsProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(
                @NotNull CompletionParameters parameters,
                ProcessingContext context,
                @NotNull CompletionResultSet result) {
                result.addElement(keyword("const", new ConstInsertHandler()));
                result.addElement(keyword("var", new VarInsertHandler()));
                result.addElement(keyword("func"));
                result.addElement(keyword("type"));
                result.addElement(keyword("import", new ImportInsertHandler()));
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
                    keyword("func", new LiteralFunctionInsertHandler()));
            }
        };

    CompletionProvider<CompletionParameters> typeDeclarationCompletionProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters params,
                                          ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                result.addElement(
                    keyword("interface", createInterfaceInsertionHandler(params)));
                result.addElement(
                    keyword("struct", new CurlyBracesInsertHandler()));

                for (GoTypes.Builtin builtin : GoTypes.Builtin.values()) {
                    result.addElement(keyword(builtin.name().toLowerCase(), null));
                }

                addPackageAutoCompletion(params, result);
            }

            private KeywordInsertionHandler createInterfaceInsertionHandler(CompletionParameters params) {
                if (isTypeNameInDeclaration(params.getPosition())) {
                    return new CurlyBracesInsertHandler();
                } else {
                    return new InlineCurlyBracesInsertHandler();
                }
            }
        };

    CompletionProvider<CompletionParameters> localImportsCompletion =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                PsiFile originalFile = parameters.getOriginalFile();
                if (!(originalFile instanceof GoFile))
                    return;

                GoFile file = (GoFile) originalFile;

                for (GoImportDeclarations imports : file.getImportDeclarations()) {
                    for (GoImportDeclaration importDecl : imports.getDeclarations()) {
                        result.addElement(LookupElementBuilder.create(
                            importDecl.getVisiblePackageName() + "."));
                    }
                }
            }
        };

    CompletionProvider<CompletionParameters> debuggingCompletionProvider =
        new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                String originalFile =
                    DebugUtil.psiToString(parameters.getOriginalFile(), false);
                String currentFile =
                    DebugUtil.psiToString(
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
                       psiElement(PsiErrorElement.class)
                           .withParent(
                               psiElement(GoPackageDeclaration.class)
                                   .withFirstNonWhitespaceChild(
                                       psiElement(PsiErrorElement.class)
                                   )
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
               BLOCK_STATEMENT,
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
                           or(
                               psiElement(GoDeferStatement.class),
                               psiElement(GoGoStatement.class)
                           )
                       )
                   )
               ),
               goAndDeferStatementCompletionProvider);

        extend(CompletionType.BASIC,
               TYPE_DECLARATION,
               typeDeclarationCompletionProvider);
    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        context.setDummyIdentifier(DUMMY_IDENTIFIER);
    }

    public static void addPackageAutoCompletion(CompletionParameters parameters, CompletionResultSet result) {
        PsiFile originalFile = parameters.getOriginalFile();
        for (LookupElement element : getImportedPackagesNames(originalFile)) {
            result.addElement(element);
        }

        // For second basic completion, add all package names to auto completion list.
        if (parameters.getCompletionType() == CompletionType.BASIC &&
                parameters.getInvocationCount() > 1) {
            addAllPackageNames(result, originalFile.getProject());
        }
    }

    public static void addAllPackageNames(CompletionResultSet result, Project project) {
        for (String pkg : GoNamesCache.getInstance(project).getAllPackages()) {
            if (pkg.contains("/")) {
                pkg = pkg.substring(pkg.lastIndexOf('/') + 1);
            }
            result.addElement(packageElement(pkg));
        }
    }

    private static boolean isTypeNameInDeclaration(PsiElement element) {
        GoPsiTypeName typeName = GoPsiUtils.findParentOfType(element, GoPsiTypeName.class);
        return typeName != null && typeName.getParent() instanceof GoTypeSpec;

    }
}
