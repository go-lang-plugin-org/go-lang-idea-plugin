package com.goide.completion;

import com.goide.GoLanguage;
import com.goide.GoParserDefinition;
import com.goide.GoTypes;
import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.stubs.index.GoFunctionIndex;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.lang.ASTNode;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.intellij.patterns.PlatformPatterns.instanceOf;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class GoCompletionContributor extends CompletionContributor {
  public static final int FUNCTION_PRIORITY = 10;
  public static final int FUNCTION_WITH_PACKAGE_PRIORITY = 0;
  public static final int TYPE_PRIORITY = 15;
  public static final int TYPE_CONVERSION = 15;
  public static final int VAR_PRIORITY = 15;
  public static final int LABEL_PRIORITY = 15;
  public static final int PACKAGE_PRIORITY = 5;

  public GoCompletionContributor() {
    extend(CompletionType.BASIC, inGoFile(), new CompletionProvider<CompletionParameters>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    ProcessingContext context,
                                    @NotNull final CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        ASTNode prev = FormatterUtil.getPreviousNonWhitespaceSibling(position.getNode());
        if (prev != null && prev.getElementType() == GoTypes.DOT) return;
        if (position.getNode().getElementType() == GoTypes.STRING) return;
        PsiElement parent = position.getParent();
        if (parent.getParent() instanceof GoSelectorExpr) return;
        if (position instanceof PsiComment) return;
        for (String keyword : suggestKeywords(position)) {
          result.addElement(createKeywordLookupElement(keyword));
        }
        
      }
    });
  }

  private static PsiElementPattern.Capture<PsiElement> inGoFile() {
    return psiElement().inFile(instanceOf(GoFile.class));
  }

  @NotNull
  private static LookupElement createKeywordLookupElement(@NotNull final String keyword) {
    return LookupElementBuilder.create(keyword).withBoldness(true).withInsertHandler(new InsertHandler<LookupElement>() {
      @Override
      public void handleInsert(InsertionContext context, LookupElement item) {
        TemplateManagerImpl templateManager = (TemplateManagerImpl)TemplateManager.getInstance(context.getProject());
        Template template = TemplateSettings.getInstance().getTemplateById("go_lang_" + keyword);
        if (template != null) {
          context.getEditor().getDocument().deleteString(context.getStartOffset(), context.getTailOffset());
          templateManager.startTemplate(context.getEditor(), template);
        }
      }
    });
  }

  @NotNull
  private static Collection<String> suggestKeywords(@NotNull PsiElement position) {
    TextRange posRange = position.getTextRange();
    GoFile posFile = (GoFile)position.getContainingFile();
    TextRange range = new TextRange(0, posRange.getStartOffset());
    String text = range.isEmpty() ? CompletionInitializationContext.DUMMY_IDENTIFIER : range.substring(posFile.getText());

    PsiFile file = PsiFileFactory.getInstance(posFile.getProject()).createFileFromText("a.go", GoLanguage.INSTANCE, text, true, false);
    int completionOffset = posRange.getStartOffset() - range.getStartOffset();
    GeneratedParserUtilBase.CompletionState state = new GeneratedParserUtilBase.CompletionState(completionOffset) {
      @Override
      public String convertItem(Object o) {
        if (o instanceof IElementType && GoParserDefinition.KEYWORDS.contains((IElementType)o)) return o.toString();
        return o instanceof String ? (String)o : null;
      }
    };
    file.putUserData(GeneratedParserUtilBase.COMPLETION_STATE_KEY, state);
    TreeUtil.ensureParsed(file.getNode());
    return state.items;
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
              
              final Project project = parent.getProject();
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
        PsiElement myDeclaration = item.getPsiElement();
        if (myDeclaration instanceof GoFunctionDeclaration) {
          GoSignature signature = ((GoFunctionDeclaration)myDeclaration).getSignature();
          if (signature != null) {
            return signature.getParameters().getParameterDeclarationList().size() > 0;
          }
        }
        return false;
      }
    }
  }
}
