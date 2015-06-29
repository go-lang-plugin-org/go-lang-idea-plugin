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
import com.goide.util.GoUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.goide.completion.GoCompletionUtil.createPrefixMatcher;
import static com.goide.psi.impl.GoPsiImplUtil.prevDot;
import static com.goide.stubs.index.GoAllPublicNamesIndex.ALL_PUBLIC_NAMES;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class GoAutoImportCompletionContributor extends CompletionContributor {
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

        final ArrayList<ElementProcessor> processors = ContainerUtil.newArrayList();
        if (parent instanceof GoReferenceExpression && !GoPsiImplUtil.isUnaryBitAndExpression(parent)) {
          processors.add(new FunctionsProcessor());
        }
        if (parent instanceof GoReferenceExpression || parent instanceof GoTypeReferenceExpression) {
          processors.add(new TypesProcessor(parent));
        }
        if (processors.isEmpty()) return;

        result = adjustMatcher(parameters, result, parent);
        Map<String, GoImportSpec> importedPackages = ((GoFile)file).getImportedPackagesMap();
        NamedElementProcessor processor = new NamedElementProcessor(processors, importedPackages, GoTestFinder.isTestFile(file), result);
        Project project = position.getProject();
        GlobalSearchScope scope = GoUtil.moduleScopeExceptContainingFile(file);
        PrefixMatcher matcher = result.getPrefixMatcher();
        Set<String> sortedKeys = sortMatching(matcher, StubIndex.getInstance().getAllKeys(ALL_PUBLIC_NAMES, project), ((GoFile)file));
        for (String name : sortedKeys) {
          processor.setName(name);
          StubIndex.getInstance().processElements(ALL_PUBLIC_NAMES, name, project, scope, GoNamedElement.class, processor);
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

  private static Set<String> sortMatching(@NotNull PrefixMatcher matcher, @NotNull Collection<String> names, @NotNull GoFile file) {
    ProgressManager.checkCanceled();
    if (matcher.getPrefix().isEmpty()) return ContainerUtil.newLinkedHashSet(names);
    
    Set<String> packagesWithAliases = ContainerUtil.newHashSet();
    for (Map.Entry<String, Collection<GoImportSpec>> entry : file.getImportMap().entrySet()) {
      for (GoImportSpec spec : entry.getValue()) {
        String alias = spec.getAlias();
        if (spec.isDot() || alias != null) {
          packagesWithAliases.add(entry.getKey());
          break;
        }
      }
    }

    List<String> sorted = new ArrayList<String>();
    for (String name : names) {
      if (matcher.prefixMatches(name) || packagesWithAliases.contains(substringBefore(name, '.'))) {
        sorted.add(name);
      }
    }

    ProgressManager.checkCanceled();
    Collections.sort(sorted, String.CASE_INSENSITIVE_ORDER);
    ProgressManager.checkCanceled();

    LinkedHashSet<String> result = new LinkedHashSet<String>();
    for (String name : sorted) {
      if (matcher.isStartMatch(name)) {
        result.add(name);
      }
    }

    ProgressManager.checkCanceled();

    result.addAll(sorted);
    return result;
  }

  private static PsiElementPattern.Capture<PsiElement> inGoFile() {
    return psiElement().inFile(psiElement(GoFile.class));
  }

  @NotNull
  private static String substringBefore(@NotNull String s, char c) {
    int i = s.indexOf(c);
    if (i == -1) return s;
    return s.substring(0, i);
  }
  
  private static String substringAfter(@NotNull String s, char c) {
    int i = s.indexOf(c);
    if (i == -1) return "";
    return s.substring(i + 1);
  }
  
  private static boolean allowed(@NotNull GoNamedElement element, boolean isTesting) {
    GoFile file = element.getContainingFile();
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

  private interface ElementProcessor {
    boolean process(@NotNull String name,
                    @NotNull GoNamedElement element,
                    @NotNull ExistingImportData importData,
                    @NotNull CompletionResultSet result);

    boolean isMine(@NotNull String name, @NotNull GoNamedElement element);
  }
  
  private static class FunctionsProcessor implements ElementProcessor {
    @Override
    public boolean process(@NotNull String name,
                           @NotNull GoNamedElement element,
                           @NotNull ExistingImportData importData,
                           @NotNull CompletionResultSet result) {
      GoFunctionDeclaration function = ((GoFunctionDeclaration)element);
      double priority = importData.exists ? GoCompletionUtil.FUNCTION_PRIORITY : GoCompletionUtil.NOT_IMPORTED_FUNCTION_PRIORITY;
      String lookupString = importData.alias != null ? importData.alias + "." + substringAfter(name, '.') : name;
      result.addElement(GoCompletionUtil.createFunctionOrMethodLookupElement(function, lookupString,
                                                                             GoAutoImportInsertHandler.FUNCTION_INSERT_HANDLER, priority));
      return true;
    }

    @Override
    public boolean isMine(@NotNull String name, @NotNull GoNamedElement element) {
      if (element instanceof GoFunctionDeclaration) {
        String functionName = substringAfter(name, '.');
        return !GoTestFinder.isTestFunctionName(functionName) && !GoTestFinder.isBenchmarkFunctionName(functionName) &&
               !GoTestFinder.isExampleFunctionName(functionName);
      }
      return false;
    }
  }

  private static class TypesProcessor implements ElementProcessor {
    private final PsiElement myParent;

    public TypesProcessor(@Nullable PsiElement parent) {
      myParent = parent;
    }

    @Override
    public boolean process(@NotNull String name,
                           @NotNull GoNamedElement element,
                           @NotNull ExistingImportData importData,
                           @NotNull CompletionResultSet result) {
      GoTypeSpec spec = ((GoTypeSpec)element);
      boolean forTypes = myParent instanceof GoTypeReferenceExpression;
      double priority;
      if (importData.exists) {
        priority = forTypes ? GoCompletionUtil.TYPE_PRIORITY : GoCompletionUtil.TYPE_CONVERSION;
      }
      else {
        priority = forTypes ? GoCompletionUtil.NOT_IMPORTED_TYPE_PRIORITY : GoCompletionUtil.NOT_IMPORTED_TYPE_CONVERSION;
      }

      String lookupString = importData.alias != null ? importData.alias + "." + substringAfter(name, '.') : name;
      if (forTypes) {
        result.addElement(GoCompletionUtil.createTypeLookupElement(spec, lookupString, GoAutoImportInsertHandler.TYPE_INSERT_HANDLER,
                                                                   importData.importPath, priority));
      }
      else {
        result.addElement(GoCompletionUtil.createTypeConversionLookupElement(spec, lookupString,
                                                                             GoAutoImportInsertHandler.TYPE_CONVERSION_INSERT_HANDLER,
                                                                             importData.importPath, priority));
      }
      return true;
    }

    @Override
    public boolean isMine(@NotNull String name, @NotNull GoNamedElement element) {
      if (myParent != null && element instanceof GoTypeSpec) {
        PsiReference reference = myParent.getReference();
        return !(reference instanceof GoTypeReference) || ((GoTypeReference)reference).allowed((GoTypeSpec)element);
      }
      return false;
    }
  }

  private static class NamedElementProcessor implements Processor<GoNamedElement> {
    @NotNull private final Collection<ElementProcessor> myProcessors;
    private final boolean myTesting;
    @NotNull private final CompletionResultSet myResult;
    @NotNull private String myName = "";
    @NotNull private final Map<String, GoImportSpec> myImportedPackages;

    public NamedElementProcessor(@NotNull Collection<ElementProcessor> processors,
                                 @NotNull Map<String, GoImportSpec> packages,
                                 boolean isTesting,
                                 @NotNull CompletionResultSet result) {
      myProcessors = processors;
      myImportedPackages = packages;
      myTesting = isTesting;
      myResult = result;
    }

    public void setName(@NotNull String name) {
      myName = name;
    }

    @Override
    public boolean process(GoNamedElement element) {
      ProgressManager.checkCanceled();
      Boolean allowed = null;
      ExistingImportData importData = null;
      for (ElementProcessor processor : myProcessors) {
        if (processor.isMine(myName, element)) {
          allowed = cachedAllowed(element, allowed);
          importData = cachedImportData(element, importData);
          if (allowed == Boolean.FALSE || importData.isDot) break;
          if (!processor.process(myName, element, importData, myResult)) {
            return false;
          }
        }
      }
      return true;
    }

    private Boolean cachedAllowed(@NotNull GoNamedElement element, @Nullable Boolean existingValue) {
      if (existingValue != null) return existingValue;
      return allowed(element, myTesting);
    }

    private ExistingImportData cachedImportData(@NotNull GoNamedElement element, @Nullable ExistingImportData existingValue) {
      if (existingValue != null) return existingValue;
      
      GoFile declarationFile = element.getContainingFile();
      String importPath = declarationFile.getImportPath();
      GoImportSpec existingImport = myImportedPackages.get(importPath);
      
      boolean exists = existingImport != null;
      boolean isDot = exists && existingImport.isDot();
      String alias = existingImport != null ? existingImport.getAlias() : null;
      return new ExistingImportData(exists, isDot, alias, importPath);
    }
  }

  private static class ExistingImportData {
    public final boolean exists;
    public final boolean isDot;
    public final String alias;
    public final String importPath;

    private ExistingImportData(boolean exists, boolean isDot, String packageName, String importPath) {
      this.exists = exists;
      this.isDot = isDot;
      this.alias = packageName;
      this.importPath = importPath;
    }
  }
}
