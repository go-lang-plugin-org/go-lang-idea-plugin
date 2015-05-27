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

package com.goide.actions;

import com.goide.GoIcons;
import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public class GoCreateFileAction extends CreateFileFromTemplateAction implements DumbAware { // todo: rewrite with package support?
  private static final String NEW_GO_FILE = "New Go File";
  private static final String PACKAGE = "PACKAGE";

  @Override
  protected PsiFile createFile(String name, @NotNull String templateName, @NotNull PsiDirectory dir) {
    FileTemplate template = FileTemplateManager.getInstance(dir.getProject()).getInternalTemplate(templateName);
    Properties properties = new Properties();
    properties.setProperty(PACKAGE, ContainerUtil.getLastItem(StringUtil.split(dir.getName(), "-")));
    try {
      PsiElement element = FileTemplateUtil.createFromTemplate(template, name, properties, dir);
      if (element instanceof PsiFile) return (PsiFile)element;
    }
    catch (Exception e) {
      LOG.warn(e);
      return null;
    }
    return super.createFile(name, templateName, dir);
  }

  public GoCreateFileAction() {
    super(NEW_GO_FILE, "", GoIcons.ICON);
  }

  @Override
  protected void buildDialog(final Project project, PsiDirectory directory, @NotNull CreateFileFromTemplateDialog.Builder builder) {
    // todo: check that file already exists
    builder.setTitle(NEW_GO_FILE)
      .addKind("Empty file", GoIcons.ICON, "Go File")
      .addKind("Simple Application", GoIcons.ICON, "Go Application");
  }

  @NotNull
  @Override
  protected String getActionName(PsiDirectory directory, String newName, String templateName) {
    return NEW_GO_FILE;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof GoCreateFileAction;
  }
}