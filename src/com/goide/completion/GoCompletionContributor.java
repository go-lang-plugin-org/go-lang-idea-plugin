package com.goide.completion;

import com.goide.GoLanguage;
import com.goide.GoParserDefinition;
import com.goide.GoTypes;
import com.goide.psi.GoFile;
import com.goide.psi.GoSelectorExpr;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.lang.ASTNode;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.intellij.patterns.PlatformPatterns.instanceOf;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class GoCompletionContributor extends CompletionContributor {
  public static final int FUNCTION_PRIORITY = 10;
  public static final int TYPE_PRIORITY = 15;
  public static final int TYPE_CONVERSION = 15;
  public static final int VAR_PRIORITY = 15;
  public static final int PACKAGE_PRIORITY = 5;

  public GoCompletionContributor() {
    extend(CompletionType.BASIC, inGoFile(), new CompletionProvider<CompletionParameters>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        ASTNode prev = FormatterUtil.getPreviousNonWhitespaceSibling(position.getNode());
        if (prev != null && prev.getElementType() == GoTypes.DOT) return;
        if (position.getNode().getElementType() == GoTypes.STRING) return;
        if (position.getParent().getParent() instanceof GoSelectorExpr) return;
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
}
