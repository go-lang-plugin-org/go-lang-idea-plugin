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
    LookupElementBuilder result =
      LookupElementBuilder.create(keyword).withBoldness(true).withInsertHandler(new InsertHandler<LookupElement>() {
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
    return myCompletionPolicy != null ? myCompletionPolicy.applyPolicy(result) : result;
  }
}
