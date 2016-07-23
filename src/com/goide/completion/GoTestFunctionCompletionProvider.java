/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

import com.goide.GoConstants;
import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.goide.runconfig.testing.GoTestFunctionType;
import com.goide.runconfig.testing.frameworks.gotest.GotestGenerateAction;
import com.goide.sdk.GoPackageUtil;
import com.goide.stubs.index.GoFunctionIndex;
import com.goide.stubs.index.GoIdFilter;
import com.goide.stubs.index.GoMethodIndex;
import com.goide.stubs.types.GoMethodDeclarationStubElementType;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.IdFilter;
import com.intellij.util.text.UniqueNameGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class GoTestFunctionCompletionProvider extends CompletionProvider<CompletionParameters> {
  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    Project project = parameters.getPosition().getProject();
    PsiFile file = parameters.getOriginalFile();
    PsiDirectory containingDirectory = file.getContainingDirectory();
    if (file instanceof GoFile && containingDirectory != null) {
      CompletionResultSet resultSet = result.withPrefixMatcher(new CamelHumpMatcher(result.getPrefixMatcher().getPrefix(), false));

      Collection<String> allPackageFunctionNames = collectAllFunctionNames(containingDirectory);      
      Set<String> allTestFunctionNames = collectAllTestNames(allPackageFunctionNames, project, (GoFile)file);
      
      String fileNameWithoutTestPrefix = StringUtil.trimEnd(file.getName(), GoConstants.TEST_SUFFIX_WITH_EXTENSION) + ".go";
      GlobalSearchScope packageScope = GoPackageUtil.packageScope(containingDirectory, ((GoFile)file).getCanonicalPackageName());
      GlobalSearchScope scope = new GoUtil.ExceptTestsScope(packageScope);
      IdFilter idFilter = GoIdFilter.getFilesFilter(scope);
      for (String functionName : allPackageFunctionNames) {
        GoFunctionIndex.process(functionName, project, scope, idFilter, declaration -> {
          addVariants(declaration, functionName, fileNameWithoutTestPrefix, allTestFunctionNames, resultSet);
          return false;
        });
      }

      Collection<String> methodKeys = ContainerUtil.newTroveSet();
      StubIndex.getInstance().processAllKeys(GoMethodIndex.KEY, new CancellableCollectProcessor<>(methodKeys), scope, idFilter);
      for (String key : methodKeys) {
        Processor<GoMethodDeclaration> processor = declaration -> {
          GoMethodDeclarationStubElementType.calcTypeText(declaration);
          String typeText = key.substring(Math.min(key.indexOf('.') + 1, key.length()));
          String methodName = declaration.getName();
          if (methodName != null) {
            if (!declaration.isPublic() || declaration.isBlank()) {
              return true;
            }
            String lookupString = !typeText.isEmpty() ? StringUtil.capitalize(typeText) + "_" + methodName : methodName;
            addVariants(declaration, lookupString, fileNameWithoutTestPrefix, allTestFunctionNames, resultSet);
          }
          return true;
        };
        GoMethodIndex.process(key, project, scope, idFilter, processor);
      }
    }
  }

  private static void addVariants(@NotNull GoFunctionOrMethodDeclaration declaration,
                                  @NotNull String functionName,
                                  @NotNull String fileNameWithoutTestPrefix,
                                  @NotNull Set<String> allTestFunctionNames,
                                  @NotNull CompletionResultSet resultSet) {
    int priority = fileNameWithoutTestPrefix.equals(declaration.getContainingFile().getName()) ? 5 : 0;
    addLookupElement(GoConstants.TEST_PREFIX + functionName, priority, allTestFunctionNames, resultSet);
    addLookupElement(GoConstants.BENCHMARK_PREFIX + functionName, priority, allTestFunctionNames, resultSet);
    addLookupElement(GoConstants.EXAMPLE_PREFIX + functionName, priority, allTestFunctionNames, resultSet);
  }

  private static void addLookupElement(@NotNull String lookupString,
                                       int initialPriority,
                                       @NotNull Set<String> allTestFunctionNames,
                                       @NotNull CompletionResultSet result) {
    int priority = initialPriority;
    if (allTestFunctionNames.contains(lookupString)) {
      priority -= 5;
      lookupString = UniqueNameGenerator.generateUniqueName(lookupString, allTestFunctionNames);
    }
    result.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(lookupString)
                                                              .withInsertHandler(GenerateTestInsertHandler.INSTANCE), priority));
  }

  @NotNull
  private static Set<String> collectAllFunctionNames(@NotNull PsiDirectory directory) {
    GlobalSearchScope packageScope = GoPackageUtil.packageScope(directory, null);
    IdFilter packageIdFilter = GoIdFilter.getFilesFilter(packageScope);
    
    Set<String> result = ContainerUtil.newHashSet();
    StubIndex.getInstance().processAllKeys(GoFunctionIndex.KEY, new CancellableCollectProcessor<String>(result) {
      @Override
      protected boolean accept(String s) {
        return !"_".equals(s) && StringUtil.isCapitalized(s);
      }
    }, packageScope, packageIdFilter);
    return result;
  }

  @NotNull
  private static Set<String> collectAllTestNames(@NotNull Collection<String> names, @NotNull Project project, @NotNull GoFile file) {
    Set<String> result = ContainerUtil.newHashSet();
    GlobalSearchScope packageScope = GoPackageUtil.packageScope(file);
    GlobalSearchScope scope = new GoUtil.TestsScope(packageScope);
    IdFilter idFilter = GoIdFilter.getFilesFilter(packageScope);
    for (String name : names) {
      if (GoTestFunctionType.fromName(name) != null) {
        GoFunctionIndex.process(name, project, scope, idFilter, declaration -> {
          result.add(name);
          return false;
        });
      }
    }
    return result;
  }

  private static class GenerateTestInsertHandler implements InsertHandler<LookupElement> {
    public static final InsertHandler<LookupElement> INSTANCE = new GenerateTestInsertHandler();

    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
      PsiElement elementAt = context.getFile().findElementAt(context.getStartOffset());
      GoFunctionOrMethodDeclaration declaration = PsiTreeUtil.getNonStrictParentOfType(elementAt, GoFunctionOrMethodDeclaration.class);
      if (declaration != null) {
        GoTestFunctionType testingType = GoTestFunctionType.fromName(declaration.getName());
        if (testingType != null) {
          String testingQualifier = null;
          if (testingType.getParamType() != null) {
            testingQualifier = GotestGenerateAction.importTestingPackageIfNeeded(declaration.getContainingFile());
          }

          GoBlock block = declaration.getBlock();
          if (block == null) {
            block = (GoBlock)declaration.add(GoElementFactory.createBlock(context.getProject()));
          }
          else if (block.getStatementList().isEmpty()) {
            block = (GoBlock)block.replace(GoElementFactory.createBlock(context.getProject()));
          }

          GoSignature newSignature = GoElementFactory.createFunctionSignatureFromText(context.getProject(),
                                                                                      testingType.getSignature(testingQualifier));
          GoSignature signature = declaration.getSignature();
          if (signature == null) {
            declaration.addBefore(newSignature, block);
          }
          else if (signature.getParameters().getParameterDeclarationList().isEmpty()) {
            signature.replace(newSignature);
          }

          Document document = context.getDocument();
          PsiDocumentManager.getInstance(context.getProject()).doPostponedOperationsAndUnblockDocument(document);
          GoStatement firstStatement = ContainerUtil.getFirstItem(block.getStatementList());
          if (firstStatement != null) {
            context.getEditor().getCaretModel().moveToOffset(firstStatement.getTextRange().getStartOffset());
          }
          else {
            PsiElement lbrace = block.getLbrace();
            PsiElement rbrace = block.getRbrace();
            int lbraceEndOffset = lbrace.getTextRange().getEndOffset();
            int startLine = document.getLineNumber(lbraceEndOffset);
            int endLine = rbrace != null ? document.getLineNumber(rbrace.getTextRange().getStartOffset()) : startLine;
            int lineDiff = endLine - startLine;
            if (lineDiff < 2) {
              document.insertString(lbraceEndOffset, StringUtil.repeat("\n", 2 - lineDiff));
            }
            int offsetToMove = document.getLineStartOffset(startLine + 1);
            context.getEditor().getCaretModel().moveToOffset(offsetToMove);
            CodeStyleManager.getInstance(context.getProject()).adjustLineIndent(document, offsetToMove);
          }
        }
      }
    }
  }
}
