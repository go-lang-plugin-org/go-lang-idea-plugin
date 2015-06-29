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
import com.goide.stubs.index.GoAllPublicNamesIndex;
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
import com.intellij.util.Processor;
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
        PsiFile file = parameters.getOriginalFile();
        if (!(file instanceof GoFile)) return;
        if (!(parent instanceof GoReferenceExpressionBase)) return;
        GoReferenceExpressionBase qualifier = ((GoReferenceExpressionBase)parent).getQualifier();
        if (qualifier != null && qualifier.getReference() != null && qualifier.getReference().resolve() != null) return;
        
        boolean completeFunctions = parent instanceof GoReferenceExpression && !GoPsiImplUtil.isUnaryBitAndExpression(parent);
        boolean completeTypes = parent instanceof GoReferenceExpression || parent instanceof GoTypeReferenceExpression;
        
        if (!completeFunctions && !completeTypes) return;
        
        result = adjustMatcher(parameters, result, parent);
        Map<String, GoImportSpec> importedPackages = ((GoFile)file).getImportedPackagesMap();
        Project project = position.getProject();
        GlobalSearchScope scope = GoUtil.moduleScopeExceptContainingFile(file);
        boolean isTesting = GoTestFinder.isTestFile(file);
        boolean forTypes = parent instanceof GoTypeReferenceExpression;

        FunctionsProcessor functionProcessor = new FunctionsProcessor(isTesting, importedPackages, result);
        TypesProcessor typeProcessor = new TypesProcessor(parent, isTesting, forTypes, importedPackages, result);
        NamedElementProcessor processor = new NamedElementProcessor(completeFunctions, completeTypes, functionProcessor, typeProcessor);
        for (String name : StubIndex.getInstance().getAllKeys(GoAllPublicNamesIndex.ALL_PUBLIC_NAMES, project)) {
          processor.setName(name);
          StubIndex.getInstance().processElements(GoAllPublicNamesIndex.ALL_PUBLIC_NAMES, name, project, scope, GoNamedElement.class, processor);
        }
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

  private static boolean allowed(@NotNull GoNamedElement declaration, boolean isTesting) {
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

  private static class FunctionsProcessor implements Processor<GoFunctionDeclaration> {
    private final boolean myIsTesting;
    private final Map<String, GoImportSpec> myImportedPackages;
    private final CompletionResultSet myFinalResult;
    private String myName;

    public FunctionsProcessor(boolean isTesting, Map<String, GoImportSpec> importedPackages, CompletionResultSet finalResult) {
      myIsTesting = isTesting;
      myImportedPackages = importedPackages;
      myFinalResult = finalResult;
    }

    public void setName(@NotNull String name) {
      myName = name;
    }

    @Override
    public boolean process(GoFunctionDeclaration declaration) {
      if (GoTestFinder.isTestFunctionName(myName) || GoTestFinder.isBenchmarkFunctionName(myName) || 
          GoTestFinder.isExampleFunctionName(myName)) return true;
      if (!allowed(declaration, myIsTesting)) return true;

      double priority = GoCompletionUtil.NOT_IMPORTED_FUNCTION_PRIORITY;
      GoFile declarationFile = declaration.getContainingFile();
      GoImportSpec existingImport = myImportedPackages.get(declarationFile.getImportPath());
      String pkg = declarationFile.getPackageName();
      if (existingImport != null) {
        if (existingImport.isDot()) {
          return true;
        }
        priority = GoCompletionUtil.FUNCTION_PRIORITY;
        pkg = ObjectUtils.chooseNotNull(existingImport.getAlias(), pkg);
      }
      String lookupString = StringUtil.isNotEmpty(pkg) ? pkg + "." + myName : myName;
      myFinalResult.addElement(GoCompletionUtil.createFunctionOrMethodLookupElement(declaration, lookupString, FUNC_INSERT_HANDLER,
                                                                                    priority));
      return true;
    }
  }

  private static class TypesProcessor implements Processor<GoTypeSpec> {
    private final PsiElement myParent;
    private final boolean myIsTesting;
    private final boolean myForTypes;
    private final Map<String, GoImportSpec> myImportedPackages;
    private final CompletionResultSet myResult;
    private String myName;

    public TypesProcessor(PsiElement parent,
                          boolean isTesting,
                          boolean forTypes,
                          Map<String, GoImportSpec> importedPackages,
                          CompletionResultSet result) {
      myParent = parent;
      myIsTesting = isTesting;
      myForTypes = forTypes;
      myImportedPackages = importedPackages;
      myResult = result;
    }

    public void setName(@NotNull String name) {
      myName = name;
    }

    @Override
    public boolean process(GoTypeSpec spec) {
      PsiReference reference = myParent.getReference();
      if (reference instanceof GoTypeReference && !((GoTypeReference)reference).allowed(spec)) return true;
      if (!allowed(spec, myIsTesting)) return true;

      double priority = myForTypes ? GoCompletionUtil.NOT_IMPORTED_TYPE_PRIORITY : GoCompletionUtil.NOT_IMPORTED_TYPE_CONVERSION;
      GoFile declarationFile = spec.getContainingFile();
      String importPath = declarationFile.getImportPath();
      String pkg = declarationFile.getPackageName();
      GoImportSpec existingImport = myImportedPackages.get(importPath);
      if (existingImport != null) {
        if (existingImport.isDot()) {
          return true;
        }
        priority = myForTypes ? GoCompletionUtil.TYPE_PRIORITY : GoCompletionUtil.TYPE_CONVERSION;
        pkg = ObjectUtils.chooseNotNull(existingImport.getAlias(), pkg);
      }
      String lookupString = StringUtil.isNotEmpty(pkg) ? pkg + "." + myName : myName;
      if (myForTypes) {
        myResult.addElement(GoCompletionUtil.createTypeLookupElement(spec, lookupString, TYPE_INSERT_HANDLER, importPath, priority));
      }
      else {
        myResult.addElement(GoCompletionUtil.createTypeConversionLookupElement(spec, lookupString, TYPE_CONVERSION_INSERT_HANDLER,
                                                                               importPath, priority));
      }
      return true;
    }
  }

  private static class NamedElementProcessor implements Processor<GoNamedElement> {
    private final boolean myCompleteFunctions;
    private final boolean myCompleteTypes;
    private final FunctionsProcessor myFunctionProcessor;
    private final TypesProcessor myTypeProcessor;
    private String myName;

    public NamedElementProcessor(boolean completeFunctions,
                                 boolean completeTypes,
                                 FunctionsProcessor functionProcessor,
                                 TypesProcessor typeProcessor) {
      myCompleteFunctions = completeFunctions;
      myCompleteTypes = completeTypes;
      myFunctionProcessor = functionProcessor;
      myTypeProcessor = typeProcessor;
    }

    public void setName(@NotNull String name) {
      myName = name;
    }

    @Override
    public boolean process(GoNamedElement element) {
      if (myCompleteFunctions && element instanceof GoFunctionDeclaration) {
        myFunctionProcessor.setName(myName);
        return myFunctionProcessor.process(((GoFunctionDeclaration)element));
      }
      if (myCompleteTypes && element instanceof GoTypeSpec) {
        myTypeProcessor.setName(myName);
        return myTypeProcessor.process(((GoTypeSpec)element));
      }
      return true;
    }
  }
}
