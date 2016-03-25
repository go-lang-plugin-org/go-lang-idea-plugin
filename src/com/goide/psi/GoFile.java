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

package com.goide.psi;

import com.goide.GoConstants;
import com.goide.GoFileType;
import com.goide.GoLanguage;
import com.goide.GoTypes;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.sdk.GoPackageUtil;
import com.goide.sdk.GoSdkUtil;
import com.goide.stubs.GoConstSpecStub;
import com.goide.stubs.GoFileStub;
import com.goide.stubs.GoVarSpecStub;
import com.goide.stubs.types.*;
import com.goide.util.GoUtil;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubTree;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayFactory;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GoFile extends PsiFileBase {

  public GoFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, GoLanguage.INSTANCE);
  }

  @Nullable
  public String getImportPath(boolean withVendoring) {
    return GoSdkUtil.getImportPath(getParent(), withVendoring);
  }

  @NotNull
  @Override
  public GlobalSearchScope getResolveScope() {
    return GoUtil.goPathResolveScope(this);
  }

  @NotNull
  @Override
  public SearchScope getUseScope() {
    return GoUtil.goPathUseScope(this);
  }

  @Nullable
  public GoPackageClause getPackage() {
    return CachedValuesManager.getCachedValue(this, new CachedValueProvider<GoPackageClause>() {
      @Override
      public Result<GoPackageClause> compute() {
        GoPackageClause packageClauses = calcFirst(Conditions.<PsiElement>instanceOf(GoPackageClause.class));
        return Result.create(packageClauses, GoFile.this);
      }
    });
  }

  @Nullable
  public GoImportList getImportList() {
    return findChildByClass(GoImportList.class);
  }

  @Nullable
  public String getBuildFlags() {
    GoFileStub stub = getStub();
    if (stub != null) {
      return stub.getBuildFlags();
    }

    // https://code.google.com/p/go/source/browse/src/pkg/go/build/build.go?r=2449e85a115014c3d9251f86d499e5808141e6bc#790
    Collection<String> buildFlags = ContainerUtil.newArrayList();
    int buildFlagLength = GoConstants.BUILD_FLAG.length();
    for (PsiComment comment : getCommentsToConsider(this)) {
      String commentText = StringUtil.trimStart(comment.getText(), "//").trim();
      if (commentText.startsWith(GoConstants.BUILD_FLAG) && commentText.length() > buildFlagLength
          && StringUtil.isWhiteSpace(commentText.charAt(buildFlagLength))) {
        ContainerUtil.addIfNotNull(buildFlags, StringUtil.nullize(commentText.substring(buildFlagLength).trim(), true));
      }
    }
    return !buildFlags.isEmpty() ? StringUtil.join(buildFlags, "|") : null;
  }


  @NotNull
  public List<GoFunctionDeclaration> getFunctions() {
    return CachedValuesManager.getCachedValue(this, new CachedValueProvider<List<GoFunctionDeclaration>>() {
      @Override
      public Result<List<GoFunctionDeclaration>> compute() {
        GoFileStub stub = getStub();
        List<GoFunctionDeclaration> functions = stub != null ? getChildrenByType(stub, GoTypes.FUNCTION_DECLARATION,
                                                                                 GoFunctionDeclarationStubElementType.ARRAY_FACTORY)
                                                             : calc(Conditions.instanceOf(GoFunctionDeclaration.class));
        return Result.create(functions, GoFile.this);
      }
    });
  }

  @NotNull
  public List<GoMethodDeclaration> getMethods() {
    return CachedValuesManager.getCachedValue(this, new CachedValueProvider<List<GoMethodDeclaration>>() {
      @Override
      public Result<List<GoMethodDeclaration>> compute() {
        StubElement<GoFile> stub = getStub();
        List<GoMethodDeclaration> calc = stub != null
                                         ? getChildrenByType(stub, GoTypes.METHOD_DECLARATION, GoMethodDeclarationStubElementType.ARRAY_FACTORY)
                                         : calc(Conditions.instanceOf(GoMethodDeclaration.class));
        return Result.create(calc, GoFile.this);
      }
    });
  }

  @NotNull
  public List<GoTypeSpec> getTypes() {
    return CachedValuesManager.getCachedValue(this, new CachedValueProvider<List<GoTypeSpec>>() {
      @Override
      public Result<List<GoTypeSpec>> compute() {
        StubElement<GoFile> stub = getStub();
        List<GoTypeSpec> types = stub != null ? getChildrenByType(stub, GoTypes.TYPE_SPEC, GoTypeSpecStubElementType.ARRAY_FACTORY) 
                                              : calcTypes();
        return Result.create(types, GoFile.this);
      }
    });
  }

  @NotNull
  public List<GoImportSpec> getImports() {
    return CachedValuesManager.getCachedValue(this, new CachedValueProvider<List<GoImportSpec>>() {
      @Override
      public Result<List<GoImportSpec>> compute() {
        StubElement<GoFile> stub = getStub();
        List<GoImportSpec> imports = stub != null ? getChildrenByType(stub, GoTypes.IMPORT_SPEC, GoImportSpecStubElementType.ARRAY_FACTORY) 
                                                  : calcImports();
        return Result.create(imports, GoFile.this);
      }
    });
  }

  public GoImportSpec addImport(String path, String alias) {
    GoImportList importList = getImportList();
    if (importList != null) {
      return importList.addImport(path, alias);
    }
    return null;
  }

  /**
   * @return map like { import path -> import spec } for file
   */
  @NotNull
  public Map<String, GoImportSpec> getImportedPackagesMap() {
    return CachedValuesManager.getCachedValue(this, new CachedValueProvider<Map<String, GoImportSpec>>() {
      @Nullable
      @Override
      public Result<Map<String, GoImportSpec>> compute() {
        Map<String, GoImportSpec> map = ContainerUtil.newHashMap();
        for (GoImportSpec spec : getImports()) {
          if (!spec.isForSideEffects()) {
            String importPath = spec.getPath();
            if (StringUtil.isNotEmpty(importPath)) {
              map.put(importPath, spec);
            }
          }
        }
        return Result.create(map, GoFile.this);
      }
    });
  }

  /**
   * @return map like { local package name, maybe alias -> import spec } for file
   */
  @NotNull
  public MultiMap<String, GoImportSpec> getImportMap() {
    return CachedValuesManager.getCachedValue(this, new CachedValueProvider<MultiMap<String, GoImportSpec>>() {
      @Nullable
      @Override
      public Result<MultiMap<String, GoImportSpec>> compute() {
        MultiMap<String, GoImportSpec> map = MultiMap.createLinked();
        List<Object> dependencies = ContainerUtil.newArrayList(GoFile.this);
        for (GoImportSpec spec : getImports()) {
          String alias = spec.getAlias();
          if (alias != null) {
            map.putValue(alias, spec);
            continue;
          }
          if (spec.isDot()) {
            map.putValue(".", spec);
            continue;
          }
          GoImportString string = spec.getImportString();
          PsiDirectory dir = string.resolve();
          ContainerUtil.addIfNotNull(dependencies, dir);
          Collection<String> packagesInDirectory = GoPackageUtil.getAllPackagesInDirectory(dir, true);
          if (!packagesInDirectory.isEmpty()) {
            for (String packageNames : packagesInDirectory) {
              if (!StringUtil.isEmpty(packageNames)) {
                map.putValue(packageNames, spec);
              }
            }
          }
          else {
            String key = spec.getLocalPackageName();
            if (!StringUtil.isEmpty(key)) {
              map.putValue(key, spec);
            }
          }
        }
        return Result.create(map, ArrayUtil.toObjectArray(dependencies));
      }
    });
  }

  @NotNull
  public List<GoVarDefinition> getVars() {
    return CachedValuesManager.getCachedValue(this, new CachedValueProvider<List<GoVarDefinition>>() {
      @Override
      public Result<List<GoVarDefinition>> compute() {
        List<GoVarDefinition> result;
        StubElement<GoFile> stub = getStub();
        if (stub != null) {
          result = ContainerUtil.newArrayList();
          List<GoVarSpec> varSpecs = getChildrenByType(stub, GoTypes.VAR_SPEC, GoVarSpecStubElementType.ARRAY_FACTORY);
          for (GoVarSpec spec : varSpecs) {
            GoVarSpecStub specStub = spec.getStub();
            if (specStub == null) continue;
            result.addAll(getChildrenByType(specStub, GoTypes.VAR_DEFINITION, GoVarDefinitionStubElementType.ARRAY_FACTORY));
          }
        }
        else {
          result = calcVars();
        }
        return Result.create(result, GoFile.this);
      }
    });
  }

  @NotNull
  public List<GoConstDefinition> getConstants() {
    return CachedValuesManager.getCachedValue(this, new CachedValueProvider<List<GoConstDefinition>>() {
      @Override
      public Result<List<GoConstDefinition>> compute() {
        StubElement<GoFile> stub = getStub();
        List<GoConstDefinition> result;
        if (stub != null) {
          result = ContainerUtil.newArrayList();
          List<GoConstSpec> constSpecs = getChildrenByType(stub, GoTypes.CONST_SPEC, GoConstSpecStubElementType.ARRAY_FACTORY);
          for (GoConstSpec spec : constSpecs) {
            GoConstSpecStub specStub = spec.getStub();
            if (specStub == null) continue;
            result.addAll(getChildrenByType(specStub, GoTypes.CONST_DEFINITION, GoConstDefinitionStubElementType.ARRAY_FACTORY));
          }
        }
        else {
          result = calcConsts();
        }
        return Result.create(result, GoFile.this);
      }
    });
  }

  @NotNull
  private List<GoTypeSpec> calcTypes() {
    final List<GoTypeSpec> result = ContainerUtil.newArrayList();
    processChildrenDummyAware(this, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement e) {
        if (e instanceof GoTypeDeclaration) {
          for (GoTypeSpec spec : ((GoTypeDeclaration)e).getTypeSpecList()) {
            result.add(spec);
          }
        }
        return true;
      }
    });
    return result;
  }

  @NotNull
  private List<GoImportSpec> calcImports() {
    List<GoImportSpec> result = ContainerUtil.newArrayList();
    GoImportList list = getImportList();
    if (list == null) return ContainerUtil.emptyList();
    for (GoImportDeclaration declaration : list.getImportDeclarationList()) {
      for (GoImportSpec spec : declaration.getImportSpecList()) {
        result.add(spec);
      }
    }
    return result;
  }

  @NotNull
  private List<GoVarDefinition> calcVars() {
    final List<GoVarDefinition> result = ContainerUtil.newArrayList();
    processChildrenDummyAware(this, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement e) {
        if (e instanceof GoVarDeclaration) {
          for (GoVarSpec spec : ((GoVarDeclaration)e).getVarSpecList()) {
            for (GoVarDefinition def : spec.getVarDefinitionList()) {
              result.add(def);
            }
          }
        }
        return true;
      }
    });
    return result;
  }

  @NotNull
  private List<GoConstDefinition> calcConsts() {
    final List<GoConstDefinition> result = ContainerUtil.newArrayList();
    processChildrenDummyAware(this, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement e) {
        if (e instanceof GoConstDeclaration) {
          for (GoConstSpec spec : ((GoConstDeclaration)e).getConstSpecList()) {
            for (GoConstDefinition def : spec.getConstDefinitionList()) {
              result.add(def);
            }
          }
        }
        return true;
      }
    });
    return result;
  }

  @Nullable
  private <T extends PsiElement> T calcFirst(@NotNull final Condition<PsiElement> condition) {
    final Ref<T> result = Ref.create(null);
    processChildrenDummyAware(this, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement e) {
        if (condition.value(e)) {
          //noinspection unchecked
          result.set((T)e);
          return false;
        }
        return true;
      }
    });
    return result.get();
  }

  @NotNull
  private <T extends PsiElement> List<T> calc(@NotNull final Condition<PsiElement> condition) {
    final List<T> result = ContainerUtil.newArrayList();
    processChildrenDummyAware(this, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement e) {
        if (condition.value(e)) {
          //noinspection unchecked
          result.add((T)e);
        }
        return true;
      }
    });
    return result;
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return GoFileType.INSTANCE;
  }

  public boolean hasMainFunction() { // todo create a map for faster search
    List<GoFunctionDeclaration> functions = getFunctions();
    for (GoFunctionDeclaration function : functions) {
      if (GoConstants.MAIN.equals(function.getName())) {
        return true;
      }
    }
    return false;
  }

  @Nullable
  public String getPackageName() {
    GoFileStub stub = getStub();
    if (stub != null) return stub.getPackageName();
    GoPackageClause packageClause = getPackage();
    return packageClause != null ? packageClause.getName() : null;
  }

  public String getCanonicalPackageName() {
    String packageName = getPackageName();
    if (StringUtil.isNotEmpty(packageName) && GoTestFinder.isTestFile(this)) {
      return StringUtil.trimEnd(packageName, GoConstants.TEST_SUFFIX);
    }
    return packageName;
  }

  @Nullable
  @Override
  public GoFileStub getStub() {
    //noinspection unchecked
    return (GoFileStub)super.getStub();
  }

  public boolean hasCPathImport() {
    return getImportedPackagesMap().containsKey(GoConstants.C_PATH);
  }

  public void deleteImport(@NotNull GoImportSpec importSpec) {
    GoImportDeclaration importDeclaration = PsiTreeUtil.getParentOfType(importSpec, GoImportDeclaration.class);
    assert importDeclaration != null;
    PsiElement elementToDelete = importDeclaration.getImportSpecList().size() == 1 ? importDeclaration : importSpec;
    elementToDelete.delete();
  }

  private static boolean processChildrenDummyAware(@NotNull GoFile file, @NotNull final Processor<PsiElement> processor) {
    StubTree stubTree = file.getStubTree();
    if (stubTree != null) {
      List<StubElement<?>> plainList = stubTree.getPlainList();
      for (StubElement<?> stubElement : plainList) {
        PsiElement psi = stubElement.getPsi();
        if (!processor.process(psi)) return false;
      }
      return true;
    }
    return new Processor<PsiElement>() {
      @Override
      public boolean process(@NotNull PsiElement psiElement) {
        for (PsiElement child = psiElement.getFirstChild(); child != null; child = child.getNextSibling()) {
          if (child instanceof GeneratedParserUtilBase.DummyBlock) {
            if (!process(child)) return false;
          }
          else if (!processor.process(child)) return false;
        }
        return true;
      }
    }.process(file);
  }

  @NotNull
  private static <E extends PsiElement> List<E> getChildrenByType(@NotNull StubElement<? extends PsiElement> stub,
                                                                  IElementType elementType,
                                                                  ArrayFactory<E> f) {
    return Arrays.asList(stub.getChildrenByType(elementType, f));
  }

  @NotNull
  private static Collection<PsiComment> getCommentsToConsider(@NotNull GoFile file) {
    Collection<PsiComment> commentsToConsider = ContainerUtil.newArrayList();
    PsiElement child = file.getFirstChild();
    int lastEmptyLineOffset = 0;
    while (child != null) {
      if (child instanceof PsiComment) {
        commentsToConsider.add((PsiComment)child);
      }
      else if (child instanceof PsiWhiteSpace) {
        if (StringUtil.countChars(child.getText(), '\n') > 1) {
          lastEmptyLineOffset = child.getTextRange().getStartOffset();
        }
      }
      else {
        break;
      }
      child = child.getNextSibling();
    }
    final int finalLastEmptyLineOffset = lastEmptyLineOffset;
    return ContainerUtil.filter(commentsToConsider, new Condition<PsiComment>() {
      @Override
      public boolean value(PsiComment comment) {
        return comment.getTextRange().getStartOffset() < finalLastEmptyLineOffset;
      }
    });
  }
}