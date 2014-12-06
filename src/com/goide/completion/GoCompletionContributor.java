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
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoTypeReference;
import com.goide.stubs.index.GoFunctionIndex;
import com.goide.stubs.index.GoTypesIndex;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.PsiFilePattern;
import com.intellij.psi.*;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.goide.completion.GoKeywordCompletionProvider.EMPTY_INSERT_HANDLER;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;
import static com.intellij.patterns.StandardPatterns.*;

public class GoCompletionContributor extends CompletionContributor implements DumbAware {
  public static final int KEYWORD_PRIORITY = 20;
  public static final int CONTEXT_KEYWORD_PRIORITY = 25;
  public static final int FUNCTION_PRIORITY = 10;
  public static final int FUNCTION_WITH_PACKAGE_PRIORITY = 0;
  public static final int TYPE_PRIORITY = 15;
  public static final int TYPE_CONVERSION = 15;
  public static final int VAR_PRIORITY = 15;
  public static final int LABEL_PRIORITY = 15;
  public static final int PACKAGE_PRIORITY = 5;

  private static final AddBracesInsertHandler ADD_BRACES_INSERT_HANDLER = new AddBracesInsertHandler();
  private static final InsertHandler<LookupElement> ADD_BRACKETS_INSERT_HANDLER = new AddBracketsInsertHandler();

  public GoCompletionContributor() {
    // todo: move it away to GoKeywordCompletionContributor
    extend(CompletionType.BASIC, packagePattern(), new GoKeywordCompletionProvider(KEYWORD_PRIORITY,
                                                                                   AutoCompletionPolicy.ALWAYS_AUTOCOMPLETE, "package"));
    extend(CompletionType.BASIC, importPattern(), new GoKeywordCompletionProvider(KEYWORD_PRIORITY, "import"));
    extend(CompletionType.BASIC, topLevelPattern(), new GoKeywordCompletionProvider(KEYWORD_PRIORITY, "const", "var", "func", "type"));
    extend(CompletionType.BASIC, insideBlockPattern(GoTypes.IDENTIFIER),
           new GoKeywordCompletionProvider(KEYWORD_PRIORITY, "for", "const", "var", "return", "if", "switch", "go", "defer", "goto"));
    extend(CompletionType.BASIC, insideBlockPattern(GoTypes.IDENTIFIER),
           new GoKeywordCompletionProvider(KEYWORD_PRIORITY, EMPTY_INSERT_HANDLER, "fallthrough"));
    extend(CompletionType.BASIC, insideBlockPattern(GoTypes.IDENTIFIER),
           new GoKeywordCompletionProvider(KEYWORD_PRIORITY, ADD_BRACES_INSERT_HANDLER, "select"));
    extend(CompletionType.BASIC, typeDeclaration(), new GoKeywordCompletionProvider(KEYWORD_PRIORITY, new AddBracesInsertHandler(),
                                                                                    "interface", "struct"));
    extend(CompletionType.BASIC, insideForStatement(GoTypes.IDENTIFIER),
           new GoKeywordCompletionProvider(CONTEXT_KEYWORD_PRIORITY, EMPTY_INSERT_HANDLER, "break", "continue"));
    extend(CompletionType.BASIC, typeExpression(), new GoKeywordCompletionProvider(CONTEXT_KEYWORD_PRIORITY, "chan"));
    extend(CompletionType.BASIC, typeExpression(), new GoKeywordCompletionProvider(CONTEXT_KEYWORD_PRIORITY, ADD_BRACKETS_INSERT_HANDLER,
                                                                                   "map"));
    extend(CompletionType.BASIC, afterIfBlock(GoTypes.IDENTIFIER), new GoKeywordCompletionProvider(CONTEXT_KEYWORD_PRIORITY, "else"));
    extend(CompletionType.BASIC, afterElseKeyword(), new GoKeywordCompletionProvider(CONTEXT_KEYWORD_PRIORITY, "if"));
    //extend(CompletionType.BASIC, insideSwitchStatement(), new GoKeywordCompletionProvider(CONTEXT_KEYWORD_PRIORITY, "case", "default"));
    //  todo: "case", "default", "range" 
  }

  @Override
  public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
    super.fillCompletionVariants(parameters, result);
    if (insideGoOrDeferStatements(GoTypes.IDENTIFIER).accepts(parameters.getPosition())) {
      InsertHandler<LookupElement> insertHandler = GoKeywordCompletionProvider.createTemplateBasedInsertHandler("go_lang_anonymous_func");
      result.addElement(GoKeywordCompletionProvider.createKeywordLookupElement("func", CONTEXT_KEYWORD_PRIORITY, insertHandler));
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

  private static PsiElementPattern.Capture<PsiElement> inGoFile() {
    return psiElement().inFile(psiElement(GoFile.class));
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

  public static class AutoImport extends CompletionContributor {
    private static final ParenthesesWithImport FUNC_INSERT_HANDLER = new ParenthesesWithImport();
    private static final InsertHandler<LookupElement> TYPE_INSERT_HANDLER = new InsertHandler<LookupElement>() {
      @Override
      public void handleInsert(InsertionContext context, LookupElement item) {
        PsiElement element = item.getPsiElement();
        if (element instanceof GoNamedElement) {
          autoImport(context, (GoNamedElement)element);
        }
      }
    };

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
              result = adjustMatcher(parameters, result, parent);
              Project project = parent.getProject();
              for (String name : StubIndex.getInstance().getAllKeys(GoFunctionIndex.KEY, project)) {
                if (StringUtil.isCapitalized(name) && !StringUtil.startsWith(name, "Test") && !StringUtil.startsWith(name, "Benchmark")) {
                  for (GoFunctionDeclaration declaration : GoFunctionIndex.find(name, project, GoUtil.moduleScope(position))) {
                    if (!allowed(declaration)) continue;
                    result.addElement(GoPsiImplUtil.createFunctionOrMethodLookupElement(declaration, true, FUNC_INSERT_HANDLER));
                  }
                }
              }
            }
          }

          if (parent instanceof GoTypeReferenceExpression) {
            GoTypeReferenceExpression qualifier = ((GoTypeReferenceExpression)parent).getQualifier();
            if (qualifier == null || qualifier.getReference().resolve() == null) {
              result = adjustMatcher(parameters, result, parent);
              Project project = parent.getProject();
              for (String name : StubIndex.getInstance().getAllKeys(GoTypesIndex.KEY, project)) {
                if (StringUtil.isCapitalized(name)) {
                  for (GoTypeSpec declaration : GoTypesIndex.find(name, project, GoUtil.moduleScope(position))) {
                    if (declaration.getContainingFile() == parameters.getOriginalFile()) continue;
                    PsiReference reference = parent.getReference();
                    if (reference instanceof GoTypeReference && !((GoTypeReference)reference).allowed(declaration)) continue;
                    if (!allowed(declaration)) continue;
                    result.addElement(GoPsiImplUtil.createTypeLookupElement(declaration, true, TYPE_INSERT_HANDLER));
                  }
                }
              }
            }
          }
        }

        private boolean allowed(@NotNull GoNamedElement declaration) {
          GoFile file = declaration.getContainingFile();
          if (!GoUtil.allowed(file)) return false;
          PsiDirectory directory = file.getContainingDirectory();
          if (directory != null) {
            VirtualFile vFile = directory.getVirtualFile();
            if (vFile.getPath().endsWith("go/doc/testdata")) return false;
          }

          String packageName = file.getPackageName();
          if (packageName != null && StringUtil.endsWith(packageName, "_test")) return false;
          if (StringUtil.equals(packageName, "main")) return false;
          return true;
        }

        private CompletionResultSet adjustMatcher(@NotNull CompletionParameters parameters,
                                                  @NotNull CompletionResultSet result,
                                                  @NotNull PsiElement parent) {
          int startOffset = parent.getTextRange().getStartOffset();
          String newPrefix = parameters.getEditor().getDocument().getText(TextRange.create(startOffset, parameters.getOffset()));
          return result.withPrefixMatcher(result.getPrefixMatcher().cloneWithPrefix(newPrefix));
        }
      });
    }

    private static class ParenthesesWithImport extends ParenthesesInsertHandler<LookupElement> {
      @Override
      public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        PsiElement element = item.getPsiElement();
        if (element instanceof GoFunctionDeclaration) {
          super.handleInsert(context, item);
          autoImport(context, (GoNamedElement)element);
        }
      }

      @Override
      protected boolean placeCaretInsideParentheses(InsertionContext context, @NotNull LookupElement item) {
        PsiElement e = item.getPsiElement();
        GoSignature signature = e instanceof GoFunctionDeclaration ? ((GoFunctionDeclaration)e).getSignature() : null;
        return signature != null && signature.getParameters().getParameterDeclarationList().size() > 0;
      }
    }
  }

  private static void autoImport(@NotNull InsertionContext context, @NotNull GoNamedElement element) {
    Editor editor = context.getEditor();
    Document document = editor.getDocument();
    String name = element.getContainingFile().getPackageName();
    String full = element.getContainingFile().getFullPackageName();
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
