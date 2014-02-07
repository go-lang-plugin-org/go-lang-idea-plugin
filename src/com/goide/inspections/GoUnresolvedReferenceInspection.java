package com.goide.inspections;

import com.goide.GoTypes;
import com.goide.psi.*;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
import static com.intellij.codeInspection.ProblemHighlightType.LIKE_UNKNOWN_SYMBOL;

public class GoUnresolvedReferenceInspection extends GoInspectionBase {
  @Override
  protected void checkFile(PsiFile file, final ProblemsHolder problemsHolder) {
    if (!(file instanceof GoFile)) return;
    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitReferenceExpression(@NotNull GoReferenceExpression o) {
        super.visitReferenceExpression(o);
        PsiReference reference = o.getReference();
        GoReferenceExpression qualifier = o.getQualifier();
        PsiReference qualifierRef = qualifier != null ? qualifier.getReference() : null;
        PsiElement qualifierResolve = qualifierRef != null ? qualifierRef.resolve() : null;
        if (qualifier != null && qualifierResolve == null) return;
        if (reference.resolve() == null) {
          PsiElement id = o.getIdentifier();
          String name = id.getText();
          problemsHolder.registerProblem(id, "Unresolved reference " + "'" + name + "'", LIKE_UNKNOWN_SYMBOL);
        }
      }

      @Override
      public void visitImportString(@NotNull GoImportString o) {
        if (o.getTextLength() < 2) return;
        PsiReference[] references = o.getReferences();
        for (final PsiReference reference : references) {
          if (reference instanceof FileReference) {
            if (((FileReference)reference).multiResolve(false).length == 0) {
              ProblemHighlightType type = reference.getRangeInElement().isEmpty() ? GENERIC_ERROR_OR_WARNING : LIKE_UNKNOWN_SYMBOL;
              problemsHolder.registerProblem(reference, ProblemsHolder.unresolvedReferenceMessage(reference), type);
            }
          }
        }
      }

      @Override
      public void visitLabelRef(@NotNull GoLabelRef o) {
        PsiReference reference = o.getReference();
        String name = o.getText();
        if (reference.resolve() == null) {
          problemsHolder.registerProblem(o, "Unresolved label " + "'" + name + "'", LIKE_UNKNOWN_SYMBOL);
        }
      }

      @Override
      public void visitTypeReferenceExpression(@NotNull GoTypeReferenceExpression o) {
        super.visitTypeReferenceExpression(o);
        PsiReference reference = o.getReference();
        GoTypeReferenceExpression qualifier = o.getQualifier();
        PsiReference qualifierRef = qualifier != null ? qualifier.getReference() : null;
        PsiElement qualifierResolve = qualifierRef != null ? qualifierRef.resolve() : null;
        if (qualifier != null && qualifierResolve == null) return;
        if (reference.resolve() == null) {
          PsiElement id = o.getIdentifier();
          String name = id.getText();
          ASTNode next = FormatterUtil.getNextNonWhitespaceSibling(o.getNode());
          boolean isDot = next != null && next.getElementType() == GoTypes.DOT;
          LocalQuickFix[] fixes = isDot ? new LocalQuickFix[]{} : new LocalQuickFix[]{new IntroduceTypeFix(id, name)};
          problemsHolder.registerProblem(id, "Unresolved type " + "'" + name + "'", LIKE_UNKNOWN_SYMBOL, fixes);
        }
      }
    });
  }

  private static class IntroduceTypeFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private final String myName;

    protected IntroduceTypeFix(@NotNull PsiElement id, String name) {
      super(id);
      myName = name;
    }

    @Override
    public void invoke(@NotNull Project project,
                       @NotNull PsiFile file,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull PsiElement startElement,
                       @NotNull PsiElement endElement) {
      GoTopLevelDeclaration decl = PsiTreeUtil.getTopmostParentOfType(startElement, GoTopLevelDeclaration.class);
      if (decl == null || !(decl.getParent() instanceof GoFile)) return;

      if (editor == null) return;
      TemplateManagerImpl templateManager = (TemplateManagerImpl)TemplateManager.getInstance(project);
      Template template = TemplateSettings.getInstance().getTemplateById("go_lang_type_qf");
      if (template != null) {
        int start = decl.getTextRange().getStartOffset();
        editor.getDocument().insertString(start, "\n\n");
        editor.getCaretModel().moveToOffset(start);
        templateManager.startTemplate(editor, template, true, ContainerUtil.stringMap("NAME", myName), null);
      }
    }

    @NotNull
    @Override
    public String getText() {
      return "Create type '" + myName + "'";
    }

    @NotNull
    @Override
    public String getFamilyName() {
      return "Go";
    }
  }
}
