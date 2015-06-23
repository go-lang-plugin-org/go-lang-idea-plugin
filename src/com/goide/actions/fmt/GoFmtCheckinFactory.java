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

package com.goide.actions.fmt;

import com.goide.psi.GoFile;
import com.intellij.execution.ExecutionException;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleUtilCore;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GoFmtCheckinFactory extends CheckinHandlerFactory {
  private static final String GO_FMT = "GO_FMT";

  @NotNull
  public CheckinHandler createHandler(@NotNull final CheckinProjectPanel panel, @NotNull CommitContext commitContext) {
    return new CheckinHandler() {
      @Override
      public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        final JCheckBox checkBox = new JCheckBox("Go fmt");
        return new RefreshableOnComponent() {
          @NotNull
          public JComponent getComponent() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(checkBox, BorderLayout.WEST);
            return panel;
          }

          public void refresh() {
          }

          public void saveState() {
            PropertiesComponent.getInstance(panel.getProject()).setValue(GO_FMT, Boolean.toString(checkBox.isSelected()));
          }

          public void restoreState() {
            checkBox.setSelected(enabled(panel));
          }
        };
      }

      @Override
      public ReturnResult beforeCheckin(@Nullable CommitExecutor executor, PairConsumer<Object, Object> additionalDataConsumer) {
        if (enabled(panel)) {
          FileDocumentManager.getInstance().saveAllDocuments();
          for (PsiFile file : getPsiFiles()) {
            try {
              VirtualFile virtualFile = file.getVirtualFile();
              new GoFmtFileAction().doSomething(virtualFile, ModuleUtilCore.findModuleForPsiElement(file), file.getProject(), "Go fmt", true);
            }
            catch (ExecutionException ignored) {
            }
          }
        }
        return super.beforeCheckin();
      }

      @NotNull
      private List<PsiFile> getPsiFiles() {
        Collection<VirtualFile> files = panel.getVirtualFiles();
        List<PsiFile> psiFiles = new ArrayList<PsiFile>();
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

  private static boolean enabled(@NotNull CheckinProjectPanel panel) {
    return PropertiesComponent.getInstance(panel.getProject()).getBoolean(GO_FMT, false);
  }
}