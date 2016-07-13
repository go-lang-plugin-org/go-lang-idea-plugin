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

package com.goide.runconfig.testing.frameworks.gotest;

import com.goide.GoConstants;
import com.goide.generate.GoGenerateTestActionBase;
import com.goide.psi.GoFile;
import com.goide.psi.GoImportSpec;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.runconfig.testing.GoTestFunctionType;
import com.goide.template.GoLiveTemplateContextType;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.text.UniqueNameGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class GotestGenerateAction extends GoGenerateTestActionBase {
  public GotestGenerateAction(@NotNull GoTestFunctionType type) {
    super(GotestFramework.INSTANCE, new GenerateActionHandler(type));
    String presentationName = StringUtil.capitalize(type.toString().toLowerCase(Locale.US));
    Presentation presentation = getTemplatePresentation();
    presentation.setText(presentationName);
    presentation.setDescription("Generate " + presentationName + " function");
  }

  @Override
  protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
    GoLiveTemplateContextType.File fileContextType =
      TemplateContextType.EP_NAME.findExtension(GoLiveTemplateContextType.File.class);
    return fileContextType != null && fileContextType.isInContext(file, editor.getCaretModel().getOffset());
  }

  @NotNull
  public static String importTestingPackageIfNeeded(@NotNull GoFile file) {
    GoImportSpec alreadyImportedPackage = file.getImportedPackagesMap().get(GoConstants.TESTING_PATH);
    String qualifier = GoPsiImplUtil.getImportQualifierToUseInFile(alreadyImportedPackage, GoConstants.TESTING_PATH);
    if (qualifier != null) {
      return qualifier;
    }

    String localName = UniqueNameGenerator.generateUniqueName(GoConstants.TESTING_PATH, file.getImportMap().keySet());
    file.addImport(GoConstants.TESTING_PATH, !GoConstants.TESTING_PATH.equals(localName) ? localName : null);
    return localName;
  }

  private static class GenerateActionHandler implements CodeInsightActionHandler {

    @NotNull private final GoTestFunctionType myType;

    public GenerateActionHandler(@NotNull GoTestFunctionType type) {
      myType = type;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
      if (!(file instanceof GoFile)) {
        return;
      }
      String testingQualifier = null;
      if (myType.getParamType() != null) {
        testingQualifier = importTestingPackageIfNeeded((GoFile)file);
        PsiDocumentManager.getInstance(file.getProject()).doPostponedOperationsAndUnblockDocument(editor.getDocument());
      }
      String functionText = "func " + myType.getPrefix();
      int offset = functionText.length();
      functionText += "(" + myType.getSignature(testingQualifier) + ") {\n\t\n}";
      EditorModificationUtil.insertStringAtCaret(editor, functionText, false, offset);
      AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
    }

    @Override
    public boolean startInWriteAction() {
      return true;
    }
  }
}
