package com.goide.actions;

import com.goide.GoSdkType;
import com.goide.jps.model.JpsGoSdkType;
import com.goide.psi.GoFile;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.ExceptionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class GoFmtFileAction extends AnAction implements DumbAware {
  private static final String NOTIFICATION_TITLE = "Reformat code with go gmt";
  private static final Logger LOG = Logger.getInstance(GoFmtFileAction.class);

  @Override
  public void update(@NotNull AnActionEvent e) {
    if (e.getProject() == null) return;
    PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
    boolean isErlang = psiFile instanceof GoFile;
    e.getPresentation().setEnabled(isErlang);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
    final Project project = e.getProject();
    assert project != null;
    assert file instanceof GoFile;
    final VirtualFile vFile = file.getVirtualFile();

    if (vFile == null || !vFile.isInLocalFileSystem()) return;
    final Document document = PsiDocumentManager.getInstance(project).getDocument(file);
    assert document != null;
    final String filePath = vFile.getCanonicalPath();
    assert filePath != null;

    final String groupId = e.getPresentation().getText();
    try {
      GeneralCommandLine commandLine = new GeneralCommandLine();
      Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();

      String sdkHome = sdk != null ? sdk.getHomePath() : null;
      if (StringUtil.isEmpty(sdkHome)) {
        notify(project, groupId, "Project sdk is empty", NotificationType.WARNING);
        return;
      }
      if (!(sdk.getSdkType() instanceof GoSdkType)) {
        notify(project, groupId, "Project sdk is not valid", NotificationType.WARNING);
        return;
      }

      File executable = JpsGoSdkType.getGoExecutableFile(sdkHome);

      commandLine.setExePath(executable.getAbsolutePath());
      commandLine.addParameters("fmt", filePath);

      final String commandLineString = commandLine.getCommandLineString();
      OSProcessHandler handler = new OSProcessHandler(commandLine.createProcess(), commandLineString);
      handler.addProcessListener(new ProcessAdapter() {
        @Override
        public void processTerminated(ProcessEvent event) {
          ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
              ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                  try {
                    document.setText(FileUtil.loadFile(new File(filePath)));
                  }
                  catch (IOException e) {
                    error(file, project, groupId, null);
                  }
                }
              });
            }
          });
        }
      });
      handler.startNotify();
    }
    catch (Exception ex) {
      error(file, project, groupId, ex);
      LOG.error(ex);
    }
  }

  private static void error(@NotNull PsiFile file, @NotNull Project project, @NotNull String groupId, @Nullable Exception ex) {
    Notifications.Bus.notify(new Notification(groupId, file.getName() + " formatting with go fmt failed",
                                              ex == null ? "" : ExceptionUtil.getUserStackTrace(ex, LOG),
                                              NotificationType.ERROR), project);
  }

  private static void notify(@NotNull Project project, @NotNull String groupId, @NotNull String content, @NotNull NotificationType type) {
    Notifications.Bus.notify(new Notification(groupId, NOTIFICATION_TITLE, content, type), project);
  }
}
