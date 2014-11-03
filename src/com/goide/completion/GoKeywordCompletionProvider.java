/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
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

  @NotNull
  private LookupElement createKeywordLookupElement(@NotNull final String keyword) {
    InsertHandler<LookupElement> insertHandler = createTemplateBasedInsertHandler("go_lang_" + keyword);
    LookupElementBuilder result = LookupElementBuilder.create(keyword).withBoldness(true).withInsertHandler(insertHandler);
    return myCompletionPolicy != null ? myCompletionPolicy.applyPolicy(result) : result;
  }

  @Nullable
  public static InsertHandler<LookupElement> createTemplateBasedInsertHandler(@NotNull final String templateId) {
    return new InsertHandler<LookupElement>() {
      @Override
      public void handleInsert(@NotNull InsertionContext context, LookupElement item) {
        TemplateManagerImpl templateManager = (TemplateManagerImpl)TemplateManager.getInstance(context.getProject());
        Template template = TemplateSettings.getInstance().getTemplateById(templateId);
        Editor editor = context.getEditor();
        if (template != null) {
          editor.getDocument().deleteString(context.getStartOffset(), context.getTailOffset());
          templateManager.startTemplate(editor, template);
        }
        else {
          EditorModificationUtil.insertStringAtCaret(editor, " ");
        }
      }
    };
  }
}
