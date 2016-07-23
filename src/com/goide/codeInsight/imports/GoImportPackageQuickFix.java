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

package com.goide.codeInsight.imports;

import com.goide.GoIcons;
import com.goide.completion.GoCompletionUtil;
import com.goide.project.GoVendoringUtil;
import com.goide.psi.GoFile;
import com.goide.psi.GoReferenceExpression;
import com.goide.psi.GoTypeReferenceExpression;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoReference;
import com.goide.psi.impl.GoTypeReference;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.stubs.index.GoPackagesIndex;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.daemon.impl.DaemonListeners;
import com.intellij.codeInsight.daemon.impl.ShowAutoImportPass;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInspection.HintAction;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.impl.LaterInvocator;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

import static com.intellij.util.containers.ContainerUtil.*;

public class GoImportPackageQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement implements HintAction, HighPriorityAction {
  @NotNull private final String myPackageName;
  @Nullable private List<String> myPackagesToImport;

  public GoImportPackageQuickFix(@NotNull PsiElement element, @NotNull String importPath) {
    super(element);
    myPackageName = "";
    myPackagesToImport = Collections.singletonList(importPath);
  }

  public GoImportPackageQuickFix(@NotNull PsiReference reference) {
    super(reference.getElement());
    myPackageName = reference.getCanonicalText();
  }

  @Nullable
  public PsiReference getReference(PsiElement element) {
    if (element != null && element.isValid()) {
      for (PsiReference reference : element.getReferences()) {
        if (isSupportedReference(reference)) {
          return reference;
        }
      }
    }
    return null;
  }

  private static boolean isSupportedReference(@Nullable PsiReference reference) {
    return reference instanceof GoReference || reference instanceof GoTypeReference;
  }

  @Override
  public boolean showHint(@NotNull Editor editor) {
    return doAutoImportOrShowHint(editor, true);
  }

  @NotNull
  @Override
  public String getText() {
    PsiElement element = getStartElement();
    if (element != null) {
      return "Import " + getText(getImportPathVariantsToImport(element));
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
    if (!FileModificationService.getInstance().prepareFileForWrite(file)) return;
    perform(getImportPathVariantsToImport(startElement), file, editor);
  }

  @Override
  public boolean isAvailable(@NotNull Project project,
                             @NotNull PsiFile file,
                             @NotNull PsiElement startElement,
                             @NotNull PsiElement endElement) {
    PsiReference reference = getReference(startElement);
    return file instanceof GoFile && file.getManager().isInProject(file)
           && reference != null && reference.resolve() == null
           && !getImportPathVariantsToImport(startElement).isEmpty() && notQualified(startElement);
  }

  private static boolean notQualified(@Nullable PsiElement startElement) {
    return startElement instanceof GoReferenceExpression && ((GoReferenceExpression)startElement).getQualifier() == null ||
      startElement instanceof GoTypeReferenceExpression && ((GoTypeReferenceExpression)startElement).getQualifier() == null;
  }

  @NotNull
  private List<String> getImportPathVariantsToImport(@NotNull PsiElement element) {
    if (myPackagesToImport == null) {
      myPackagesToImport = getImportPathVariantsToImport(myPackageName, element);
    }
    return myPackagesToImport;
  }

  @NotNull
  public static List<String> getImportPathVariantsToImport(@NotNull String packageName, @NotNull PsiElement context) {
    PsiFile contextFile = context.getContainingFile();
    Set<String> imported = contextFile instanceof GoFile 
                           ? ((GoFile)contextFile).getImportedPackagesMap().keySet() : Collections.emptySet();
    Project project = context.getProject();
    PsiDirectory parentDirectory = contextFile != null ? contextFile.getParent() : null;
    String testTargetPackage = GoTestFinder.getTestTargetPackage(contextFile);
    Module module = contextFile != null ? ModuleUtilCore.findModuleForPsiElement(contextFile) : null;
    boolean vendoringEnabled = GoVendoringUtil.isVendoringEnabled(module);
    GlobalSearchScope scope = GoUtil.goPathResolveScope(context);
    Collection<GoFile> packages = StubIndex.getElements(GoPackagesIndex.KEY, packageName, project, scope, GoFile.class);
    return sorted(skipNulls(map2Set(
      packages,
      file -> {
        if (parentDirectory != null && parentDirectory.isEquivalentTo(file.getParent())) {
          if (testTargetPackage == null || !testTargetPackage.equals(file.getPackageName())) {
            return null;
          }
        }
        if (!GoPsiImplUtil.canBeAutoImported(file, false, module)) {
          return null;
        }
        String importPath = file.getImportPath(vendoringEnabled);
        return !imported.contains(importPath) ? importPath : null;
      }
    )), new MyImportsComparator(context, vendoringEnabled));
  }

  public boolean doAutoImportOrShowHint(@NotNull Editor editor, boolean showHint) {
    PsiElement element = getStartElement();
    if (element == null || !element.isValid()) return false;

    PsiReference reference = getReference(element);
    if (reference == null || reference.resolve() != null) return false;

    List<String> packagesToImport = getImportPathVariantsToImport(element);
    if (packagesToImport.isEmpty()) {
      return false;
    }

    PsiFile file = element.getContainingFile();
    String firstPackageToImport = getFirstItem(packagesToImport);

    // autoimport on trying to fix
    if (packagesToImport.size() == 1) {
      if (GoCodeInsightSettings.getInstance().isAddUnambiguousImportsOnTheFly() && !LaterInvocator.isInModalContext() &&
          (ApplicationManager.getApplication().isUnitTestMode() || DaemonListeners.canChangeFileSilently(file))) {
        CommandProcessor.getInstance().runUndoTransparentAction(() -> perform(file, firstPackageToImport));
        return true;
      }
    }

    // show hint on failed autoimport
    if (showHint) {
      if (ApplicationManager.getApplication().isUnitTestMode()) return false;
      if (HintManager.getInstance().hasShownHintsThatWillHideByOtherHint(true)) return false;
      if (!GoCodeInsightSettings.getInstance().isShowImportPopup()) return false;
      TextRange referenceRange = reference.getRangeInElement().shiftRight(element.getTextRange().getStartOffset());
      HintManager.getInstance().showQuestionHint(
        editor,
        ShowAutoImportPass.getMessage(packagesToImport.size() > 1, getFirstItem(packagesToImport)),
        referenceRange.getStartOffset(),
        referenceRange.getEndOffset(),
        () -> {
          if (file.isValid() && !editor.isDisposed()) {
            perform(packagesToImport, file, editor);
          }
          return true;
        }
      );
      return true;
    }
    return false;
  }

  private void perform(@NotNull List<String> packagesToImport, @NotNull PsiFile file, @Nullable Editor editor) {
    LOG.assertTrue(editor != null || packagesToImport.size() == 1, "Cannot invoke fix with ambiguous imports on null editor");
    if (packagesToImport.size() > 1 && editor != null) {
      JBList list = new JBList(packagesToImport);
      list.installCellRenderer(o -> {
        JBLabel label = new JBLabel(o.toString(), GoIcons.PACKAGE, SwingConstants.LEFT);
        label.setBorder(IdeBorderFactory.createEmptyBorder(2, 4, 2, 4));
        return label;
      });
      PopupChooserBuilder builder = JBPopupFactory.getInstance().createListPopupBuilder(list).setRequestFocus(true)
        .setTitle("Package to import")
        .setItemChoosenCallback(
          () -> {
            int i = list.getSelectedIndex();
            if (i < 0) return;
            perform(file, packagesToImport.get(i));
          })
        .setFilteringEnabled(o -> o instanceof String ? (String)o : o.toString());
      JBPopup popup = builder.createPopup();
      builder.getScrollPane().setBorder(null);
      builder.getScrollPane().setViewportBorder(null);
      popup.showInBestPositionFor(editor);
    }
    else if (packagesToImport.size() == 1) {
      perform(file, getFirstItem(packagesToImport));
    }
    else {
      String packages = StringUtil.join(packagesToImport, ",");
      throw new IncorrectOperationException("Cannot invoke fix with ambiguous imports on editor ()" + editor + ". Packages: " + packages);
    }
  }

  private void perform(@NotNull PsiFile file, @Nullable String pathToImport) {
    if (file instanceof GoFile && pathToImport != null) {
      Project project = file.getProject();
      CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
        if (!isAvailable()) return;
        if (((GoFile)file).getImportedPackagesMap().containsKey(pathToImport)) return;
        ((GoFile)file).addImport(pathToImport, null);
      }), "Add import", null);
    }
  }

  private static class MyImportsComparator implements Comparator<String> {
    @Nullable
    private final String myContextImportPath;

    public MyImportsComparator(@Nullable PsiElement context, boolean vendoringEnabled) {
      myContextImportPath = GoCompletionUtil.getContextImportPath(context, vendoringEnabled);
    }

    @Override
    public int compare(@NotNull String s1, @NotNull String s2) {
      int result = Comparing.compare(GoCompletionUtil.calculatePackagePriority(s2, myContextImportPath),
                                     GoCompletionUtil.calculatePackagePriority(s1, myContextImportPath));
      return result != 0 ? result : Comparing.compare(s1, s2);
    }
  }
}
