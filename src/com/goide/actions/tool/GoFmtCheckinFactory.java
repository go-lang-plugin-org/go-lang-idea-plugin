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

package com.goide.actions.tool;

import com.goide.psi.GoFile;
import com.intellij.CommonBundle;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.PairConsumer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;

public class GoFmtCheckinFactory extends CheckinHandlerFactory {
  private static final String GO_FMT = "GO_FMT";

  @Override
  @NotNull
  public CheckinHandler createHandler(@NotNull CheckinProjectPanel panel, @NotNull CommitContext commitContext) {
    return new CheckinHandler() {
      @Override
      public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        JCheckBox checkBox = new JCheckBox("Go fmt");
        return new RefreshableOnComponent() {
          @Override
          @NotNull
          public JComponent getComponent() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(checkBox, BorderLayout.WEST);
            return panel;
          }

          @Override
          public void refresh() {
          }

          @Override
          public void saveState() {
            PropertiesComponent.getInstance(panel.getProject()).setValue(GO_FMT, Boolean.toString(checkBox.isSelected()));
          }

          @Override
          public void restoreState() {
            checkBox.setSelected(enabled(panel));
          }
        };
      }

      @Override
      public ReturnResult beforeCheckin(@Nullable CommitExecutor executor, PairConsumer<Object, Object> additionalDataConsumer) {
        if (enabled(panel)) {
          Ref<Boolean> success = Ref.create(true);
          FileDocumentManager.getInstance().saveAllDocuments();
          for (PsiFile file : getPsiFiles()) {
            VirtualFile virtualFile = file.getVirtualFile();
            new GoFmtFileAction().doSomething(virtualFile, ModuleUtilCore.findModuleForPsiElement(file), file.getProject(), "Go fmt", true,
                                              result -> {
                                                if (!result) success.set(false);
                                              });
          }
          if (!success.get()) {
            return showErrorMessage(executor);
          }
        }
        return super.beforeCheckin();
      }

      @NotNull
      private ReturnResult showErrorMessage(@Nullable CommitExecutor executor) {
        String[] buttons = new String[]{"&Details...", commitButtonMessage(executor, panel), CommonBundle.getCancelButtonText()};
        int answer = Messages.showDialog(panel.getProject(),
                                         "<html><body>GoFmt returned non-zero code on some of the files.<br/>" +
                                         "Would you like to commit anyway?</body></html>\n",
                                         "Go Fmt", null, buttons, 0, 1, UIUtil.getWarningIcon());
        if (answer == Messages.OK) {
          return ReturnResult.CLOSE_WINDOW;
        }
        if (answer == Messages.NO) {
          return ReturnResult.COMMIT;
        }
        return ReturnResult.CANCEL;
      }

      @NotNull
      private List<PsiFile> getPsiFiles() {
        Collection<VirtualFile> files = panel.getVirtualFiles();
        List<PsiFile> psiFiles = ContainerUtil.newArrayList();
        PsiManager manager = PsiManager.getInstance(panel.getProject());
        for (VirtualFile file : files) {
          PsiFile psiFile = manager.findFile(file);
          if (psiFile instanceof GoFile) {
            psiFiles.add(psiFile);
          }
        }
        return psiFiles;
      }
    };
  }

  @NotNull
  private static String commitButtonMessage(@Nullable CommitExecutor executor, @NotNull CheckinProjectPanel panel) {
    return StringUtil.trimEnd(executor != null ? executor.getActionText() : panel.getCommitActionName(), "...");
  }

  private static boolean enabled(@NotNull CheckinProjectPanel panel) {
    return PropertiesComponent.getInstance(panel.getProject()).getBoolean(GO_FMT, false);
  }
}