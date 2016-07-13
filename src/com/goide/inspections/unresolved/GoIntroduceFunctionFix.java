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

package com.goide.inspections.unresolved;

import com.goide.GoConstants;
import com.goide.GoDocumentationProvider;
import com.goide.project.GoVendoringUtil;
import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoTypeUtil;
import com.goide.refactor.GoRefactoringUtil;
import com.goide.util.GoPathScopeHelper;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateBuilderImpl;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.diagnostic.AttachmentFactory;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class GoIntroduceFunctionFix extends LocalQuickFixAndIntentionActionOnPsiElement implements HighPriorityAction {
  private final String myName;
  private static final String FAMILY_NAME = "Create function";

  public GoIntroduceFunctionFix(@NotNull PsiElement element, @NotNull String name) {
    super(element);
    myName = name;
  }

  @Override
  public void invoke(@NotNull Project project,
                     @NotNull PsiFile file,
                     @Nullable("is null when called from inspection") Editor editor,
                     @NotNull PsiElement startElement,
                     @NotNull PsiElement endElement) {
    if (editor == null) {
      LOG.error("Cannot run quick fix without editor: " + getClass().getSimpleName(),
                AttachmentFactory.createAttachment(file.getVirtualFile()));
      return;
    }

    if (!(startElement instanceof GoCallExpr)) {
      return;
    }

    GoCallExpr call = (GoCallExpr)startElement;
    List<GoExpression> args = call.getArgumentList().getExpressionList();
    GoType resultType = ContainerUtil.getFirstItem(GoTypeUtil.getExpectedTypes(call));
    GoFunctionDeclaration function = createFunctionDeclaration(file, myName, args, resultType);

    PsiElement anchor = PsiTreeUtil.getParentOfType(call, GoTopLevelDeclaration.class);
    if (anchor == null) return;

    function = (GoFunctionDeclaration)file.addAfter(function, anchor);
    if (function == null) return;
    function = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(function);
    if (function == null) return;

    TemplateBuilderImpl builder = new TemplateBuilderImpl(function);
    setupFunctionParameters(function, builder, args);
    setupFunctionResult(function, builder);
    GoBlock body = function.getBlock();
    builder.setEndVariableAfter(body == null || body.getLbrace() == null ? function : body.getLbrace());

    function = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(function);
    if (function == null) return;

    Template template = builder.buildTemplate();
    TextRange range = function.getTextRange();
    editor.getCaretModel().moveToOffset(range.getStartOffset());
    editor.getDocument().deleteString(range.getStartOffset(), range.getEndOffset());

    startTemplate(editor, template, project);
  }

  @NotNull
  private static String convertType(@NotNull final PsiFile file, @Nullable GoType type, final Map<String, GoImportSpec> importMap) {
    if (type == null) return GoConstants.INTERFACE_TYPE;
    final Module module = ModuleUtilCore.findModuleForPsiElement(file);
    final boolean vendoringEnabled = GoVendoringUtil.isVendoringEnabled(module);
    return GoDocumentationProvider.getTypePresentation(type, new Function<PsiElement, String>() {
      @Override
      public String fun(PsiElement element) {
        if (element instanceof GoTypeSpec) {
          GoTypeSpec spec = (GoTypeSpec)element;
          if (GoPsiImplUtil.builtin(spec)) return spec.getIdentifier().getText();

          GoFile typeFile = spec.getContainingFile();
          if (file.isEquivalentTo(typeFile) || GoUtil.inSamePackage(typeFile, file)) {
            return spec.getIdentifier().getText();
          }
          if (!spec.isPublic()) {
            return GoConstants.INTERFACE_TYPE;
          }

          GoPathScopeHelper scopeHelper = GoPathScopeHelper.fromReferenceFile(file.getProject(), module, file.getVirtualFile());
          boolean isAllowed = scopeHelper.couldBeReferenced(typeFile.getVirtualFile(), file.getVirtualFile());
          if (!isAllowed) return GoConstants.INTERFACE_TYPE;

          String importPath = typeFile.getImportPath(vendoringEnabled);

          GoImportSpec importSpec = importMap.get(importPath);
          String packageName = StringUtil.notNullize(typeFile.getPackageName());
          String qualifier = StringUtil.notNullize(GoPsiImplUtil.getImportQualifierToUseInFile(importSpec, packageName), packageName);
          
          // todo: add import package fix if getImportQualifierToUseInFile is null?
          return GoPsiImplUtil.getFqn(qualifier, spec.getIdentifier().getText());
        }
        return GoConstants.INTERFACE_TYPE;
      }
    });
  }

  private static void setupFunctionResult(GoFunctionDeclaration function, TemplateBuilderImpl builder) {
    GoSignature signature = function.getSignature();
    GoResult result = signature != null ? signature.getResult() : null;
    if (result != null && !result.isVoid()) {
      GoType resultType = result.getType();
      if (resultType instanceof GoTypeList) {
        for (GoType type : ((GoTypeList)resultType).getTypeList()) {
          builder.replaceElement(type, type.getText());
        }
      }
      else if (resultType != null) {
        builder.replaceElement(resultType, resultType.getText());
      }
    }
  }

  private static void setupFunctionParameters(@NotNull GoFunctionDeclaration function,
                                              @NotNull TemplateBuilderImpl builder,
                                              @NotNull List<GoExpression> args) {
    if (function.getSignature() == null) return;
    int i = 0;
    List<GoParameterDeclaration> parameterList = function.getSignature().getParameters().getParameterDeclarationList();
    for (GoParameterDeclaration parameterDeclaration : parameterList) {
      builder.replaceElement(parameterDeclaration.getType(), parameterDeclaration.getType().getText());
      for (GoParamDefinition parameter : parameterDeclaration.getParamDefinitionList()) {
        builder.replaceElement(parameter.getIdentifier(), GoRefactoringUtil.createParameterNameSuggestedExpression(args.get(i)));
        i++;
      }
    }
  }

  private static void startTemplate(@NotNull final Editor editor, @NotNull final Template template, @NotNull final Project project) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        if (project.isDisposed() || editor.isDisposed()) return;
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
          @Override
          public void run() {
            TemplateManager.getInstance(project).startTemplate(editor, template, null);
          }
        }, "Introduce function", null);
      }
    };
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      runnable.run();
    }
    else {
      ApplicationManager.getApplication().invokeLater(runnable);
    }
  }

  @NotNull
  private static GoFunctionDeclaration createFunctionDeclaration(@NotNull PsiFile file,
                                                                 @NotNull String name,
                                                                 @NotNull List<GoExpression> argsList,
                                                                 @Nullable GoType result) {
    List<String> argumentText = ContainerUtil.newSmartList();
    Map<String, GoImportSpec> importMap = ((GoFile)file).getImportedPackagesMap();
    for (GoExpression expression : argsList) {
      argumentText.add("i " + convertType(file, expression.getGoType(null), importMap));
    }

    String args = StringUtil.join(argumentText, ", ");
    String resultType = result instanceof GoTypeList ? "(" + result.getText() + ")" : result != null ? result.getText() : "";
    return GoElementFactory.createFunctionDeclarationFromText(file.getProject(), name, args, resultType);
  }

  @NotNull
  @Override
  public String getText() {
    return FAMILY_NAME + " " + myName;
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return FAMILY_NAME;
  }
}