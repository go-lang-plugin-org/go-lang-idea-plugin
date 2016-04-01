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

package com.goide.usages;

import com.goide.GoParserDefinition;
import com.goide.GoTypes;
import com.goide.lexer.GoLexer;
import com.goide.psi.*;
import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.ElementDescriptionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoFindUsagesProvider implements FindUsagesProvider {
  @Nullable
  @Override
  public WordsScanner getWordsScanner() {
    return new DefaultWordsScanner(new GoLexer(), TokenSet.create(GoTypes.IDENTIFIER),
                                   GoParserDefinition.COMMENTS, GoParserDefinition.STRING_LITERALS);
  }

  @Override
  public boolean canFindUsagesFor(@NotNull PsiElement element) {
    if (element instanceof GoImportSpec) {
      GoImportSpec importSpec = (GoImportSpec)element;
      return importSpec.getAlias() != null && !importSpec.isDot() && !importSpec.isForSideEffects();
    }
    return element instanceof GoNamedElement;
  }

  @Nullable
  @Override
  public String getHelpId(@NotNull PsiElement psiElement) {
    return HelpID.FIND_OTHER_USAGES;
  }

  @NotNull
  @Override
  public String getType(@NotNull PsiElement element) {
    if (element instanceof GoMethodDeclaration) return "method";
    if (element instanceof GoFunctionDeclaration) return "function";
    if (element instanceof GoConstDefinition || element instanceof GoConstDeclaration) return "constant";
    if (element instanceof GoVarDefinition || element instanceof GoVarDeclaration) return "variable";
    if (element instanceof GoParamDefinition) return "parameter";
    if (element instanceof GoFieldDefinition) return "field";
    if (element instanceof GoAnonymousFieldDefinition) return "anonymous field";
    if (element instanceof GoTypeSpec || element instanceof GoTypeDeclaration) return "type";
    if (element instanceof GoImportDeclaration) return "import";
    if (element instanceof GoImportSpec) return "import alias";
    if (element instanceof GoReceiver) return "receiver";
    if (element instanceof GoMethodSpec) return "method specification";
    if (element instanceof GoLabelDefinition) return "label";
    if (element instanceof GoPackageClause) return "package statement";

    // should be last
    if (element instanceof GoStatement) return "statement";
    if (element instanceof GoTopLevelDeclaration) return "declaration";
    if (element instanceof GoCommClause || element instanceof GoCaseClause) return "case";
    return "";
  }

  @NotNull
  @Override
  public String getDescriptiveName(@NotNull PsiElement element) {
    return ElementDescriptionUtil.getElementDescription(element, UsageViewLongNameLocation.INSTANCE);
  }

  @NotNull
  @Override
  public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
    return ElementDescriptionUtil.getElementDescription(element, UsageViewShortNameLocation.INSTANCE);
  }
}