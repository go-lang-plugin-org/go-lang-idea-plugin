package com.goide.actions.fmt;

import com.intellij.execution.ExecutionException;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.PairConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GoFmtCheckinFactory extends CheckinHandlerFactory {
   public static final String GO_FMT = "GO_FMT";
 
   @NotNull
   public CheckinHandler createHandler(final CheckinProjectPanel panel, CommitContext commitContext) {
     return new CheckinHandler() {
       @Override
       public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
         final JCheckBox checkBox = new JCheckBox("Go fmt");
         return new RefreshableOnComponent() {
           public JComponent getComponent() {
             final JPanel panel = new JPanel(new BorderLayout());
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
               GoFmtFileAction.doFmt(file, file.getProject(), file.getVirtualFile(), null);
             }
             catch (ExecutionException ignored) {
             }
           }
         }
         return super.beforeCheckin();
       }
 
       private PsiFile[] getPsiFiles() {
         final Collection<VirtualFile> files = panel.getVirtualFiles();
         final List<PsiFile> psiFiles = new ArrayList<PsiFile>();
         final PsiManager manager = PsiManager.getInstance(panel.getProject());
         for (final VirtualFile file : files) {
           final PsiFile psiFile = manager.findFile(file);
           if (psiFile != null) {
             psiFiles.add(psiFile);
           }
         }
         return PsiUtilCore.toPsiFileArray(psiFiles);
       }
     };
   }

  private static boolean enabled(@NotNull CheckinProjectPanel panel) {
    return PropertiesComponent.getInstance(panel.getProject()).getBoolean(GO_FMT, false);
  }
}