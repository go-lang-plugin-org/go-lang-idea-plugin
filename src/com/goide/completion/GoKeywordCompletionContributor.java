/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader
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

import com.goide.GoTypes;
import com.goide.psi.*;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.PsiFilePattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.goide.completion.GoKeywordCompletionProvider.EMPTY_INSERT_HANDLER;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;
import static com.intellij.patterns.StandardPatterns.*;

public class GoKeywordCompletionContributor extends CompletionContributor {
  private static final AddBracesInsertHandler ADD_BRACES_INSERT_HANDLER = new AddBracesInsertHandler();
  private static final InsertHandler<LookupElement> ADD_BRACKETS_INSERT_HANDLER = new AddBracketsInsertHandler();

  public GoKeywordCompletionContributor() {
    extend(CompletionType.BASIC, packagePattern(), new GoKeywordCompletionProvider(GoCompletionUtil.KEYWORD_PRIORITY,
                                                                                   AutoCompletionPolicy.ALWAYS_AUTOCOMPLETE, "package"));
    extend(CompletionType.BASIC, importPattern(), new GoKeywordCompletionProvider(GoCompletionUtil.KEYWORD_PRIORITY, "import"));
    extend(CompletionType.BASIC, topLevelPattern(),
           new GoKeywordCompletionProvider(GoCompletionUtil.KEYWORD_PRIORITY, "const", "var", "func", "type"));
    extend(CompletionType.BASIC, insideBlockPattern(GoTypes.IDENTIFIER),
           new GoKeywordCompletionProvider(GoCompletionUtil.KEYWORD_PRIORITY, "for", "const", "var", "return", "if", "switch", "go",
                                           "defer", "goto"));
    extend(CompletionType.BASIC, insideBlockPattern(GoTypes.IDENTIFIER),
           new GoKeywordCompletionProvider(GoCompletionUtil.KEYWORD_PRIORITY, EMPTY_INSERT_HANDLER, "fallthrough"));
    extend(CompletionType.BASIC, insideBlockPattern(GoTypes.IDENTIFIER),
           new GoKeywordCompletionProvider(GoCompletionUtil.KEYWORD_PRIORITY, ADD_BRACES_INSERT_HANDLER, "select"));
    extend(CompletionType.BASIC, typeDeclaration(),
           new GoKeywordCompletionProvider(GoCompletionUtil.KEYWORD_PRIORITY, ADD_BRACES_INSERT_HANDLER,
                                           "interface", "struct"));
    extend(CompletionType.BASIC, insideForStatement(GoTypes.IDENTIFIER),
           new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, EMPTY_INSERT_HANDLER, "break", "continue"));
    extend(CompletionType.BASIC, typeExpression(), new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, "chan"));
    extend(CompletionType.BASIC, typeExpression(),
           new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, ADD_BRACKETS_INSERT_HANDLER,
                                           "map"));
    extend(CompletionType.BASIC, afterIfBlock(GoTypes.IDENTIFIER),
           new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, "else"));
    extend(CompletionType.BASIC, afterElseKeyword(), new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, "if"));
    //extend(CompletionType.BASIC, insideSwitchStatement(), new GoKeywordCompletionProvider(CONTEXT_KEYWORD_PRIORITY, "case", "default"));
    //  todo: "case", "default", "range"
  }

  @Override
  public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
    super.fillCompletionVariants(parameters, result);
    if (insideGoOrDeferStatements(GoTypes.IDENTIFIER).accepts(parameters.getPosition())) {
      InsertHandler<LookupElement> insertHandler = GoKeywordCompletionProvider.createTemplateBasedInsertHandler("go_lang_anonymous_func");
      result.addElement(
        GoKeywordCompletionProvider.createKeywordLookupElement("func", GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, insertHandler));
    }
  }

  private static ElementPattern<? extends PsiElement> afterIfBlock(@NotNull IElementType tokenType) {
    return psiElement(tokenType).withParent(
      psiElement(GoExpression.class).withParent(psiElement(GoStatement.class)
                                                  .afterSiblingSkipping(psiElement().whitespaceCommentEmptyOrError(),
                                                                        psiElement(GoIfStatement.class))))
      .andNot(afterElseKeyword()).andNot(onStatementBeginning(tokenType));
  }

  private static ElementPattern<? extends PsiElement> afterElseKeyword() {
    return psiElement(GoTypes.IDENTIFIER).afterLeafSkipping(psiElement().whitespaceCommentEmptyOrError(), psiElement(GoTypes.ELSE));
  }

  private static ElementPattern<? extends PsiElement> insideForStatement(@NotNull IElementType tokenType) {
    return insideBlockPattern(tokenType).inside(GoForStatement.class);
  }

  private static ElementPattern<? extends PsiElement> typeExpression() {
    return psiElement(GoTypes.IDENTIFIER).withParent(GoTypeReferenceExpression.class);
  }

  //private static ElementPattern<? extends PsiElement> insideSwitchStatement() {
  //  return insideBlockPattern().inside(GoSwitchStatement.class);
  //}

  private static ElementPattern<? extends PsiElement> typeDeclaration() {
    return psiElement(GoTypes.IDENTIFIER)
      .withParent(psiElement(GoTypeReferenceExpression.class).withParent(psiElement(GoType.class).withParent(GoTypeSpec.class)));
  }

  private static PsiElementPattern.Capture<PsiElement> insideGoOrDeferStatements(@NotNull IElementType tokenType) {
    return psiElement(tokenType)
      .withParent(psiElement(GoExpression.class).withParent(or(psiElement(GoDeferStatement.class), psiElement(GoGoStatement.class))));
  }

  private static PsiElementPattern.Capture<PsiElement> insideBlockPattern(@NotNull IElementType tokenType) {
    return onStatementBeginning(tokenType)
      .withParent(psiElement(GoExpression.class).withParent(psiElement(GoStatement.class).withParent(GoBlock.class)));
  }

  private static PsiElementPattern.Capture<PsiElement> topLevelPattern() {
    return onStatementBeginning(GoTypes.IDENTIFIER).withParent(psiElement(PsiErrorElement.class).withParent(goFileWithPackage()));
  }

  private static PsiElementPattern.Capture<PsiElement> importPattern() {
    return onStatementBeginning(GoTypes.IDENTIFIER)
      .withParent(psiElement(PsiErrorElement.class).afterSiblingSkipping(psiElement().whitespace(),
                                                                         psiElement(GoImportList.class)));
  }

  private static PsiElementPattern.Capture<PsiElement> packagePattern() {
    return psiElement(GoTypes.IDENTIFIER)
      .withParent(psiElement(PsiErrorElement.class).withParent(goFileWithoutPackage()).isFirstAcceptedChild(psiElement()));
  }

  private static PsiElementPattern.Capture<PsiElement> onStatementBeginning(@NotNull IElementType tokenType) {
    return psiElement(tokenType).afterLeafSkipping(psiElement().whitespaceCommentEmptyOrError().withoutText(string().containsChars("\n")),
                                                   or(psiElement(GoTypes.SEMICOLON), psiElement(GoTypes.LBRACE),
                                                      psiElement().withText(string().containsChars("\n"))));
  }

  private static PsiFilePattern.Capture<GoFile> goFileWithPackage() {
    return psiFile(GoFile.class).withChildren(collection(PsiElement.class).filter(not(psiElement().whitespaceCommentEmptyOrError()),
                                                                                  collection(PsiElement.class)
                                                                                    .first(psiElement(GoTypes.PACKAGE_CLAUSE))));
  }

  private static PsiFilePattern.Capture<GoFile> goFileWithoutPackage() {
    return psiFile(GoFile.class).withChildren(collection(PsiElement.class).filter(not(psiElement().whitespaceCommentEmptyOrError()),
                                                                                  collection(PsiElement.class).first(
                                                                                    not(psiElement(GoTypes.PACKAGE_CLAUSE)))));
  }
}
