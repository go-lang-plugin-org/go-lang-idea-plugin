package com.goide.psi;

import com.goide.GoFileType;
import com.goide.GoLanguage;
import com.goide.GoTypes;
import com.goide.stubs.GoFileStub;
import com.goide.stubs.types.GoConstDefinitionStubElementType;
import com.goide.stubs.types.GoTypeSpecStubElementType;
import com.goide.stubs.types.GoVarDefinitionStubElementType;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.roots.impl.ProjectFileIndexImpl;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubTree;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ArrayFactory;
import com.intellij.util.PathUtil;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.FilteringIterator;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class GoFile extends PsiFileBase {
  private static final String MAIN_FUNCTION_NAME = "main";

  public GoFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, GoLanguage.INSTANCE);
  }

  private CachedValue<GoPackageClause> myPackage;
  private CachedValue<List<GoImportSpec>> myImportsValue;
  private CachedValue<List<GoFunctionDeclaration>> myFunctionsValue;
  private CachedValue<List<GoMethodDeclaration>> myMethodsValue;
  private CachedValue<List<GoTypeSpec>> myTypesValue;
  private CachedValue<List<GoVarDefinition>> myVarsValue;
  private CachedValue<List<GoConstDefinition>> myConstsValue;

  @Nullable
  public String getFullPackageName() {
    VirtualFile virtualFile = getOriginalFile().getVirtualFile();
    VirtualFile root = ProjectFileIndexImpl.SERVICE.getInstance(getProject()).getSourceRootForFile(virtualFile);
    if (root != null) {
      String fullPackageName = FileUtil.getRelativePath(root.getPath(), virtualFile.getPath(), '/');
      if (fullPackageName != null && StringUtil.containsChar(fullPackageName, '/')) {
        return PathUtil.getParentPath(fullPackageName);
      }
    }
    return null;
  }

  @Nullable
  public GoPackageClause getPackage() {
    if (myPackage == null) {
      myPackage = getCachedValueManager().createCachedValue(new CachedValueProvider<GoPackageClause>() {
        @Override
        public Result<GoPackageClause> compute() {
          List<GoPackageClause> packageClauses = calc(new Condition<PsiElement>() {
            @Override
            public boolean value(PsiElement e) {
              return e instanceof GoPackageClause;
            }
          });
          return Result.create(ContainerUtil.getFirstItem(packageClauses), GoFile.this);
        }
      }, false);
    }
    return myPackage.getValue();
  }
  
  @Nullable
  public GoImportList getImportList() {
    return findChildByClass(GoImportList.class);
  }

  @NotNull
  public List<GoFunctionDeclaration> getFunctions() {
    if (myFunctionsValue == null) {
      myFunctionsValue = getCachedValueManager().createCachedValue(new CachedValueProvider<List<GoFunctionDeclaration>>() {
        @Override
        public Result<List<GoFunctionDeclaration>> compute() {
          //noinspection unchecked
          List<GoFunctionDeclaration> calc = calc(FilteringIterator.instanceOf(GoFunctionDeclaration.class));
          return Result.create(calc, GoFile.this);
        }
      }, false);
    }
    return myFunctionsValue.getValue();
  }

  @NotNull
  public List<GoMethodDeclaration> getMethods() {
    if (myMethodsValue == null) {
      myMethodsValue = getCachedValueManager().createCachedValue(new CachedValueProvider<List<GoMethodDeclaration>>() {
        @Override
        public Result<List<GoMethodDeclaration>> compute() {
          //noinspection unchecked
          List<GoMethodDeclaration> calc = calc(FilteringIterator.instanceOf(GoMethodDeclaration.class));
          return Result.create(calc, GoFile.this);
        }
      }, false);
    }
    return myMethodsValue.getValue();
  }

  @NotNull
  public List<GoTypeSpec> getTypes() {
    StubElement<GoFile> stub = getStub();
    if (stub != null) return getChildrenByType(stub, GoTypes.TYPE_SPEC, GoTypeSpecStubElementType.ARRAY_FACTORY);

    if (myTypesValue == null) {
      myTypesValue = getCachedValueManager().createCachedValue(new CachedValueProvider<List<GoTypeSpec>>() {
        @Override
        public Result<List<GoTypeSpec>> compute() {
          return Result.create(calcTypes(), GoFile.this);
        }
      }, false);
    }
    return myTypesValue.getValue();
  }

  @NotNull
  public List<GoImportSpec> getImports() {
    if (myImportsValue == null) {
      myImportsValue = getCachedValueManager().createCachedValue(new CachedValueProvider<List<GoImportSpec>>() {
        @Override
        public Result<List<GoImportSpec>> compute() {
          return Result.create(calcImports(), GoFile.this);
        }
      }, false);
    }
    return myImportsValue.getValue();
  }

  public MultiMap<String, PsiElement> getImportMap() {
    MultiMap<String, PsiElement> map = MultiMap.create();
    for (GoImportSpec spec : getImports()) {
      GoImportString string = spec.getImportString();
      PsiElement identifier = spec.getIdentifier();
      if (identifier != null) {
        map.putValue(identifier.getText(), spec);
        continue;
      }
      if (spec.getDot() != null) {
        map.putValue(".", spec);
        continue;
      }
      String key = PathUtil.getFileName(StringUtil.unquoteString(string.getText()));
      if (!StringUtil.isEmpty(key)) {
        map.putValue(key, string);
      }
    }
    return map;
  }

  @NotNull
  public List<GoVarDefinition> getVars() {
    StubElement<GoFile> stub = getStub();
    if (stub != null) return getChildrenByType(stub, GoTypes.VAR_DEFINITION, GoVarDefinitionStubElementType.ARRAY_FACTORY);
    if (myVarsValue == null) {
      myVarsValue = getCachedValueManager().createCachedValue(new CachedValueProvider<List<GoVarDefinition>>() {
        @Override
        public Result<List<GoVarDefinition>> compute() {
          return Result.create(calcVars(), GoFile.this);
        }
      }, false);
    }
    return myVarsValue.getValue();
  }

  @NotNull
  public List<GoConstDefinition> getConsts() {
    StubElement<GoFile> stub = getStub();
    if (stub != null) return getChildrenByType(stub, GoTypes.CONST_DEFINITION, GoConstDefinitionStubElementType.ARRAY_FACTORY);
    if (myConstsValue == null) {
      myConstsValue = getCachedValueManager().createCachedValue(new CachedValueProvider<List<GoConstDefinition>>() {
        @Override
        public Result<List<GoConstDefinition>> compute() {
          return Result.create(calcConsts(), GoFile.this);
        }
      }, false);
    }
    return myConstsValue.getValue();
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
    final List<GoImportSpec> result = ContainerUtil.newArrayList();
    processChildrenDummyAware(this, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement e) {
        if (e instanceof GoImportList) {
          for (GoImportDeclaration declaration : ((GoImportList)e).getImportDeclarationList()) {
            for (GoImportSpec spec : declaration.getImportSpecList()) {
              result.add(spec);
            }
          }
        }
        return true;
      }
    });
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

  @NotNull
  private <T extends PsiElement> List<T> calc(final Condition<PsiElement> condition) {
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
  private CachedValuesManager getCachedValueManager() {
    return CachedValuesManager.getManager(getProject());
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return GoFileType.INSTANCE;
  }

  @Nullable
  public GoFunctionDeclaration findMainFunction() { // todo create a map for faster search
    List<GoFunctionDeclaration> functions = getFunctions();
    for (GoFunctionDeclaration function : functions) {
      if (MAIN_FUNCTION_NAME.equals(function.getName())) {
        return function;
      }
    }
    return null;
  }

  @Nullable
  public String getPackageName() {
    GoFileStub stub = getStub();
    if (stub != null) return stub.getPackageName();

    GoPackageClause packageClause = getPackage();
    if (packageClause != null) {
      PsiElement packageIdentifier = packageClause.getIdentifier();
      if (packageIdentifier != null) {
        return packageIdentifier.getText().trim();
      }
    }
    return null;
  }

  @Nullable
  @Override
  public GoFileStub getStub() {
    //noinspection unchecked
    return (GoFileStub)super.getStub();
  }

  private static boolean processChildrenDummyAware(GoFile file, final Processor<PsiElement> processor) {
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
      public boolean process(PsiElement psiElement) {
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

  private static <E extends PsiElement> List<E> getChildrenByType(StubElement<? extends PsiElement> stub,
                                                                  IElementType elementType,
                                                                  ArrayFactory<E> f) {
    return Arrays.asList(stub.getChildrenByType(elementType, f));
  }
}