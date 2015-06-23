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

import com.goide.GoConstants;
import com.goide.GoParserDefinition;
import com.goide.GoTypes;
import com.goide.psi.GoImportString;
import com.goide.psi.GoPackageClause;
import com.goide.psi.GoReferenceExpressionBase;
import com.goide.psi.GoVarDefinition;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GoCompletionContributor extends CompletionContributor {
  public GoCompletionContributor() {
    extend(CompletionType.BASIC, importString(), new GoImportPathsCompletionProvider());
    extend(CompletionType.BASIC, referenceExpression(), new GoReferenceCompletionProvider());
    extend(CompletionType.BASIC, varDefinition(), new GoReferenceCompletionProvider());
  }

  @Override
  public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
    PsiElement position = parameters.getPosition();
    if (position.getParent() instanceof GoPackageClause && position.getNode().getElementType() == GoTypes.IDENTIFIER) {
      PsiDirectory directory = parameters.getOriginalFile().getParent();
      Collection<String> packagesInDirectory = GoUtil.getAllPackagesInDirectory(directory);
      for (String packageName : packagesInDirectory) {
        result.addElement(LookupElementBuilder.create(packageName));
        if (packageName.endsWith(GoConstants.TEST_SUFFIX)) {
          result.addElement(LookupElementBuilder.create(StringUtil.trimEnd(packageName, GoConstants.TEST_SUFFIX)));
        }
      }

      if (packagesInDirectory.isEmpty() && directory != null) {
        String packageFromDirectory = FileUtil.sanitizeFileName(directory.getName());
        if (!packageFromDirectory.isEmpty()) {
          result.addElement(LookupElementBuilder.create(packageFromDirectory));
        }
      }
      result.addElement(LookupElementBuilder.create(GoConstants.MAIN));
    }
    super.fillCompletionVariants(parameters, result);
  }

  private static PsiElementPattern.Capture<PsiElement> importString() {
    return PlatformPatterns.psiElement().withElementType(GoParserDefinition.STRING_LITERALS).withParent(GoImportString.class);
  }

  private static PsiElementPattern.Capture<PsiElement> referenceExpression() {
    return PlatformPatterns.psiElement().withParent(GoReferenceExpressionBase.class);
  }

  private static PsiElementPattern.Capture<PsiElement> varDefinition() {
    return PlatformPatterns.psiElement().withParent(GoVarDefinition.class);
  }
}
