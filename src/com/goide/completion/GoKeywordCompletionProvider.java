package com.goide.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoKeywordCompletionProvider extends CompletionProvider<CompletionParameters> {
  @Nullable private final AutoCompletionPolicy myCompletionPolicy;
  @NotNull private final String[] myKeywords;

  public GoKeywordCompletionProvider(String... keywords) {
    this(null, keywords);
  }

  public GoKeywordCompletionProvider(@Nullable AutoCompletionPolicy completionPolicy, @NotNull String... keywords) {
    myCompletionPolicy = completionPolicy;
    myKeywords = keywords;
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    for (String keyword : myKeywords) {
      result.addElement(createKeywordLookupElement(keyword));
    }
  }

  private LookupElement createKeywordLookupElement(final String keyword) {
    InsertHandler<LookupElement> insertHandler = createTemplateBasedInsertHandler("go_lang_" + keyword);
    LookupElementBuilder result = LookupElementBuilder.create(keyword).withBoldness(true).withInsertHandler(insertHandler);
    return myCompletionPolicy != null ? myCompletionPolicy.applyPolicy(result) : result;
  }

  public static InsertHandler<LookupElement> createTemplateBasedInsertHandler(@NotNull final String templateId) {
    // todo: add space after keyword if there are no template with given id
    return new InsertHandler<LookupElement>() {
      @Override
      public void handleInsert(InsertionContext context, LookupElement item) {
        TemplateManagerImpl templateManager = (TemplateManagerImpl)TemplateManager.getInstance(context.getProject());
        Template template = TemplateSettings.getInstance().getTemplateById(templateId);
        if (template != null) {
          context.getEditor().getDocument().deleteString(context.getStartOffset(), context.getTailOffset());
          templateManager.startTemplate(context.getEditor(), template);
        }
      }
    };
  }
}
