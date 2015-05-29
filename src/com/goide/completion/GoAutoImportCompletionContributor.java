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
import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoTypeReference;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.stubs.index.GoFunctionIndex;
import com.goide.stubs.index.GoTypesIndex;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.goide.completion.GoCompletionUtil.createPrefixMatcher;
import static com.goide.psi.impl.GoPsiImplUtil.prevDot;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class GoAutoImportCompletionContributor extends CompletionContributor {
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
  private static final InsertHandler<LookupElement> TYPE_CONVERSION_INSERT_HANDLER = new InsertHandler<LookupElement>() {
    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
      PsiElement element = item.getPsiElement();
      if (element instanceof GoNamedElement) {
        if (element instanceof GoTypeSpec) {
          GoCompletionUtil.getTypeConversionInsertHandler(((GoTypeSpec)element)).handleInsert(context, item);
        }
        autoImport(context, (GoNamedElement)element);
      }
    }
  };


  public GoAutoImportCompletionContributor() {
    extend(CompletionType.BASIC, inGoFile(), new CompletionProvider<CompletionParameters>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        PsiElement parent = position.getParent();
        if (prevDot(parent)) return;
        result = adjustMatcher(parameters, result, parent);

        final PsiFile file = parameters.getOriginalFile();
        if (!(file instanceof GoFile)) return;

        final Map<String, GoImportSpec> importedPackages = ((GoFile)file).getImportedPackagesMap();

        Project project = position.getProject();
        GlobalSearchScope scope = GoUtil.moduleScope(position);
        boolean isTesting = GoTestFinder.isTestFile(parameters.getOriginalFile()); 

        if (parent instanceof GoReferenceExpression && !GoPsiImplUtil.isUnaryBitAndExpression(parent)) {
          GoReferenceExpression qualifier = ((GoReferenceExpression)parent).getQualifier();
          if (qualifier == null || qualifier.getReference().resolve() == null) {
            for (String name : StubIndex.getInstance().getAllKeys(GoFunctionIndex.KEY, project)) {
              if (StringUtil.isCapitalized(name) && !GoTestFinder.isTestFunctionName(name) && !GoTestFinder.isBenchmarkFunctionName(name)) {
                for (GoFunctionDeclaration declaration : GoFunctionIndex.find(name, project, scope)) {
                  GoFile declarationFile = declaration.getContainingFile();
                  if (declarationFile == file) continue;
                  if (!allowed(declaration, isTesting)) continue;
                  
                  double priority = GoCompletionUtil.NOT_IMPORTED_FUNCTION_PRIORITY;
                  GoImportSpec existingImport = importedPackages.get(declarationFile.getImportPath());
                  String pkg = declarationFile.getPackageName();
                  if (existingImport != null) {
                    if (existingImport.isDot()) {
                      continue;
                    }
                    priority = GoCompletionUtil.FUNCTION_PRIORITY;
                    pkg = ObjectUtils.chooseNotNull(existingImport.getAlias(), pkg);
                  }
                  String lookupString = StringUtil.isNotEmpty(pkg) ? pkg + "." + name : name;
                  result.addElement(GoCompletionUtil.createFunctionOrMethodLookupElement(declaration, lookupString,
                                                                                         FUNC_INSERT_HANDLER, priority));
                }
              }
            }
          }
        }
        if (parent instanceof GoReferenceExpression || parent instanceof GoTypeReferenceExpression) {
          GoReferenceExpressionBase qualifier = ((GoReferenceExpressionBase)parent).getQualifier();
          if (qualifier == null || qualifier.getReference() == null || qualifier.getReference().resolve() == null) {
            boolean forTypes = parent instanceof GoTypeReferenceExpression;
            for (String name : StubIndex.getInstance().getAllKeys(GoTypesIndex.KEY, project)) {
              if (StringUtil.isCapitalized(name)) {
                for (GoTypeSpec declaration : GoTypesIndex.find(name, project, scope)) {
                  GoFile declarationFile = declaration.getContainingFile();
                  if (declarationFile == file) continue;
                  PsiReference reference = parent.getReference();
                  if (reference instanceof GoTypeReference && !((GoTypeReference)reference).allowed(declaration)) continue;
                  if (!allowed(declaration, isTesting)) continue;

                  double priority = forTypes ? GoCompletionUtil.NOT_IMPORTED_TYPE_PRIORITY : GoCompletionUtil.NOT_IMPORTED_TYPE_CONVERSION;
                  String importPath = declarationFile.getImportPath();
                  String pkg = declarationFile.getPackageName();
                  GoImportSpec existingImport = importedPackages.get(importPath);
                  if (existingImport != null) {
                    if (existingImport.isDot()) {
                      continue;
                    }
                    priority = forTypes ? GoCompletionUtil.TYPE_PRIORITY : GoCompletionUtil.TYPE_CONVERSION;
                    pkg = ObjectUtils.chooseNotNull(existingImport.getAlias(), pkg);
                  }
                  String lookupString = StringUtil.isNotEmpty(pkg) ? pkg + "." + name : name; 
                  if (forTypes) {
                    result.addElement(GoCompletionUtil.createTypeLookupElement(declaration, lookupString, TYPE_INSERT_HANDLER, 
                                                                               importPath, priority));
                  }
                  else {
                    result.addElement(GoCompletionUtil.createTypeConversionLookupElement(declaration, lookupString,
                                                                                         TYPE_CONVERSION_INSERT_HANDLER, importPath,
                                                                                         priority));
                  }
                }
              }
            }
          }
        }
      }

      private boolean allowed(@NotNull GoNamedElement declaration, boolean isTesting) {
        GoFile file = declaration.getContainingFile();
        if (!GoUtil.allowed(file)) return false;
        PsiDirectory directory = file.getContainingDirectory();
        if (directory != null) {
          VirtualFile vFile = directory.getVirtualFile();
          if (vFile.getPath().endsWith("go/doc/testdata")) return false;
        }
        
        if (!isTesting && GoTestFinder.isTestFile(file)) return false;
        String packageName = file.getPackageName();
        if (StringUtil.equals(packageName, GoConstants.MAIN)) return false;
        return true;
      }

      private CompletionResultSet adjustMatcher(@NotNull CompletionParameters parameters,
                                                @NotNull CompletionResultSet result,
                                                @NotNull PsiElement parent) {
        int startOffset = parent.getTextRange().getStartOffset();
        String newPrefix = parameters.getEditor().getDocument().getText(TextRange.create(startOffset, parameters.getOffset()));
        return result.withPrefixMatcher(createPrefixMatcher(newPrefix));
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

  private static PsiElementPattern.Capture<PsiElement> inGoFile() {
    return psiElement().inFile(psiElement(GoFile.class));
  }

  private static void autoImport(@NotNull InsertionContext context, @NotNull GoNamedElement element) {
    PsiFile file = context.getFile();
    if (!(file instanceof GoFile)) return;

    String fullPackageName = element.getContainingFile().getImportPath();
    if (StringUtil.isEmpty(fullPackageName)) return;
    
    GoImportSpec existingImport = ((GoFile)file).getImportedPackagesMap().get(fullPackageName);
    if (existingImport != null) return;
    
    PsiDocumentManager.getInstance(context.getProject()).commitDocument(context.getEditor().getDocument());
    ((GoFile)file).addImport(fullPackageName, null);
  }
}
