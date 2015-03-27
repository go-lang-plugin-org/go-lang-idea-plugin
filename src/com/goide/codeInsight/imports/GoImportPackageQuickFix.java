/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader
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

import com.goide.GoConstants;
import com.goide.GoIcons;
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
import com.intellij.openapi.util.text.StringUtil;
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
          ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
              applyFix(packagesToImport, element.getContainingFile(), editor);
            }
          });
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
    return !isPerformed && file instanceof GoFile && file.getManager().isInProject(file) && myReference.resolve() == null
           && !myPackageName.endsWith(GoConstants.TEST_SUFFIX)
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
      Collection<GoFile> es = StubIndex.getElements(GoPackagesIndex.KEY, myPackageName, element.getProject(), scope, GoFile.class);
      myPackagesToImport = sorted(skipNulls(map2Set(
        es,
        new Function<GoFile, String>() {
          @Nullable
          @Override
          public String fun(@NotNull GoFile file) {
            return file.getImportPath();
          }
        }
      )), new MyImportsComparator());
    }
    return myPackagesToImport;
  }
  
  private void applyFix(@NotNull final Collection<String> packagesToImport, @NotNull final PsiFile file, @Nullable Editor editor) {
    isPerformed = true;
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
            final String selected = newArrayList(packagesToImport).get(i);
            new WriteCommandAction.Simple(file.getProject(), getFamilyName(), file) {
              @Override
              protected void run() throws Throwable {
                perform(file, selected);
              }
            }.execute();
          }
        }
      ).createPopup().showInBestPositionFor(editor);
    }
    else {
      perform(file, getFirstItem(packagesToImport));
    }
  }

  private static void perform(@NotNull PsiFile file, @Nullable String firstItem) {
    if (file instanceof GoFile && firstItem != null) {
      ((GoFile)file).addImport(firstItem, null);
    }
  }

  private static class MyImportsComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {
      int result = Comparing.compare(StringUtil.containsChar(s1, '.'), StringUtil.containsChar(s2, '.'));
      result = result == 0 ? Comparing.compare(StringUtil.containsChar(s1, '/'), StringUtil.containsChar(s2, '/')) : result;
      return result == 0 ? Comparing.compare(s1, s2) : result;
    }
  }
}
