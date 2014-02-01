package com.goide.psi.impl;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.jpa.ql.QlFile;
import com.intellij.jpa.ql.QlTypes;
import com.intellij.jpa.ql.model.*;
import com.intellij.jpa.ql.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.persistence.util.JavaTypeInfo;
import com.intellij.persistence.util.PersistenceCommonUtil;
import com.intellij.pom.PomTarget;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.pom.references.PomService;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.meta.PsiPresentableMetaData;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import com.intellij.util.text.CaseInsensitiveStringHashingStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author ignatov
 */
public class QlReferenceImpl extends PsiPolyVariantReferenceBase<QlReferenceExpression> {
  @NotNull
  private static final ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase> MY_RESOLVER =
    new ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase>() {
      @NotNull
      @Override
      public ResolveResult[] resolve(@NotNull PsiPolyVariantReferenceBase psiPolyVariantReferenceBase, boolean incompleteCode) {
        return ((QlReferenceImpl)psiPolyVariantReferenceBase).resolveInner();
      }
    };
  private static final Key<Boolean> SYNTHETIC_ATTR = Key.create("SYNTHETIC_ATTR");

  private final static Set<String> ourFunctionsWithParens = ContainerUtil.newTroveSet(
    CaseInsensitiveStringHashingStrategy.INSTANCE,
    "key", "value", "entry", "cast", "extract", "elements", "indices", "length", "locate", "abs", "sqrt", "mod",
    "size", "index", "minelement", "maxelement", "minindex", "maxindex", "concat", "substring", "trim", "lower",
    "upper", "coalesce", "nullif",
    "column", "table", "function", "func", "operator", "sql"
  );

  private final static double ALIAS_PRIORITY = 5;
  private final static double ATTRIBUTE_PRIORITY = 4;
  private final static double SYNTHETIC_ATTRIBUTE_PRIORITY = 3.5;
  private final static double CLASS_PRIORITY = 3;
  private final static double KEYWORD_PRIORITY = 2;
  private final static double FUNCTION_PRIORITY = 1.5;
  private final static double PACKAGE_PRIORITY = 1;

  public QlReferenceImpl(@NotNull QlReferenceExpression element) {
    super(element, TextRange.from(element.getIdentifier().getStartOffsetInParent(), element.getIdentifier().getTextLength()));
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    myElement.getIdentifier().replace(
      ObjectUtils.assertNotNull(QlPsiElementFactory.createIdentifier(newElementName, myElement.getProject())));
    return myElement;
  }

  private boolean processResolveVariants(@NotNull final QlModel model, @NotNull final PsiScopeProcessor processor) {
    Project project = myElement.getProject();
    ResolveState state = ResolveState.initial();
    final QlExpression qualifier = myElement.getQualifier();
    final QlHqlWithClause with = PsiTreeUtil.getParentOfType(myElement, QlHqlWithClause.class);
    if (qualifier != null) {
      if (qualifier instanceof QlReferenceExpression) {
        return processQualifierExpression(project, (QlReferenceExpression)qualifier, processor, state, myElement);
      }
      else if (qualifier instanceof QlArrayItemExpression) {
        QlExpression expression = ((QlArrayItemExpression)qualifier).getLeft();
        int count = 1;
        while (expression instanceof QlArrayItemExpression) {
          expression = ((QlArrayItemExpression)expression).getLeft();
          count ++;
        }
        if (expression instanceof QlReferenceExpression) {
          QlElement element = resolveModelElement((QlReferenceExpression)expression);
          if (!(element instanceof QlAttribute)) return true;
          QlAttribute attribute = (QlAttribute)element;
          PsiType psiType = attribute.getPsiType();
          JavaTypeInfo typeInfo = PersistenceCommonUtil.getTypeInfo(psiType);
          while (-- count > 0 && typeInfo.containerType != null) {
            typeInfo = PersistenceCommonUtil.getTypeInfo(typeInfo.getValueType());
          }
          return typeInfo.containerType == null || processPsiTypeVariants(project, typeInfo.getValueType(), model, processor, state);
        }
        else {
          // noop
          return true;
        }
      }
      else if (qualifier instanceof QlKeyValueExpression) {
        final IElementType type = QlCompositeElementImpl.getFirstElementType(qualifier);
        QlElement element = resolveModelElement(((QlKeyValueExpression)qualifier).getReferenceExpression());
        if (!(element instanceof QlAttribute)) return true;
        QlAttribute attribute = (QlAttribute)element;
        PsiType psiType = attribute.getPsiType();
        JavaTypeInfo typeInfo = PersistenceCommonUtil.getTypeInfo(psiType);
        PsiType targetType;
        if (type == QlTypes.QL_KEY) {
          targetType = typeInfo.getKeyType();
        }
        else if (type == QlTypes.QL_VALUE) {
          targetType = typeInfo.getValueType();
        }
        else /*if (type == QlTypes.QL_ENTRY) */ {
          // noop
          return true;
        }
        return processPsiTypeVariants(project, targetType, model, processor, state);
      }
      else {
        return true;
      }
    }
    else if (model instanceof ScopeQlModel) {
      return processObjectAttributes(project, ((ScopeQlModel)model).getScopeEntity(), processor, state);
    }
    else if (with != null) {
      final PsiElement withParent = with.getParent();
      if (withParent instanceof QlJoinExpression) {
        final QlExpression refExpression = ((QlJoinExpression)withParent).getJoinArgument();

        if (refExpression instanceof QlAliasDefinition) {
          final QlIdentifier identifier = ((QlAliasDefinition)refExpression).getIdentifier();
          final String text = identifier.getText();
          if (text == null || !myElement.getIdentifier().getText().equals(text)) {
            return true;
          }
        }
      }
      return processUnqualifiedResolve(project, processor, model, state);
    }
    else {
      return processUnqualifiedResolve(project, processor, model, state);
    }
  }

  @Nullable
  public static QlElement resolveModelElement(final QlReferenceExpression refExpression) {
    Object o = resolveQualifier(refExpression);
    return o instanceof QlElement ? ((QlElement)o) : null;
  }

  @Nullable
  private static Object resolveQualifier(QlReferenceExpression refExpression) {
    if (refExpression == null) return null;
    Object refTarget = refExpression.resolve();
    if (refTarget instanceof QlAliasDefinition) {
      QlExpression aliasedExpression = ((QlAliasDefinition)refTarget).getExpression();

      if (aliasedExpression instanceof QlReferenceExpression) {
        refTarget = ((QlReferenceExpression)aliasedExpression).resolve();
      }
      else if (aliasedExpression instanceof QlInVariableExpression) {
        QlReferenceExpression refExpression2 = ((QlInVariableExpression)aliasedExpression).getReferenceExpression();
        refTarget = refExpression2 == null? null : refExpression2.resolve();
      }
      else {
        // noop
      }
    }
    return refTarget;
  }

  private static boolean processPsiTypeVariants(Project project,
                                                PsiType targetType,
                                                QlModel model,
                                                PsiScopeProcessor processor,
                                                ResolveState state) {
    for (QlEntity object : model.getEntities()) {
      PsiType type = object.getPsiType();
      if (type != null && type.equals(targetType)) {
        if (!processObjectAttributes(project, object, processor, state)) return false;
      }
    }
    return true;
  }

  public static boolean processQualifierExpression(Project project,
                                                   QlReferenceExpression qualifier,
                                                   PsiScopeProcessor processor,
                                                   ResolveState state, final PsiElement place) {
    Object qualifierTarget = qualifier == null? null : qualifier.resolve();
    if (qualifierTarget == qualifier) {
      // noop
    }
    else if (qualifierTarget instanceof QlEntity) {
      if (!processObjectAttributes(project, (QlEntity)qualifierTarget, processor, state)) return false;
      if (!processSpecialAttributes(project, (QlEntity)qualifierTarget, processor, state)) return false;
      PsiClass psiClass = PsiTypesUtil.getPsiClass(((QlEntity)qualifierTarget).getPsiType());
      if (psiClass != null && !processPsiClassMembers(psiClass, processor, state)) return false;
    }
    else if (qualifierTarget instanceof QlAttribute) {
      QlAttribute attribute = (QlAttribute)qualifierTarget;
      PsiElement parent = qualifier.getParent();

      QlEntity entity = attribute.getOtherEntity();
      boolean container = attribute.isToMany();

      if (!container || parent instanceof QlAliasDefinition || parent instanceof QlInVariableExpression) {
        if (!processObjectAttributes(project, entity, processor, state)) return false;
      }
      if (!processSpecialAttributes(project, (QlAttribute)qualifierTarget, processor, state)) return false;
    }
    else if (qualifierTarget instanceof PsiPackage) {
      if (!((PsiElement)qualifierTarget).processDeclarations(processor, state, place, place)) return false;
    }
    else if (qualifierTarget instanceof PsiClass) {
      PsiClass psiClass = (PsiClass)qualifierTarget;
      if (psiClass.isEnum()) {
        for (PsiField field : psiClass.getFields()) {
          if (!(field instanceof PsiEnumConstant)) continue;
          if (!processor.execute(field, state)) return false;
        }
      }
      else {
        if (!processPsiClassMembers(psiClass, processor, state)) return false;
      }
    }
    else if (qualifierTarget instanceof QlAliasDefinition) {
      QlExpression aliasedExpression = ((QlAliasDefinition)qualifierTarget).getExpression();
      if (aliasedExpression instanceof QlReferenceExpression && !aliasedExpression.equals(qualifier)) {
        return processQualifierExpression(project, (QlReferenceExpression)aliasedExpression, processor, state, place);
      }
      else if (aliasedExpression instanceof QlInVariableExpression) {
        return processQualifierExpression(project, ((QlInVariableExpression)aliasedExpression).getReferenceExpression(), processor, state, place);
      }
      else if (aliasedExpression instanceof QlTreatExpression) {
        return processQualifierExpression(project, ((QlTreatExpression)aliasedExpression).getReferenceExpression(), processor, state, place);        
      }
      else if (aliasedExpression instanceof QlTableExpression) {
        QlStringLiteral literal = ((QlTableExpression)aliasedExpression).getStringLiteral();
        if (literal == null) return true;
        QlModel model = getPersistenceModel(aliasedExpression);
        String name = StringUtil.unquoteString(literal.getText());
        if (!model.processInDbTable(name, processor, state)) return false;
      }
      else if (aliasedExpression instanceof QlQueryExpression) {
        QlSelectClause clause = ((QlQueryExpression)aliasedExpression).getSelectClause();
        if (clause == null) return true;
        List<QlExpression> list = clause.getExpressionList();
        for (QlExpression expression : list) {
          if (expression instanceof QlReferenceExpression) {
            Object resolve = ((QlReferenceExpression)expression).resolve();
            if (resolve instanceof QlAttribute) {
              PsiElement attributeTarget = PomService.convertToPsi(project, ((QlAttribute)resolve));
              if (!processor.execute(attributeTarget, state)) return false;
            }
            if (!processQualifierExpression(project, ((QlReferenceExpression)expression), processor, state, place)) return false;
          }
          if (!processor.execute(expression, state)) return false;          
        }
      }
      else {
        // noop
      }
    }
    return true;
  }

  private static boolean processPsiClassMembers(PsiClass psiClass, PsiScopeProcessor processor, ResolveState state) {
    for (PsiClass innerClass : psiClass.getAllInnerClasses()) {
      if (innerClass.isEnum() ||
          innerClass.hasModifierProperty(PsiModifier.PUBLIC) &&
          innerClass.hasModifierProperty(PsiModifier.STATIC)) {
        if (!processor.execute(innerClass, state)) return false;
      }
    }
    for (PsiField field : psiClass.getAllFields()) {
      if (field.hasModifierProperty(PsiModifier.PUBLIC) &&
          field.hasModifierProperty(PsiModifier.STATIC) &&
          field.hasModifierProperty(PsiModifier.FINAL)) {
        if (!processor.execute(field, state)) return false;
      }
    }
    return true;
  }

  private static boolean processSpecialAttributes(Project project, QlElement o, PsiScopeProcessor processor, ResolveState state) {
    ResolveState newState = state.put(SYNTHETIC_ATTR, Boolean.TRUE);
    for (QlAttribute attribute : o.getSpecialAttributes()) {
      PsiElement attributeTarget = PomService.convertToPsi(project, attribute);
      if (!processor.execute(attributeTarget, newState)) return false;
    }
    return true;
  }

  private static boolean processObjectAttributes(Project project, @Nullable QlEntity element, PsiScopeProcessor processor, ResolveState state) {
    if (element == null) return true;
    for (QlAttribute attribute : element.getAttributes()) {
      PsiElement attributeTarget = PomService.convertToPsi(project, attribute);
      if (!processor.execute(attributeTarget, state)) return false;
    }
    return true;
  }

  private boolean processUnqualifiedResolve(Project project, PsiScopeProcessor processor, QlModel model, ResolveState state) {
    PsiElement scope = PsiTreeUtil.getParentOfType(myElement, QlStatement.class, QlFile.class);
    if (!PsiTreeUtil.treeWalkUp(processor, myElement, scope, state)) return false;
    PsiElement parent = myElement.getParent();
    if (!(parent instanceof QlGroupByClause || parent instanceof QlOrderByClause)) {
      for (QlEntity object : model.getEntities()) {
        PsiElement element = PomService.convertToPsi(project, object);
        if (element == null) return true;
        if (!processor.execute(element, state)) return false;
      }

      JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);

      PsiPackage rootPackage = javaPsiFacade.findPackage("");
      return rootPackage == null || rootPackage.processDeclarations(processor, state, myElement, myElement);
    }
    return true;
  }

  @NotNull
  public static QlModel getPersistenceModel(@NotNull QlCompositeElement element) {
    PsiFile psiFile = element.getContainingFile();
    QlModel model = psiFile instanceof QlFile ? ((QlFile)psiFile).getQlModel() : null;
    return model == null ? QlModel.EMPTY_MODEL: model;
  }

  @NotNull
  private ResolveResult[] resolveInner() {
    final QlModel model = getPersistenceModel(myElement);
    if (model instanceof ScopeQlModel && ((ScopeQlModel)model).isEmptyModel()) {
      return new ResolveResult[] { new PsiElementResolveResult(myElement) };
    }

    final String identifierText = myElement.getIdentifier().getText();
    final Collection<ResolveResult> result = new OrderedSet<ResolveResult>();
    processResolveVariants(model, createResolveProcessor(model, identifierText, result, !(myElement.getParent() instanceof QlAliasDefinition)));
    return result.toArray(new ResolveResult[result.size()]);
  }

  public static BaseScopeProcessor createResolveProcessor(final QlModel model,
                                                          final String identifierText,
                                                          final Collection<ResolveResult> result,
                                                          final boolean notInAliasDef) {
    return new BaseScopeProcessor() {
      @Override
      public boolean execute(@NotNull PsiElement psiElement, ResolveState state) {
        QlElement qlElement = null;
        String name;
        boolean caseSensitive = true;
        if (psiElement instanceof PomTargetPsiElement) {
          PomTarget target = ((PomTargetPsiElement)psiElement).getTarget();
          if (target instanceof QlElement) {
            qlElement = (QlElement)target;
            name = ((QlElement)target).getPersistenceElementName();
          }
          else {
            return true;
          }
        }
        else if (psiElement instanceof QlAliasDefinition && notInAliasDef) {
          name = ((QlAliasDefinition)psiElement).getIdentifier().getText();
          caseSensitive = false;
        }
        else if (psiElement instanceof PsiNamedElement) {
          name = ((PsiNamedElement)psiElement).getName();
        }
        else {
          return true;
        }

        if (Comparing.equal(identifierText, name, caseSensitive)) {
          if (psiElement instanceof PsiClass) {
            qlElement = model.findEntityByName(((PsiClass)psiElement).getQualifiedName());
          }

          if (qlElement != null) {
            result.add(new QlResolveResult(qlElement));
          }
          else {
            result.add(new PsiElementResolveResult(psiElement));
          }
        }
        return true;
      }
    };
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    final ArrayList<LookupElement> variants = new ArrayList<LookupElement>();
    QlModel model = getPersistenceModel(myElement);
    processResolveVariants(model, getCompletionProcessor(variants));
    return ArrayUtil.toObjectArray(variants);
  }

  public static BaseScopeProcessor getCompletionProcessor(final Collection<LookupElement> variants) {
    return new BaseScopeProcessor() {
      @Override
      public boolean execute(@NotNull PsiElement psiElement, ResolveState state) {
        boolean isSynthetic = Boolean.TRUE.equals(state.get(SYNTHETIC_ATTR));
        if (psiElement instanceof PomTargetPsiElement) {
          PomTarget target = ((PomTargetPsiElement)psiElement).getTarget();

          if (target instanceof QlElement) {
            QlElement qlElement = (QlElement)target;

            String name = qlElement.getPersistenceElementName();
            Icon icon = qlElement.getIcon();
            double priority = isSynthetic ? SYNTHETIC_ATTRIBUTE_PRIORITY
                                          : target instanceof QlAttribute
                                            ? ATTRIBUTE_PRIORITY
                                            : CLASS_PRIORITY;
            PsiType type = qlElement.getPsiType();

            LookupElement item = PrioritizedLookupElement.withPriority(
              LookupElementBuilder.create(StringUtil.notNullize(name, "unnamed")).withIcon(icon).withTypeText(
                type == null ? null : type.getPresentableText()), priority);

            item = qlElement instanceof QlAttribute ? item : TailTypeDecorator.withTail(item, TailType.SPACE);
            variants.add(item);
          }
        }
        else if (psiElement instanceof PsiNamedElement) {
          double priority =
            psiElement instanceof PsiPackage ? PACKAGE_PRIORITY :
            psiElement instanceof QlAliasDefinition ? ALIAS_PRIORITY :
            CLASS_PRIORITY;

          Icon icon = getIcon(psiElement);
          variants.add(PrioritizedLookupElement.withPriority(
            LookupElementBuilder.create((PsiNamedElement)psiElement).withIcon(icon), priority));
        }
        return true;
      }
    };
  }

  @Nullable
  public static Icon getIcon(@NotNull PsiElement psiElement) {
    return psiElement instanceof PsiPresentableMetaData ? ((PsiPresentableMetaData)psiElement).getIcon() : psiElement.getIcon(0);
  }

  @Override
  @NotNull
  public final ResolveResult[] multiResolve(final boolean incompleteCode) {
    return ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, MY_RESOLVER, false, false);
  }

  @Override
  @Nullable
  public final PsiElement resolve() {
    final ResolveResult[] results = multiResolve(false);
    return results.length > 0 ? results[0].getElement() : null;
  }

  public static LookupElement createKeywordLookupItem(String keyword) {
    LookupElementBuilder builder = LookupElementBuilder.create(keyword.toLowerCase()).withCaseSensitivity(false).bold();
    if (ourFunctionsWithParens.contains(keyword)) {
      return TailTypeDecorator.withTail(PrioritizedLookupElement.withPriority(
        builder.withInsertHandler(ParenthesesInsertHandler.WITH_PARAMETERS).withTailText("()"), FUNCTION_PRIORITY), TailType.SPACE);
    }
    else {
      return TailTypeDecorator.withTail(PrioritizedLookupElement.withPriority(builder.withInsertHandler(new InsertHandler<LookupElement>() {
        @Override
        public void handleInsert(InsertionContext context, LookupElement item) {
          if (context.getStartOffset() == 0) return;
          char ch = context.getDocument().getCharsSequence().charAt(context.getStartOffset() - 1);
          if (ch == '.') context.getDocument().insertString(context.getStartOffset(), "fake ");
          else if (StringUtil.isJavaIdentifierPart(ch)) context.getDocument().insertString(context.getStartOffset(), " ");
        }
      }), KEYWORD_PRIORITY), TailType.SPACE);
    }
  }


  public static class QlResolveResult implements ResolveResult {

    private final QlElement myPersistentElement;

    public QlResolveResult(QlElement persistentElement) {
      myPersistentElement = persistentElement;
    }

    public QlElement getPersistentElement() {
      return myPersistentElement;
    }

    @Override
    public PsiElement getElement() {
      return myPersistentElement.getPsiElement();
    }

    @Override
    public boolean isValidResult() {
      return true;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      QlResolveResult result = (QlResolveResult)o;

      if (!myPersistentElement.equals(result.myPersistentElement)) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return myPersistentElement.hashCode();
    }
  }
}
