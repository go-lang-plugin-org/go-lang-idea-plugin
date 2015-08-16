/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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
import com.intellij.openapi.project.DumbAware;
import com.intellij.patterns.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.goide.completion.GoKeywordCompletionProvider.EMPTY_INSERT_HANDLER;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;
import static com.intellij.patterns.StandardPatterns.*;

public class GoKeywordCompletionContributor extends CompletionContributor implements DumbAware {
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
           new GoKeywordCompletionProvider(GoCompletionUtil.KEYWORD_PRIORITY, BracesInsertHandler.INSTANCE, "select"));
    extend(CompletionType.BASIC, typeDeclaration(),
           new GoKeywordCompletionProvider(GoCompletionUtil.KEYWORD_PRIORITY, BracesInsertHandler.INSTANCE, "interface", "struct"));
    extend(CompletionType.BASIC, typeExpression(),
           new GoKeywordCompletionProvider(GoCompletionUtil.KEYWORD_PRIORITY, BracesInsertHandler.ONE_LINER, "interface", "struct"));
    extend(CompletionType.BASIC, insideForStatement(GoTypes.IDENTIFIER),
           new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, EMPTY_INSERT_HANDLER, "break", "continue"));
    extend(CompletionType.BASIC, typeExpression(), new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, "chan"));
    extend(CompletionType.BASIC, typeExpression(), new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, 
                                                                                   ADD_BRACKETS_INSERT_HANDLER, "map"));
    
    extend(CompletionType.BASIC, referenceExpression(), new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY,
                                                                                        ADD_BRACKETS_INSERT_HANDLER, "map"));
    extend(CompletionType.BASIC, referenceExpression(), new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY,
                                                                                        BracesInsertHandler.ONE_LINER, "struct"));
    
    extend(CompletionType.BASIC, afterIfBlock(GoTypes.IDENTIFIER), 
           new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, "else"));
    extend(CompletionType.BASIC, afterElseKeyword(), new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, "if"));
    extend(CompletionType.BASIC, insideSwitchStatement(), 
           new GoKeywordCompletionProvider(GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, "case", "default"));
    //  todo: "range"
  }

  @Override
  public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
    super.fillCompletionVariants(parameters, result);
    if (insideGoOrDeferStatements().accepts(parameters.getPosition())) {
      InsertHandler<LookupElement> insertHandler = GoKeywordCompletionProvider.createTemplateBasedInsertHandler("go_lang_anonymous_func");
      result.addElement(
        GoKeywordCompletionProvider.createKeywordLookupElement("func", GoCompletionUtil.CONTEXT_KEYWORD_PRIORITY, insertHandler));
    }
  }

  private static ElementPattern<? extends PsiElement> afterIfBlock(@NotNull IElementType tokenType) {
    PsiElementPattern.Capture<GoStatement> statement = 
      psiElement(GoStatement.class).afterSiblingSkipping(psiElement().whitespaceCommentEmptyOrError(), psiElement(GoIfStatement.class));
    PsiElementPattern.Capture<GoLeftHandExprList> lh = psiElement(GoLeftHandExprList.class).withParent(statement);
    return psiElement(tokenType).withParent(psiElement(GoReferenceExpressionBase.class).with(new GoNonQualifiedReference()).withParent(lh))
      .andNot(afterElseKeyword()).andNot(onStatementBeginning(tokenType));
  }

  private static ElementPattern<? extends PsiElement> afterElseKeyword() {
    return psiElement(GoTypes.IDENTIFIER).afterLeafSkipping(psiElement().whitespaceCommentEmptyOrError(), psiElement(GoTypes.ELSE));
  }

  private static ElementPattern<? extends PsiElement> insideForStatement(@NotNull IElementType tokenType) {
    return insideBlockPattern(tokenType).inside(GoForStatement.class);
  }

  private static ElementPattern<? extends PsiElement> typeExpression() {
    return psiElement(GoTypes.IDENTIFIER).withParent(
      psiElement(GoTypeReferenceExpression.class).with(new GoNonQualifiedReference()));
  }
  
  private static ElementPattern<? extends PsiElement> referenceExpression() {
    return psiElement(GoTypes.IDENTIFIER).withParent(
      psiElement(GoReferenceExpression.class).withParent(not(psiElement(GoSelectorExpr.class))).with(new GoNonQualifiedReference()));
  }

  private static ElementPattern<? extends PsiElement> insideSwitchStatement() {
    return onStatementBeginning(GoTypes.IDENTIFIER, GoTypes.CASE, GoTypes.DEFAULT).inside(GoCaseClause.class);
  }

  private static ElementPattern<? extends PsiElement> typeDeclaration() {
    return psiElement(GoTypes.IDENTIFIER)
      .withParent(psiElement(GoTypeReferenceExpression.class).withParent(psiElement(GoType.class).withParent(GoSpecType.class)));
  }

  private static PsiElementPattern.Capture<PsiElement> insideGoOrDeferStatements() {
    return psiElement(GoTypes.IDENTIFIER)
      .withParent(psiElement(GoExpression.class).withParent(or(psiElement(GoDeferStatement.class), psiElement(GoGoStatement.class))));
  }

  private static PsiElementPattern.Capture<PsiElement> insideBlockPattern(@NotNull IElementType tokenType) {
    return onStatementBeginning(tokenType)
      .withParent(psiElement(GoExpression.class).withParent(psiElement(GoLeftHandExprList.class).withParent(
        psiElement(GoStatement.class).withParent(GoBlock.class))));
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

  private static PsiElementPattern.Capture<PsiElement> onStatementBeginning(@NotNull IElementType... tokenTypes) {
    return psiElement().withElementType(TokenSet.create(tokenTypes))
      .afterLeafSkipping(psiElement().whitespaceCommentEmptyOrError().withoutText(string().containsChars("\n")),
                         or(psiElement(GoTypes.SEMICOLON), psiElement(GoTypes.LBRACE),
                            psiElement().withText(string().containsChars("\n"))));
  }

  private static PsiFilePattern.Capture<GoFile> goFileWithPackage() {
    CollectionPattern<PsiElement> collection = collection(PsiElement.class);
    CollectionPattern<PsiElement> packageIsFirst = collection.first(psiElement(GoTypes.PACKAGE_CLAUSE));
    return psiFile(GoFile.class).withChildren(collection.filter(not(psiElement().whitespaceCommentEmptyOrError()),
                                                                packageIsFirst));
  }

  private static PsiFilePattern.Capture<GoFile> goFileWithoutPackage() {
    CollectionPattern<PsiElement> collection = collection(PsiElement.class);
    ElementPattern<Collection<PsiElement>> emptyOrPackageIsNotFirst = or(collection.empty(),
                                                                         collection.first(not(psiElement(GoTypes.PACKAGE_CLAUSE))));
    return psiFile(GoFile.class).withChildren(collection.filter(not(psiElement().whitespaceCommentEmptyOrError()),
                                                                emptyOrPackageIsNotFirst));
  }

  private static class GoNonQualifiedReference extends PatternCondition<GoReferenceExpressionBase> {
    public GoNonQualifiedReference() {
      super("non qualified type");
    }

    @Override
    public boolean accepts(@NotNull GoReferenceExpressionBase element, ProcessingContext context) {
      return element.getQualifier() == null;
    }
  }
}
