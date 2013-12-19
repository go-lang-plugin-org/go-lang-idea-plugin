package ro.redeul.google.go.lang.completion;

import com.intellij.codeInsight.completion.*;
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
import ro.redeul.google.go.lang.completion.insertHandler.*;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.statements.*;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import java.util.*;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.not;
import static com.intellij.patterns.StandardPatterns.or;
import static ro.redeul.google.go.lang.completion.GoCompletionUtil.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 9:09:20 PM
 */
public class GoCompletionContributor extends CompletionContributor {

    public static final String DUMMY_IDENTIFIER = CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED;

    private static final  String [] BULTINS_WITH_RETURN = {
        "new", "make", "len", "cap", "append", "copy", "complex",
        "real", "imag", "recover"
    };

    private static final  String [] BULTINS_WITHOUT_RETURN = {
        "delete", "panic", "print", "println"
    };

    // Check whether a PsiElement is a valid position for a type name.
    @SuppressWarnings("unchecked")
    private static final PsiElementPattern.Capture<PsiElement> TYPE_DECLARATION = psiElement().withParent(
            psiElement(GoLiteralIdentifier.class).withParent(
                    or(psiElement(GoPsiTypeName.class),             // where type name is expected
                            psiElement(GoLiteralExpression.class).withParent(
                                    or(psiElement(GoExpr.class),                            // in an expression
                                            psiElement(GoExpressionList.class).withParent(
                                                    // in method invocation parameter or assignment
                                                    or(psiElement(GoCallOrConvExpression.class),
                                                            psiElement(GoAssignmentStatement.class)
                                                    )
                                            ),
                                            // in return statement
                                            psiElement(GoReturnStatement.class),

                                            // in variable declaration statement
                                            psiElement(GoVarDeclaration.class)
                                    )
                            )
                    )
            )
    );

    private static final ElementPattern<? extends PsiElement> BLOCK_STATEMENT =
            psiElement().withParent(
                    psiElement(GoLiteralIdentifier.class).withParent(
                            psiElement(GoLiteralExpression.class).withParent(
                                    psiElement(GoExpressionStatement.class).withParent(
                                            GoBlockStatement.class
                                    )
                            )
                    )
            );

    // Check whether a PsiElement is a valid position for a qualified identifier (identifier with package name).
    @SuppressWarnings("unchecked")
    public static final PsiElementPattern.Capture<PsiElement> VALID_PACKAGE_NAME_POSITION = psiElement().withParent(
            psiElement(GoLiteralIdentifier.class).withParent(
                    or(psiElement(GoPsiTypeName.class),             // where type name is expected
                            psiElement(GoLiteralExpression.class).withParent(
                                    or(psiElement(GoExpr.class),                            // in an expression
                                            psiElement(GoExpressionList.class).withParent(
                                                    // in method invocation parameter or assignment
                                                    or(psiElement(GoCallOrConvExpression.class),
                                                            psiElement(GoAssignmentStatement.class)
                                                    )
                                            ),
                                            // in statements, like return, go, defer, etc
                                            psiElement(GoStatement.class)
                                    )
                            )
                    )
            )
    );

    @SuppressWarnings("unchecked")
    private static final PsiElementPattern.Capture<PsiElement> GO_OR_DEFER_STATEMENT = psiElement().withParent(
            psiElement(GoLiteralIdentifier.class).withParent(
                    psiElement(GoLiteralExpression.class).withParent(
                            or(
                                    psiElement(GoDeferStatement.class),
                                    psiElement(GoGoStatement.class)
                            )
                    )
            )
    );


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
                String currentFile =
                    DebugUtil.psiToString(
                        parameters.getPosition().getContainingFile(), false);

                System.out.println(currentFile);
            }
        };

    @SuppressWarnings("unchecked")
    public GoCompletionContributor() {

//        extend(CompletionType.BASIC,
//               psiElement(),
//               debuggingCompletionProvider);

        CompletionProvider<CompletionParameters> packageCompletionProvider = new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(
                    @NotNull CompletionParameters parameters,
                    ProcessingContext context,
                    @NotNull CompletionResultSet result) {
                result.addElement(AutoCompletionPolicy.ALWAYS_AUTOCOMPLETE.applyPolicy(keyword("package")));
            }
        };
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

        CompletionProvider<CompletionParameters> importPathCompletionProvider = new CompletionProvider<CompletionParameters>() {
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
                                    .withIcon(PlatformIcons.PACKAGE_ICON)
                                    .withTypeText("via sdk"));
                }

                Collection<String> goProjectPackages = packageNamesCache.getProjectPackages();

                for (String goPackage : goProjectPackages) {
                    result.addElement(
                            LookupElementBuilder.create("\"" + goPackage + "\"")
                                    .withIcon(PlatformIcons.PACKAGE_ICON)
                                    .bold()
                                    .withTypeText("via project"));
                }
            }
        };
        extend(CompletionType.BASIC,
               psiElement(GoTokenTypes.litSTRING)
                   .withParent(
                       psiElement(GoLiteralString.class)
                           .withParent(GoImportDeclaration.class)),
                importPathCompletionProvider);

        CompletionProvider<CompletionParameters> blockStatementsCompletionProvider = new CompletionProvider<CompletionParameters>() {
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

                for (String builtin : BULTINS_WITHOUT_RETURN) {
                    result.addElement(
                            builtinFunc(builtin, new FunctionInsertHandler()));
                }

                for (String builtin : BULTINS_WITH_RETURN) {
                    result.addElement(
                            builtinFunc(builtin, new FunctionInsertHandler()));
                }

                addPackageAutoCompletion(parameters, result);
            }
        };
        extend(CompletionType.BASIC,
               BLOCK_STATEMENT,
                blockStatementsCompletionProvider);

        CompletionProvider<CompletionParameters> topLevelKeywordsProvider = new CompletionProvider<CompletionParameters>() {
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
        extend(CompletionType.BASIC,
               psiElement().withParent(
                   psiElement(PsiErrorElement.class).withParent(
                       psiElement(GoFile.class).withChild(
                           psiElement(GoPackageDeclaration.class)
                       )
                   )
               ),
                topLevelKeywordsProvider);

        CompletionProvider<CompletionParameters> goAndDeferStatementCompletionProvider = new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters params,
                                          ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                result.addElement(
                        keyword("func", new LiteralFunctionInsertHandler()));
            }
        };
        extend(CompletionType.BASIC,
                GO_OR_DEFER_STATEMENT,
                goAndDeferStatementCompletionProvider);

        CompletionProvider<CompletionParameters> builtinFunctionsCompletionProvider = new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          ProcessingContext context,
                                          @NotNull CompletionResultSet result) {

                for (String builtinFunction : BULTINS_WITH_RETURN) {
                    result.addElement(
                            builtinFunc(builtinFunction, new FunctionInsertHandler()));
                }
            }
        };
        extend(CompletionType.BASIC,
               psiElement().withParent(
                   psiElement(GoLiteralIdentifier.class).withParent(
                       psiElement(GoLiteralExpression.class).withParent(
                           not(or(
                               psiElement(GoExpressionStatement.class),

                               // No builtin function is appropriate for go/defer statement.
                               psiElement(GoGoStatement.class),
                               psiElement(GoDeferStatement.class)
                           ))
                       )
                   )
               ),
                builtinFunctionsCompletionProvider);

        CompletionProvider<CompletionParameters> typeDeclarationCompletionProvider = new CompletionProvider<CompletionParameters>() {
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
            }

            private KeywordInsertionHandler createInterfaceInsertionHandler(CompletionParameters params) {
                if (isTypeNameInDeclaration(params.getPosition())) {
                    return new CurlyBracesInsertHandler();
                } else {
                    return new InlineCurlyBracesInsertHandler();
                }
            }
        };
        extend(CompletionType.BASIC,
                TYPE_DECLARATION,
                typeDeclarationCompletionProvider);

        CompletionProvider<CompletionParameters> packageNameCompletionProvider = new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters params,
                                          ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                addPackageAutoCompletion(params, result);
            }
        };
        extend(CompletionType.BASIC,
                VALID_PACKAGE_NAME_POSITION,
                packageNameCompletionProvider);
    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        context.setDummyIdentifier(DUMMY_IDENTIFIER);
    }

    private static void addPackageAutoCompletion(CompletionParameters parameters, CompletionResultSet result) {
        PsiFile originalFile = parameters.getOriginalFile();
        Set<String> importedPackages = new HashSet<String>();
        for (LookupElement element : getImportedPackagesNames(originalFile)) {
            result.addElement(element);
            importedPackages.add(element.getLookupString());
        }

        // For second basic completion, add all package names to auto completion list.
        if (parameters.getCompletionType() == CompletionType.BASIC &&
                parameters.getInvocationCount() > 1) {
            addAllPackageNames(result, originalFile, importedPackages);
        }
    }

    public static void addAllPackageNames(CompletionResultSet result, PsiFile file) {
        addAllPackageNames(result, file, Collections.<String>emptySet());
    }

    private static void addAllPackageNames(CompletionResultSet result, PsiFile file, Set<String> importedPackages) {
        String currentPackageName = getFilePackageName(file);

        Map<String, List<String>> packageMap = getPackageNameToImportPathMapping(file.getProject(), importedPackages);
        for (Map.Entry<String, List<String>> e : packageMap.entrySet()) {
            String packageName = e.getKey();

            // Don't add builtin or current package to code completion list.
            if ("builtin".equals(packageName) || currentPackageName.equals(packageName)) {
                continue;
            }

            String tailText = getPackageTailText(e.getValue());
            result.addElement(packageElement(packageName, tailText));
        }
    }

    private static String getFilePackageName(PsiFile file) {
        if (file instanceof GoFile) {
            GoPackageDeclaration currentPackage = ((GoFile) file).getPackage();
            if (currentPackage != null) {
                return currentPackage.getPackageName();
            }
        }
        return "";
    }

    private static String getPackageTailText(List<String> packages) {
        int size = packages.size();
        if (size > 1) {
            return size + " variants...";
        } else {
            return packages.get(0);
        }
    }

    private static Map<String, List<String>> getPackageNameToImportPathMapping(Project project, Set<String> importedPackages) {
        Map<String, List<String>> packageMap = new HashMap<String, List<String>>();
        for (String pkg : GoNamesCache.getInstance(project).getAllPackages()) {
            String visibleName = pkg;
            if (visibleName.contains("/")) {
                visibleName = visibleName.substring(visibleName.lastIndexOf('/') + 1);
            }
            if (!importedPackages.contains(visibleName)) {
                List<String> packages = packageMap.get(visibleName);
                if (packages == null) {
                    packages = new ArrayList<String>();
                    packageMap.put(visibleName, packages);
                }
                packages.add(pkg);
            }
        }
        return packageMap;
    }

    private static boolean isTypeNameInDeclaration(PsiElement element) {
        GoPsiTypeName typeName = GoPsiUtils.findParentOfType(element, GoPsiTypeName.class);
        return typeName != null && typeName.getParent() instanceof GoTypeSpec;

    }
}
