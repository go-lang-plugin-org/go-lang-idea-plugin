package com.goide.completion;

import com.goide.GoTypes;
import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.stubs.index.GoFunctionIndex;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.PsiFilePattern;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;
import static com.intellij.patterns.StandardPatterns.or;
import static com.intellij.patterns.StandardPatterns.string;

public class GoCompletionContributor extends CompletionContributor {
  public static final int FUNCTION_PRIORITY = 10;
  public static final int FUNCTION_WITH_PACKAGE_PRIORITY = 0;
  public static final int TYPE_PRIORITY = 15;
  public static final int TYPE_CONVERSION = 15;
  public static final int VAR_PRIORITY = 15;
  public static final int LABEL_PRIORITY = 15;
  public static final int PACKAGE_PRIORITY = 5;

  public GoCompletionContributor() {
    extend(CompletionType.BASIC, packagePattern(), new GoKeywordCompletionProvider(AutoCompletionPolicy.ALWAYS_AUTOCOMPLETE, "package"));
    extend(CompletionType.BASIC, importPattern(), new GoKeywordCompletionProvider("import"));
    extend(CompletionType.BASIC, topLevelPattern(), new GoKeywordCompletionProvider("const", "var", "func", "type"));
  }

  private static PsiElementPattern.Capture<PsiElement> inGoFile() {
    return psiElement().inFile(psiElement(GoFile.class));
  }

  private static PsiElementPattern.Capture<PsiElement> topLevelPattern() {
    return onNewLine().withParent(psiElement(PsiErrorElement.class).withParent(goFileWithPackage()));
  }


  private static PsiElementPattern.Capture<PsiElement> importPattern() {
    return onNewLine().withParent(psiElement(PsiErrorElement.class).afterSiblingSkipping(psiElement().whitespace(),
                                                                                         psiElement(GoImportList.class)));
  }

  private static PsiElementPattern.Capture<PsiElement> packagePattern() {
    return psiElement().withParent(psiElement(PsiErrorElement.class).withParent(goFileWithoutPackage()).isFirstAcceptedChild(psiElement()));
  }

  private static PsiElementPattern.Capture<PsiElement> onNewLine() {
    return psiElement().afterLeafSkipping(psiElement().whitespaceCommentEmptyOrError().withoutText(string().contains("\n")),
                                          or(psiElement(GoTypes.SEMICOLON), psiElement().withText(string().contains("\n"))));
  }

  private static PsiFilePattern.Capture<GoFile> goFileWithPackage() {
    return psiFile(GoFile.class).withFirstNonWhitespaceChild(psiElement(GoTypes.PACKAGE_CLAUSE));
  }

  private static PsiFilePattern.Capture<GoFile> goFileWithoutPackage() {
    return psiFile(GoFile.class).andNot(psiElement().withFirstNonWhitespaceChild(psiElement(GoTypes.PACKAGE_CLAUSE)));
  }

  public static class AutoImport extends CompletionContributor {

    private static final ParenthesesWithImport FUNC_IMPORT_INSERT_HANDLER = new ParenthesesWithImport();

    public AutoImport() {
      extend(CompletionType.BASIC, inGoFile(), new CompletionProvider<CompletionParameters>() {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      ProcessingContext context,
                                      @NotNull CompletionResultSet result) {
          PsiElement position = parameters.getPosition();
          PsiElement parent = position.getParent();
          if (parent.getParent() instanceof GoSelectorExpr) return;
          if (parent instanceof GoReferenceExpression) {
            GoReferenceExpression qualifier = ((GoReferenceExpression)parent).getQualifier();
            if (qualifier == null || qualifier.getReference().resolve() == null) {

              int startOffset = parent.getTextRange().getStartOffset();
              String newPrefix = parameters.getEditor().getDocument().getText(TextRange.create(startOffset, parameters.getOffset()));
              result = result.withPrefixMatcher(result.getPrefixMatcher().cloneWithPrefix(newPrefix));
              
              Project project = parent.getProject();
              Collection<String> functionNames = StubIndex.getInstance().getAllKeys(GoFunctionIndex.KEY, project);
              for (String name : functionNames) {
                if (StringUtil.isCapitalized(name) && !StringUtil.startsWith(name, "Test") && !StringUtil.startsWith(name, "Benchmark")) {
                  for (GoFunctionDeclaration declaration : GoFunctionIndex.find(name, project, GlobalSearchScope.allScope(project))) {
                    GoFile file = declaration.getContainingFile();
                    if (!GoUtil.allowed(file)) continue;
                    String packageName = file.getPackageName();
                    if (packageName != null && StringUtil.endsWith(packageName, "_test")) continue;
                    if (StringUtil.equals(packageName, "main")) continue;
                    result.addElement(GoPsiImplUtil.createFunctionOrMethodLookupElement(declaration, true, FUNC_IMPORT_INSERT_HANDLER));
                  }
                }
              }
            }
          }
        }
      });
    }

    private static class ParenthesesWithImport extends ParenthesesInsertHandler<LookupElement> {
      @Override
      public void handleInsert(InsertionContext context, LookupElement item) {
        PsiElement myDeclaration = item.getPsiElement();
        if (myDeclaration instanceof GoFunctionDeclaration) {
          super.handleInsert(context, item);
          Editor editor = context.getEditor();
          Document document = editor.getDocument();
          String name = ((GoFunctionDeclaration)myDeclaration).getContainingFile().getPackageName();
          String full = ((GoFunctionDeclaration)myDeclaration).getContainingFile().getFullPackageName();
          if (name == null || full == null) return;
          document.insertString(context.getStartOffset(), name + ".");
          PsiDocumentManager.getInstance(context.getProject()).commitDocument(document);
          PsiFile file = context.getFile();
          if (!(file instanceof GoFile)) return;
          if (!((GoFile)file).getImportMap().get(name).isEmpty()) return;
          GoImportList list = ((GoFile)file).getImportList();
          if (list != null) {
            list.addImport(full, null);
          }
        }
      }

      @Override
      protected boolean placeCaretInsideParentheses(InsertionContext context, LookupElement item) {
        PsiElement e = item.getPsiElement();
        GoSignature signature = e instanceof GoFunctionDeclaration ? ((GoFunctionDeclaration)e).getSignature() : null;
        return signature != null && signature.getParameters().getParameterDeclarationList().size() > 0;
      }
    }
  }
}
