package com.goide.completion;

import com.goide.GoLanguage;
import com.goide.GoParserDefinition;
import com.goide.GoTypes;
import com.goide.psi.GoFile;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
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
  public static final int VAR_PRIORITY = 15;
  public static final int PACKAGE_PRIORITY = 5;

  public GoCompletionContributor() {
    extend(CompletionType.BASIC, psiElement().inFile(instanceOf(GoFile.class)), new CompletionProvider<CompletionParameters>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        if (position.getNode().getElementType() == GoTypes.STRING) return;
        for (String keyword : suggestKeywords(position)) {
          result.addElement(createKeywordLookupElement(keyword));
        }
      }
    });
  }

  @NotNull
  private static LookupElement createKeywordLookupElement(@NotNull String keyword) {
    return LookupElementBuilder.create(keyword).withBoldness(true).withInsertHandler(new InsertHandler<LookupElement>() {
      @Override
      public void handleInsert(InsertionContext context, LookupElement item) {
        Editor editor = context.getEditor();
        TemplateManagerImpl templateManager = (TemplateManagerImpl)TemplateManager.getInstance(context.getProject());
        Runnable expandTemplate = templateManager.prepareTemplate(editor, TemplateSettings.TAB_CHAR, null);
        if (expandTemplate != null) expandTemplate.run();
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
