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
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.sdk.GoPackageUtil;
import com.goide.sdk.GoSdkUtil;
import com.goide.stubs.GoConstSpecStub;
import com.goide.stubs.GoFileStub;
import com.goide.stubs.GoVarSpecStub;
import com.goide.stubs.types.*;
import com.goide.util.GoUtil;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayFactory;
import com.intellij.util.ArrayUtil;
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
    return GoUtil.goPathUseScope(this, true);
  }

  @Nullable
  public GoPackageClause getPackage() {
    return CachedValuesManager.getCachedValue(this, () -> {
      GoFileStub stub = getStub();
      if (stub != null) {
        StubElement<GoPackageClause> packageClauseStub = stub.getPackageClauseStub();
        return CachedValueProvider.Result.create(packageClauseStub != null ? packageClauseStub.getPsi() : null, this);
      }
      return CachedValueProvider.Result.create(findChildByClass(GoPackageClause.class), this);
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
    return CachedValuesManager.getCachedValue(this, () -> {
      GoFileStub stub = getStub();
      List<GoFunctionDeclaration> functions = stub != null 
                                              ? getChildrenByType(stub, GoTypes.FUNCTION_DECLARATION, GoFunctionDeclarationStubElementType.ARRAY_FACTORY)
                                              : GoPsiImplUtil.goTraverser().children(this).filter(GoFunctionDeclaration.class).toList();
      return CachedValueProvider.Result.create(functions, this);
    });
  }

  @NotNull
  public List<GoMethodDeclaration> getMethods() {
    return CachedValuesManager.getCachedValue(this, () -> {
      StubElement<GoFile> stub = getStub();
      List<GoMethodDeclaration> calc = stub != null
                                       ? getChildrenByType(stub, GoTypes.METHOD_DECLARATION, GoMethodDeclarationStubElementType.ARRAY_FACTORY)
                                       : GoPsiImplUtil.goTraverser().children(this).filter(GoMethodDeclaration.class).toList();
      return CachedValueProvider.Result.create(calc, this);
    });
  }

  @NotNull
  public List<GoTypeSpec> getTypes() {
    return CachedValuesManager.getCachedValue(this, () -> {
      StubElement<GoFile> stub = getStub();
      List<GoTypeSpec> types = stub != null ? getChildrenByType(stub, GoTypes.TYPE_SPEC, GoTypeSpecStubElementType.ARRAY_FACTORY) 
                                            : calcTypes();
      return CachedValueProvider.Result.create(types, this);
    });
  }

  @NotNull
  public List<GoImportSpec> getImports() {
    return CachedValuesManager.getCachedValue(this, () -> {
      StubElement<GoFile> stub = getStub();
      List<GoImportSpec> imports = stub != null ? getChildrenByType(stub, GoTypes.IMPORT_SPEC, GoImportSpecStubElementType.ARRAY_FACTORY) 
                                                : calcImports();
      return CachedValueProvider.Result.create(imports, this);
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
    return CachedValuesManager.getCachedValue(this, () -> {
      Map<String, GoImportSpec> map = ContainerUtil.newHashMap();
      for (GoImportSpec spec : getImports()) {
        if (!spec.isForSideEffects()) {
          String importPath = spec.getPath();
          if (StringUtil.isNotEmpty(importPath)) {
            map.put(importPath, spec);
          }
        }
      }
      return CachedValueProvider.Result.create(map, this);
    });
  }

  /**
   * @return map like { local package name, maybe alias -> import spec } for file
   */
  @NotNull
  public MultiMap<String, GoImportSpec> getImportMap() {
    return CachedValuesManager.getCachedValue(this, () -> {
      MultiMap<String, GoImportSpec> map = MultiMap.createLinked();
      List<Object> dependencies = ContainerUtil.newArrayList(this);
      Module module = ModuleUtilCore.findModuleForPsiElement(this);
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
        // todo[zolotov]: implement package modification tracker
        ContainerUtil.addIfNotNull(dependencies, dir);
        Collection<String> packagesInDirectory = GoPackageUtil.getAllPackagesInDirectory(dir, module, true);
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
      return CachedValueProvider.Result.create(map, ArrayUtil.toObjectArray(dependencies));
    });
  }

  @NotNull
  public List<GoVarDefinition> getVars() {
    return CachedValuesManager.getCachedValue(this, () -> {
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
      return CachedValueProvider.Result.create(result, this);
    });
  }

  @NotNull
  public List<GoConstDefinition> getConstants() {
    return CachedValuesManager.getCachedValue(this, () -> {
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
      return CachedValueProvider.Result.create(result, this);
    });
  }

  @NotNull
  private List<GoTypeSpec> calcTypes() {
    return GoPsiImplUtil.goTraverser().children(this).filter(GoTypeDeclaration.class).flatten(GoTypeDeclaration::getTypeSpecList).toList();
  }

  @NotNull
  private List<GoImportSpec> calcImports() {
    GoImportList list = getImportList();
    if (list == null) return ContainerUtil.emptyList();
    List<GoImportSpec> result = ContainerUtil.newArrayList();
    for (GoImportDeclaration declaration : list.getImportDeclarationList()) {
      result.addAll(declaration.getImportSpecList());
    }
    return result;
  }

  @NotNull
  private List<GoVarDefinition> calcVars() {
    return GoPsiImplUtil.goTraverser().children(this).filter(GoVarDeclaration.class)
      .flatten(GoVarDeclaration::getVarSpecList)
      .flatten(GoVarSpec::getVarDefinitionList).toList();
  }

  @NotNull
  private List<GoConstDefinition> calcConsts() {
    return GoPsiImplUtil.goTraverser().children(this).filter(GoConstDeclaration.class)
      .flatten(GoConstDeclaration::getConstSpecList)
      .flatten(GoConstSpec::getConstDefinitionList).toList();
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
    return CachedValuesManager.getCachedValue(this, () -> {
      GoFileStub stub = getStub();
      if (stub != null) {
        return CachedValueProvider.Result.create(stub.getPackageName(), this);
      }
      GoPackageClause packageClause = getPackage();
      return CachedValueProvider.Result.create(packageClause != null ? packageClause.getName() : null, this);
    });
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
    int finalLastEmptyLineOffset = lastEmptyLineOffset;
    return ContainerUtil.filter(commentsToConsider, comment -> comment.getTextRange().getStartOffset() < finalLastEmptyLineOffset);
  }
}