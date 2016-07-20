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
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoTypeUtil;
import com.goide.refactor.GoRefactoringUtil;
import com.goide.util.GoPathScopeHelper;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.diagnostic.AttachmentFactory;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
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
    if (!(startElement instanceof GoCallExpr)) return;

    GoCallExpr call = (GoCallExpr)startElement;
    List<GoExpression> args = call.getArgumentList().getExpressionList();
    GoType resultType = ContainerUtil.getFirstItem(GoTypeUtil.getExpectedTypes(call));

    PsiElement anchor = PsiTreeUtil.findPrevParent(file, call);

    Template template = TemplateManager.getInstance(project).createTemplate("", "");
    template.addTextSegment("\nfunc " + myName);

    setupFunctionParameters(template, args, file);
    setupFunctionResult(template, resultType);

    template.addTextSegment(" {\n\t");
    template.addEndVariable();
    template.addTextSegment("\n}");

    int offset = anchor.getTextRange().getEndOffset();
    editor.getCaretModel().moveToOffset(offset);

    startTemplate(editor, template, project);
  }

  @NotNull
  private static String convertType(@NotNull PsiFile file, @Nullable GoType type, @NotNull Map<String, GoImportSpec> importMap) {
    if (type == null) return GoConstants.INTERFACE_TYPE;
    Module module = ModuleUtilCore.findModuleForPsiElement(file);
    boolean vendoringEnabled = GoVendoringUtil.isVendoringEnabled(module);
    return GoDocumentationProvider.getTypePresentation(type, element -> {
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
    });
  }

  private static void setupFunctionResult(@NotNull Template template, @Nullable GoType type) {
    if (type instanceof GoTypeList) {
      template.addTextSegment(" (");
      List<GoType> list = ((GoTypeList)type).getTypeList();
      for (int i = 0; i < list.size(); i++) {
        template.addVariable(new ConstantNode(list.get(i).getText()), true);
        if (i < list.size() - 1) template.addTextSegment(", ");
      }
      template.addTextSegment(")");
      return;
    }
    if (type != null) {
      template.addTextSegment(" ");
      template.addVariable(new ConstantNode(type.getText()), true);
    }
  }

  private static void setupFunctionParameters(@NotNull Template template,
                                              @NotNull List<GoExpression> args, PsiFile file) {
    Map<String, GoImportSpec> importMap = ((GoFile)file).getImportedPackagesMap();
    template.addTextSegment("(");
    for (int i = 0; i < args.size(); i++) {
      GoExpression e = args.get(i);
      template.addVariable(GoRefactoringUtil.createParameterNameSuggestedExpression(e), true);
      template.addTextSegment(" ");
      String type = convertType(file, e.getGoType(null), importMap);
      template.addVariable(new ConstantNode(type), true);
      if (i != args.size() - 1) template.addTextSegment(", ");
    }
    template.addTextSegment(")");
  }

  private static void startTemplate(@NotNull Editor editor, @NotNull Template template, @NotNull Project project) {
    Runnable runnable = () -> {
      if (project.isDisposed() || editor.isDisposed()) return;
      CommandProcessor.getInstance().executeCommand(project, () ->
        TemplateManager.getInstance(project).startTemplate(editor, template, null), "Introduce function", null);
    };
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      runnable.run();
    }
    else {
      ApplicationManager.getApplication().invokeLater(runnable);
    }
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