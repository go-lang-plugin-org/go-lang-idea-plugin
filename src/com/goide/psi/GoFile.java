package com.goide.psi;

import com.goide.GoFileType;
import com.goide.GoLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.Processor;
import com.intellij.util.containers.FilteringIterator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GoFile extends PsiFileBase {
  public GoFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, GoLanguage.INSTANCE);
  }

  private CachedValue<List<GoFunctionDeclaration>> myFunctionsValue;
  private CachedValue<List<GoMethodDeclaration>> myMethodsValue;
  private CachedValue<List<GoTypeSpec>> myTypesValue;
  private CachedValue<List<GoVarDefinition>> myVarsValue;
  //private CachedValue<List<GoConstSpec>> myConstsValue;

  @NotNull
  public List<GoFunctionDeclaration> getFunctions() {
    if (myFunctionsValue == null) {
      myFunctionsValue = getCachedValueManager().createCachedValue(new CachedValueProvider<List<GoFunctionDeclaration>>() {
        @Override
        public Result<List<GoFunctionDeclaration>> compute() {
          List<GoFunctionDeclaration> calc = calc(new Condition<PsiElement>() {
            @Override
            public boolean value(PsiElement e) {
              return e instanceof GoFunctionDeclaration && !(e instanceof GoMethodDeclaration);
            }
          });
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
  public List<GoVarDefinition> getVars() {
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
  private List<GoTypeSpec> calcTypes() {
    final List<GoTypeSpec> result = new ArrayList<GoTypeSpec>();
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
  private List<GoVarDefinition> calcVars() {
    final List<GoVarDefinition> result = new ArrayList<GoVarDefinition>();
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
  private <T extends PsiElement> List<T> calc(final Condition<PsiElement> condition) {
    final List<T> result = new ArrayList<T>();
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

  private static boolean processChildrenDummyAware(GoFile file, final Processor<PsiElement> processor) {
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
}
