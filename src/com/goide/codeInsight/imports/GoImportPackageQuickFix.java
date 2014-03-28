package com.goide.codeInsight.imports;

import com.goide.psi.GoFile;
import com.goide.psi.GoImportList;
import com.goide.psi.GoReferenceExpression;
import com.goide.stubs.index.GoPackagesIndex;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.QuestionAction;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInspection.HintAction;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.intellij.openapi.actionSystem.IdeActions.ACTION_SHOW_INTENTION_ACTIONS;

public class GoImportPackageQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement implements HintAction, HighPriorityAction {
  @NotNull private final String myPackageName;
  @NotNull private final TextRange myRangeInElement;
  @Nullable private Collection<String> myPackagesToImport;
  private boolean isPerformed = false;

  public GoImportPackageQuickFix(@NotNull PsiReference reference) {
    super(reference.getElement());
    myPackageName = reference.getCanonicalText();
    myRangeInElement = reference.getRangeInElement();
  }

  @Override
  public boolean showHint(@NotNull final Editor editor) {
    final PsiElement element = getStartElement();
    if (element == null || !element.isValid()) {
      return false;
    }

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
              applyFix(packagesToImport, element.getContainingFile());
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

  private static String getText(@NotNull Collection<String> packagesToImport) {
    return ContainerUtil.getFirstItem(packagesToImport, "") + "? " + (packagesToImport.size() > 1 ? "(multiple choices...) " : "");
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
    applyFix(packagesToImport, file);
  }

  @Override
  public boolean isAvailable(@NotNull Project project,
                             @NotNull PsiFile file,
                             @NotNull PsiElement startElement,
                             @NotNull PsiElement endElement) {
    return !isPerformed && file instanceof GoFile && file.getManager().isInProject(file) && !getPackagesToImport(startElement).isEmpty()
           && notQualified(startElement);
  }

  private static boolean notQualified(@Nullable PsiElement startElement) {
    return startElement instanceof GoReferenceExpression && ((GoReferenceExpression)startElement).getQualifier() == null;
  }

  @NotNull
  private Collection<String> getPackagesToImport(@NotNull PsiElement element) {
    if (myPackagesToImport == null) {
      myPackagesToImport = ContainerUtil.map2Set(StubIndex.getElements(GoPackagesIndex.KEY, myPackageName, element.getProject(),
                                                                       scope(element), GoFile.class),
                                                 new Function<GoFile, String>() {
                                                   @Override
                                                   public String fun(GoFile file) {
                                                     return file.getFullPackageName();
                                                   }
                                                 }
      );
    }
    return myPackagesToImport;
  }

  private static GlobalSearchScope scope(@NotNull PsiElement element) {
    Module module = ModuleUtilCore.findModuleForPsiElement(element);
    return module != null
           ? GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
           : GlobalSearchScope.projectScope(element.getProject());
  }

  private void applyFix(@NotNull Collection<String> packagesToImport, @NotNull PsiFile file) {
    isPerformed = true;
    String firstItem = ContainerUtil.getFirstItem(packagesToImport);
    if (file instanceof GoFile && firstItem != null) {
      GoImportList importList = ((GoFile)file).getImportList();
      if (importList != null) {
        importList.addImport(firstItem, null);
      }
    }
  }
}
