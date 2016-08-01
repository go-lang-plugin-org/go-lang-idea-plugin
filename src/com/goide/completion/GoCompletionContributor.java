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
import com.goide.GoParserDefinition;
import com.goide.GoTypes;
import com.goide.psi.*;
import com.goide.psi.impl.GoCachedReference;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.sdk.GoPackageUtil;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Conditions;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.intellij.codeInsight.completion.PrioritizedLookupElement.withPriority;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class GoCompletionContributor extends CompletionContributor {
  public GoCompletionContributor() {
    extend(CompletionType.BASIC, importString(), new GoImportPathsCompletionProvider());
    extend(CompletionType.BASIC, referenceExpression(), new GoReferenceCompletionProvider());
    extend(CompletionType.BASIC, goReference(), new GoReferenceCompletionProvider());
    extend(CompletionType.BASIC, testFunctionSignature(), new GoTestFunctionCompletionProvider());
  }

  private static PsiElementPattern.Capture<PsiElement> testFunctionSignature() {
    return psiElement(GoTypes.IDENTIFIER).with(new GoTestFilePattern()).withParent(GoFunctionDeclaration.class)
      .afterLeaf(psiElement().withElementType(GoTypes.FUNC));
  }

  private static PsiElementPattern.Capture<PsiElement> goReference() {
    return psiElement().withParent(psiElement().withReference(GoCachedReference.class));
  }

  @Override
  public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
    PsiElement position = parameters.getPosition();
    PsiFile file = parameters.getOriginalFile();
    ASTNode node = position.getNode();
    if (file instanceof GoFile && position.getParent() instanceof GoPackageClause && node.getElementType() == GoTypes.IDENTIFIER) {
      boolean isTestFile = GoTestFinder.isTestFile(file);
      PsiDirectory directory = file.getParent();
      String currentPackageName = ((GoFile)file).getPackageName();
      Collection<String> packagesInDirectory = GoPackageUtil.getAllPackagesInDirectory(directory, null, true);
      for (String packageName : packagesInDirectory) {
        if (!packageName.equals(currentPackageName)) {
          result.addElement(packageLookup(packageName, GoCompletionUtil.PACKAGE_PRIORITY - 1));
        }
        if (isTestFile) {
          result.addElement(packageLookup(packageName + GoConstants.TEST_SUFFIX, GoCompletionUtil.PACKAGE_PRIORITY));
        }
      }

      if (directory != null && ContainerUtil.filter(directory.getFiles(), Conditions.instanceOf(GoFile.class)).size() == 1) {
        String packageFromDirectory = GoPsiImplUtil.getLocalPackageName(directory.getName());
        if (!packageFromDirectory.isEmpty()) {
          result.addElement(packageLookup(packageFromDirectory, GoCompletionUtil.PACKAGE_PRIORITY - 1));
        }
      }
      result.addElement(packageLookup(GoConstants.MAIN, GoCompletionUtil.PACKAGE_PRIORITY - 2));
    }
    super.fillCompletionVariants(parameters, result);
  }

  @NotNull
  private static LookupElement packageLookup(@NotNull String packageName, int priority) {
    LookupElement element = withPriority(LookupElementBuilder.create(packageName), priority);
    return AutoCompletionPolicy.NEVER_AUTOCOMPLETE.applyPolicy(element);
  }

  private static PsiElementPattern.Capture<PsiElement> importString() {
    return psiElement().withElementType(GoParserDefinition.STRING_LITERALS).withParent(GoStringLiteral.class)
      .withSuperParent(2, GoImportString.class);
  }

  private static PsiElementPattern.Capture<PsiElement> referenceExpression() {
    return psiElement().withParent(GoReferenceExpressionBase.class);
  }

  private static class GoTestFilePattern extends PatternCondition<PsiElement> {
    public GoTestFilePattern() {
      super("in go test file");
    }

    @Override
    public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
      return GoTestFinder.isTestFile(element.getContainingFile());
    }
  }
}
