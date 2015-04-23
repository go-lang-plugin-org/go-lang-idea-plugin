package com.goide.highlighting;

import com.goide.GoFileType;
import com.goide.sdk.GoSdkUtil;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.daemon.impl.analysis.DefaultHighlightingSettingProvider;
import com.intellij.codeInsight.daemon.impl.analysis.FileHighlightingSetting;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoDefaultHighlightingSettingProvider extends DefaultHighlightingSettingProvider {
  @Nullable
  @Override
  public FileHighlightingSetting getDefaultSetting(@NotNull Project project, @NotNull VirtualFile file) {
    if (file.getFileType() != GoFileType.INSTANCE) return null;
    if (project.isDefault()) return null;
    if (!file.isValid()) return null;

    Module module = ModuleUtilCore.findModuleForFile(file, project);
    if (!GoUtil.importPathAllowed(GoSdkUtil.getPathRelativeToSdkAndLibraries(file, project, module))) {
      return FileHighlightingSetting.SKIP_HIGHLIGHTING;
    }
    return null;
  }
}
