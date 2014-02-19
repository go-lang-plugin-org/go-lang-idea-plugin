package com.goide.psi.impl;

import com.goide.GoIcons;
import com.goide.completion.GoCompletionContributor;
import com.goide.psi.*;
import com.goide.psi.impl.imports.GoImportReferenceSet;
import com.goide.stubs.index.GoMethodIndex;
import com.goide.util.SingleCharInsertHandler;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceOwner;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GoPsiImplUtil {
  private static class Lazy {
    private static final SingleCharInsertHandler DIR_INSERT_HANDLER = new SingleCharInsertHandler('/');
    private static final SingleCharInsertHandler PACKAGE_INSERT_HANDLER = new SingleCharInsertHandler('.');
  }

  @Nullable
  public static GoTypeReferenceExpression getQualifier(@NotNull GoTypeReferenceExpression o) {
    return PsiTreeUtil.getChildOfType(o, GoTypeReferenceExpression.class);
  }

  @Nullable
  public static PsiDirectory resolve(@NotNull GoImportString importString) {
    PsiReference[] references = importString.getReferences();
    for (PsiReference reference : references) {
      if (reference instanceof FileReferenceOwner) {
        PsiFileReference lastFileReference = ((FileReferenceOwner)reference).getLastFileReference();
        PsiElement result = lastFileReference != null ? lastFileReference.resolve() : null;
        return result instanceof PsiDirectory ? (PsiDirectory)result : null;
      }
    }
    return null;
  }

  @NotNull
  public static PsiReference getReference(@NotNull GoTypeReferenceExpression o) {
    return new GoTypeReference(o);
  }

  @NotNull
  public static PsiReference getReference(@NotNull GoLabelRef o) {
    return new GoLabelReference(o);
  }

  @NotNull
  public static GoReference getReference(@NotNull final GoReferenceExpression o) {
    return new GoReference(o);
  }

  @NotNull
  public static PsiReference[] getReferences(@NotNull GoImportString o) {
    if (o.getTextLength() < 2) return PsiReference.EMPTY_ARRAY;
    return new GoImportReferenceSet(o).getAllReferences();
  }

  @Nullable
  public static GoReferenceExpression getQualifier(@NotNull GoReferenceExpression o) {
    return PsiTreeUtil.getChildOfType(o, GoReferenceExpression.class);
  }

  @SuppressWarnings("UnusedParameters")
  public static boolean processDeclarations(@NotNull GoCompositeElement o,
                                            @NotNull PsiScopeProcessor processor,
                                            @NotNull ResolveState state,
                                            PsiElement lastParent,
                                            @NotNull PsiElement place) {
    boolean isAncestor = PsiTreeUtil.isAncestor(o, place, false);
    if (isAncestor) return GoCompositeElementImpl.precessDeclarationDefault(o, processor, state, lastParent, place);

    if (o instanceof GoBlock ||
        o instanceof GoIfStatement ||
        o instanceof GoSwitchStatement ||
        o instanceof GoForStatement ||
        o instanceof GoCommClause ||
        o instanceof GoFunctionLit ||
        o instanceof GoTypeCaseClause ||
        o instanceof GoExprCaseClause) {
      return processor.execute(o, state);
    }
    return GoCompositeElementImpl.precessDeclarationDefault(o, processor, state, lastParent, place);
  }

  @NotNull
  public static LookupElement createFunctionOrMethodLookupElement(@NotNull GoReceiverHolder f) {
    Icon icon = f instanceof GoMethodDeclaration || f instanceof GoMethodSpec ? GoIcons.METHOD : GoIcons.FUNCTION;
    GoSignature signature = f.getSignature();
    int paramsCount = 0;
    String resultText = "";
    String paramText = "";
    if (signature != null) {
      paramsCount = signature.getParameters().getParameterDeclarationList().size();
      GoResult result = signature.getResult();
      paramText = signature.getParameters().getText();
      if (result != null) resultText = result.getText();
    }

    InsertHandler<LookupElement> handler =
      paramsCount == 0 ? ParenthesesInsertHandler.NO_PARAMETERS : ParenthesesInsertHandler.WITH_PARAMETERS;
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder.create(f)
        .withIcon(icon)
        .withInsertHandler(handler)
        .withTypeText(resultText, true)
        .withPresentableText(f.getName() + paramText),
      GoCompletionContributor.FUNCTION_PRIORITY);
  }

  @NotNull
  public static LookupElement createTypeLookupElement(@NotNull GoTypeSpec t) {
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(t).withIcon(GoIcons.TYPE),
                                                 GoCompletionContributor.TYPE_PRIORITY);
  }

  @NotNull
  public static LookupElement createLabelLookupElement(@NotNull GoLabelDefinition l) {
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(l).withIcon(GoIcons.LABEL),
                                                 GoCompletionContributor.LABEL_PRIORITY);
  }

  @NotNull
  public static LookupElement createTypeConversionLookupElement(@NotNull GoTypeSpec t) {
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder.create(t).withInsertHandler(ParenthesesInsertHandler.WITH_PARAMETERS).withIcon(GoIcons.TYPE),
      GoCompletionContributor.TYPE_CONVERSION);
  }

  @NotNull
  public static LookupElement createVariableLikeLookupElement(@NotNull GoNamedElement v) {
    Icon icon = v instanceof GoVarDefinition ? GoIcons.VARIABLE :
                v instanceof GoParamDefinition ? GoIcons.PARAMETER :
                v instanceof GoFieldDefinition ? GoIcons.FIELD :
                v instanceof GoReceiver ? GoIcons.RECEIVER :
                v instanceof GoConstDefinition ? GoIcons.CONST :
                v instanceof GoAnonymousFieldDefinition ? GoIcons.FIELD :
                null;
    GoType type = v.getGoType();
    String text = getText(type);
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(v).withIcon(icon).withTypeText(text, true),
                                                 GoCompletionContributor.VAR_PRIORITY);
  }

  @NotNull
  public static LookupElement createPackageLookupElement(@NotNull String i, boolean forType) {
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder.create(i).withIcon(GoIcons.PACKAGE).withInsertHandler(forType ? Lazy.PACKAGE_INSERT_HANDLER : null),
      GoCompletionContributor.PACKAGE_PRIORITY);
  }

  @NotNull
  public static LookupElementBuilder createDirectoryLookupElement(@NotNull PsiDirectory dir) {
    int files = dir.getFiles().length;
    return LookupElementBuilder.create(dir).withIcon(GoIcons.PACKAGE).withInsertHandler(files == 0 ? Lazy.DIR_INSERT_HANDLER : null);
  }

  @Nullable
  public static GoType getGoType(@NotNull GoReceiver o) {
    return o.getType();
  }

  @Nullable
  public static GoType getGoType(@NotNull GoAnonymousFieldDefinition o) {
    return getType(o.getTypeReferenceExpression());
  }

  @Nullable
  public static PsiElement getIdentifier(@SuppressWarnings("UnusedParameters") @NotNull GoAnonymousFieldDefinition o) {
    return null;
  }

  @NotNull
  public static String getName(@NotNull GoAnonymousFieldDefinition o) {
    return o.getTypeReferenceExpression().getIdentifier().getText();
  }

  public static int getTextOffset(@NotNull GoAnonymousFieldDefinition o) {
    return o.getTypeReferenceExpression().getIdentifier().getTextOffset();
  }

  @Nullable
  public static GoTypeReferenceExpression getTypeReference(@Nullable GoType o) {
    if (o == null) return null;
    if (o instanceof GoChannelType) {
      return ((GoChannelType)o).getType().getTypeReferenceExpression();
    }
    return o.getTypeReferenceExpression();
  }

  @Nullable
  public static GoType getGoType(@NotNull GoExpression o) {
    if (o instanceof GoUnaryExpr) {
      GoExpression expression = ((GoUnaryExpr)o).getExpression();
      return expression != null ? getGoType(expression) : null;
    }
    else if (o instanceof GoCompositeLit) {
      GoTypeReferenceExpression expression = ((GoCompositeLit)o).getLiteralTypeExpr().getTypeReferenceExpression();
      return getType(expression);
    }
    else if (o instanceof GoBuiltinCallExpr) {
      String text = ((GoBuiltinCallExpr)o).getReferenceExpression().getText();
      if ("new".equals(text) || "make".equals(text)) {
        GoBuiltinArgs args = ((GoBuiltinCallExpr)o).getBuiltinArgs();
        GoType type = args != null ? args.getType() : null;
        if (type != null) {
          GoTypeReferenceExpression expression = getTypeReference(type);
          return getType(expression);
        }
      }
    }
    else if (o instanceof GoCallExpr) {
      GoExpression expression = ((GoCallExpr)o).getExpression();
      if (expression instanceof GoReferenceExpression) {
        PsiReference reference = expression.getReference();
        PsiElement resolve = reference != null ? reference.resolve() : null;
        if (resolve instanceof GoFunctionOrMethodDeclaration) {
          GoSignature signature = ((GoFunctionOrMethodDeclaration)resolve).getSignature();
          GoResult result = signature != null ? signature.getResult() : null;
          if (result != null) {
            GoType type = result.getType();
            if (type instanceof GoTypeList && ((GoTypeList)type).getTypeList().size() == 1) {
              return ((GoTypeList)type).getTypeList().get(0);
            }
            if (type != null) return type;
            final GoParameters parameters = result.getParameters();
            if (parameters != null) {
              GoType parametersType = parameters.getType();
              if (parametersType != null) return parametersType;
              List<GoType> composite = ContainerUtil.newArrayList();
              for (GoParameterDeclaration p : parameters.getParameterDeclarationList()) {
                composite.add(p.getType());
              }
              class MyGoTypeList extends LightElement implements GoTypeList {
                private final List<GoType> myTypes;

                public MyGoTypeList(@NotNull List<GoType> types) {
                  super(parameters.getManager(), parameters.getLanguage());
                  myTypes = types;
                }

                @NotNull
                @Override
                public List<GoType> getTypeList() {
                  return myTypes;
                }

                @Nullable
                @Override
                public GoTypeReferenceExpression getTypeReferenceExpression() {
                  return null;
                }

                @Nullable
                @Override
                public PsiElement getLparen() {
                  return null;
                }

                @Nullable
                @Override
                public PsiElement getRparen() {
                  return null;
                }

                @Override
                public String toString() {
                  return null;
                }
              }
              return new MyGoTypeList(composite);
            }
          }
        }
      }
    }
    else if (o instanceof GoReferenceExpression) {
      PsiReference reference = o.getReference();
      PsiElement resolve = reference != null ? reference.resolve() : null;
      if (resolve instanceof GoNamedElement) {
        return ((GoNamedElement)resolve).getGoType();
      }
    }
    else if (o instanceof GoParenthesesExpr) {
      GoExpression expression = ((GoParenthesesExpr)o).getExpression();
      return expression != null ? expression.getGoType() : null;
    }
    else if (o instanceof GoSelectorExpr) {
      GoExpression item = ContainerUtil.getLastItem(((GoSelectorExpr)o).getExpressionList());
      return item != null ? item.getGoType() : null;
    }
    else if (o instanceof GoIndexExpr) {
      GoExpression first = ContainerUtil.getFirstItem(((GoIndexExpr)o).getExpressionList());
      GoType type = first == null ? null : getGoType(first);
      if (type instanceof GoMapType) {
        List<GoType> list = ((GoMapType)type).getTypeList();
        if (list.size() == 2) {
          return list.get(1);
        }
      }
    }
    return null;
  }

  @Nullable
  private static GoType getType(@Nullable GoTypeReferenceExpression expression) {
    PsiReference reference = expression != null ? expression.getReference() : null;
    PsiElement resolve = reference != null ? reference.resolve() : null;
    if (resolve instanceof GoTypeSpec) return ((GoTypeSpec)resolve).getType();
    return null;
  }

  @Nullable
  public static GoType getGoType(@NotNull GoVarDefinition o) {
    PsiElement parent = o.getParent();
    if (parent instanceof GoShortVarDeclaration || parent instanceof GoRecvStatement) { // todo: range processing
      List<GoVarDefinition> defList = ((GoVarSpec)parent).getVarDefinitionList();
      int i = 0;
      for (GoVarDefinition d : defList) {
        if (d.equals(o)) break;
        i++;
      }
      List<GoExpression> exprs = ((GoVarSpec)parent).getExpressionList();
      if (exprs.size() == 1 && exprs.get(0) instanceof GoCallExpr) {
        GoExpression call = exprs.get(0);
        GoType type = call.getGoType();
        if (type instanceof GoTypeList) {
          if (((GoTypeList)type).getTypeList().size() > i) {
            return ((GoTypeList)type).getTypeList().get(i);
          }
        }
        return type;
      }
      if (exprs.size() <= i) return null;
      return exprs.get(i).getGoType();
    }
    return GoNamedElementImpl.getType(o);
  }

  @NotNull
  public static String getText(@Nullable PsiElement o) {
    return o == null ? "" : o.getText().replaceAll("\\s+", " ");
  }

  @NotNull
  public static List<GoMethodDeclaration> getMethods(@NotNull final GoTypeSpec o) {
    final PsiDirectory dir = o.getContainingFile().getOriginalFile().getParent();
    if (dir != null) {
      return CachedValuesManager.getCachedValue(o, new CachedValueProvider<List<GoMethodDeclaration>>() {
        @Nullable
        @Override
        public Result<List<GoMethodDeclaration>> compute() {
          return Result.create(calcMethods(o), dir);
        }
      });
    }
    return calcMethods(o);
  }

  @NotNull
  private static List<GoMethodDeclaration> calcMethods(@NotNull GoTypeSpec o) {
    PsiElement identifier = o.getIdentifier();
    PsiFile file = o.getContainingFile().getOriginalFile();
    if (file instanceof GoFile) {
      String packageName = ((GoFile)file).getPackageName();
      String typeName = identifier.getText();
      if (StringUtil.isEmpty(packageName) || StringUtil.isEmpty(typeName)) return Collections.emptyList();
      String key = packageName + "." + typeName;
      Project project = ((GoFile)file).getProject();
      PsiDirectory parent = file.getParent();
      GlobalSearchScope scope = parent == null ? GlobalSearchScope.allScope(project) : GlobalSearchScopesCore.directoryScope(parent, false);
      Collection<GoMethodDeclaration> declarations = GoMethodIndex.find(key, project, scope);
      return ContainerUtil.newArrayList(declarations);
    }
    return Collections.emptyList();
  }
}
