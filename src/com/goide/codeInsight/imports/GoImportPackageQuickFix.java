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

package com.goide.codeInsight.imports;

import com.goide.GoIcons;
import com.goide.completion.GoCompletionUtil;
import com.goide.project.GoExcludedPathsSettings;
import com.goide.psi.GoFile;
import com.goide.psi.GoReferenceExpression;
import com.goide.psi.GoTypeReferenceExpression;
import com.goide.stubs.index.GoPackagesIndex;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.QuestionAction;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInspection.HintAction;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.util.Function;
import com.intellij.util.NotNullFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Comparator;

import static com.intellij.openapi.actionSystem.IdeActions.ACTION_SHOW_INTENTION_ACTIONS;
import static com.intellij.util.containers.ContainerUtil.*;

public class GoImportPackageQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement implements HintAction, HighPriorityAction {
  @NotNull private final String myPackageName;
  @NotNull private final TextRange myRangeInElement;
  @NotNull private final PsiReference myReference;
  @Nullable private Collection<String> myPackagesToImport;
  private boolean isPerformed = false;

  public GoImportPackageQuickFix(@NotNull PsiReference reference) {
    super(reference.getElement());
    myReference = reference;
    myPackageName = reference.getCanonicalText();
    myRangeInElement = reference.getRangeInElement();
  }

  @Override
  public boolean showHint(@NotNull final Editor editor) {
    final PsiElement element = getStartElement();
    if (element == null || !element.isValid()) {
      return false;
    }

    if (myReference.resolve() != null) return false;

    if (isPerformed) {
      return false;
    }

    if (!GoCodeInsightSettings.getInstance().isShowImportPopup()) {
      return false;
    }

    if (HintManager.getInstance().hasShownHintsThatWillHideByOtherHint(true)) {
      return false;
    }

    if (ApplicationManager.getApplication().isUnitTestMode()) {
      return false;
    }

    final Collection<String> packagesToImport = getPackagesToImport(element);
    if (packagesToImport.isEmpty()) {
      return false;
    }

    String shortcutText = KeymapUtil.getFirstKeyboardShortcutText(ActionManager.getInstance().getAction(ACTION_SHOW_INTENTION_ACTIONS));
    String message = getText(packagesToImport) + shortcutText;

    TextRange referenceRange = myRangeInElement.shiftRight(element.getTextRange().getStartOffset());
    HintManager.getInstance().showQuestionHint(
      editor,
      message,
      referenceRange.getStartOffset(),
      referenceRange.getEndOffset(),
      new QuestionAction() {
        @Override
        public boolean execute() {
          applyFix(packagesToImport, element.getContainingFile(), editor);
          return true;
        }
      }
    );
    return true;
  }

  @NotNull
  @Override
  public String getText() {
    PsiElement element = getStartElement();
    if (element != null) {
      return "Import " + getText(getPackagesToImport(element));
    }
    return "Import package";
  }

  @NotNull
  private static String getText(@NotNull Collection<String> packagesToImport) {
    return getFirstItem(packagesToImport, "") + "? " + (packagesToImport.size() > 1 ? "(multiple choices...) " : "");
  }

  @NotNull
  @Override
  public String getFamilyName() {
    return "Import package";
  }

  @Override
  public void invoke(@NotNull Project project, @NotNull PsiFile file, @Nullable("is null when called from inspection") Editor editor,
                     @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
    Collection<String> packagesToImport = getPackagesToImport(startElement);
    assert !packagesToImport.isEmpty();
    applyFix(packagesToImport, file, editor);
  }

  @Override
  public boolean isAvailable(@NotNull Project project,
                             @NotNull PsiFile file,
                             @NotNull PsiElement startElement,
                             @NotNull PsiElement endElement) {
    return !isPerformed && file instanceof GoFile && file.getManager().isInProject(file)
           && myReference.getElement().isValid() && myReference.resolve() == null
           && !getPackagesToImport(startElement).isEmpty() && notQualified(startElement);
  }

  private static boolean notQualified(@Nullable PsiElement startElement) {
    return
      startElement instanceof GoReferenceExpression && ((GoReferenceExpression)startElement).getQualifier() == null ||
      startElement instanceof GoTypeReferenceExpression && ((GoTypeReferenceExpression)startElement).getQualifier() == null;
  }

  @NotNull
  private Collection<String> getPackagesToImport(@NotNull PsiElement element) {
    if (myPackagesToImport == null) {
      final GlobalSearchScope scope = GoUtil.moduleScope(element);
      PsiFile file = element.getContainingFile();
      final PsiDirectory parentDirectory = file != null ? file.getParent() : null;
      Project project = element.getProject();
      final GoExcludedPathsSettings excludedSettings = GoExcludedPathsSettings.getInstance(project);
      Collection<GoFile> es = StubIndex.getElements(GoPackagesIndex.KEY, myPackageName, project, scope, GoFile.class);
      myPackagesToImport = sorted(skipNulls(map2Set(
        es,
        new Function<GoFile, String>() {
          @Nullable
          @Override
          public String fun(@NotNull GoFile file) {
            String importPath = parentDirectory == null || !parentDirectory.isEquivalentTo(file.getParent()) ? file.getImportPath() : null;
            return importPath != null && !excludedSettings.isExcluded(importPath) ? importPath : null;
          }
        }
      )), new MyImportsComparator(element));
    }
    return myPackagesToImport;
  }

  private void applyFix(@NotNull final Collection<String> packagesToImport, @NotNull final PsiFile file, @Nullable Editor editor) {
    if (packagesToImport.size() > 1 && editor != null) {
      final JBList list = new JBList(packagesToImport);
      list.installCellRenderer(new NotNullFunction<Object, JComponent>() {
        @NotNull
        @Override
        public JComponent fun(@NotNull Object o) {
          JBLabel label = new JBLabel(o.toString(), GoIcons.PACKAGE, SwingConstants.LEFT);
          label.setBorder(IdeBorderFactory.createEmptyBorder(2, 4, 2, 4));
          return label;
        }
      });
      JBPopupFactory.getInstance().createListPopupBuilder(list).setRequestFocus(true).setTitle("Package to import").setItemChoosenCallback(
        new Runnable() {
          @Override
          public void run() {
            final int i = list.getSelectedIndex();
            if (i < 0) return;
            perform(file, newArrayList(packagesToImport).get(i));
          }
        }
      ).createPopup().showInBestPositionFor(editor);
    }
    else {
      perform(file, getFirstItem(packagesToImport));
    }
  }

  private void perform(@NotNull final PsiFile file, @Nullable final String pathToImport) {
    if (file instanceof GoFile && pathToImport != null) {
      WriteCommandAction.runWriteCommandAction(file.getProject(), new Runnable() {
        @Override
        public void run() {
          if (!isAvailable()) return;
          isPerformed = true;
          ((GoFile)file).addImport(pathToImport, null);
        }
      });
    }
  }

  private static class MyImportsComparator implements Comparator<String> {
    @Nullable
    private final String myContextImportPath;

    public MyImportsComparator(@Nullable PsiElement context) {
      myContextImportPath = GoCompletionUtil.getContextImportPath(context);
    }

    @Override
    public int compare(@NotNull String s1, @NotNull String s2) {
      int result = Comparing.compare(GoCompletionUtil.calculatePackagePriority(s2, myContextImportPath),
                                     GoCompletionUtil.calculatePackagePriority(s1, myContextImportPath));
      return result != 0 ? result : Comparing.compare(s1, s2);
    }
  }
}
