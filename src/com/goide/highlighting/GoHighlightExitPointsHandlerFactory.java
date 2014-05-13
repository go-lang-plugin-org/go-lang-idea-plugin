package com.goide.highlighting;

import com.goide.GoTypes;
import com.goide.psi.*;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoHighlightExitPointsHandlerFactory implements HighlightUsagesHandlerFactory {
  @Override
  public HighlightUsagesHandlerBase createHighlightUsagesHandler(Editor editor, PsiFile file) {
    int offset = editor.getCaretModel().getOffset();
    PsiElement target = file.findElementAt(offset);
    return MyHandler.createForElement(editor, file, target);
  }

  public static class MyHandler extends HighlightUsagesHandlerBase<PsiElement> {
    private final PsiElement myTarget;
    private final GoTypeOwner myFunction;

    private MyHandler(Editor editor, PsiFile file, PsiElement target, GoTypeOwner function) {
      super(editor, file);
      myTarget = target;
      myFunction = function;
    }

    @Override
    public List<PsiElement> getTargets() {
      return ContainerUtil.newSmartList(myTarget);
    }

    @Override
    protected void selectTargets(List<PsiElement> targets, Consumer<List<PsiElement>> selectionConsumer) {
      selectionConsumer.consume(targets);
    }

    @Override
    public void computeUsages(List<PsiElement> targets) {
      new GoRecursiveVisitor() {
        @Override
        public void visitFunctionLit(@NotNull GoFunctionLit literal) {
        }

        @Override
        public void visitReturnStatement(@NotNull GoReturnStatement statement) {
          addOccurrence(statement);
        }

        @Override
        public void visitCallExpr(@NotNull GoCallExpr o) {
          if (isPanic(o)) addOccurrence(o);
          super.visitCallExpr(o);
        }
      }.visitTypeOwner(myFunction);
    }

    private static boolean isPanic(GoCallExpr o) {
      GoExpression e = o.getExpression();
      if (StringUtil.equals("panic", e.getText()) && e instanceof GoReferenceExpression) {
        PsiReference reference = e.getReference();
        PsiElement resolve = reference != null ? reference.resolve() : null;
        if (!(resolve instanceof GoFunctionDeclaration)) return false;
        GoFile file = ((GoFunctionDeclaration)resolve).getContainingFile();
        return StringUtil.equals(file.getPackageName(), "builtin") && StringUtil.equals(file.getName(), "builtin.go");
      }
      return false;
    }

    public static MyHandler createForElement(@NotNull Editor editor, PsiFile file, PsiElement element) {
      GoTypeOwner function = PsiTreeUtil.getParentOfType(element, GoFunctionLit.class, GoFunctionOrMethodDeclaration.class);
      if (function == null) return null;
      if (element instanceof LeafPsiElement && ((LeafPsiElement)element).getElementType() == GoTypes.RETURN || isPanicCall(element)) {
        return new MyHandler(editor, file, element, function);
      }
      return null;
    }

    private static boolean isPanicCall(@NotNull PsiElement e) {
      PsiElement parent = e.getParent();
      if (parent instanceof GoReferenceExpression) {
        PsiElement grandPa = parent.getParent();
        if (grandPa instanceof GoCallExpr) return isPanic((GoCallExpr)grandPa);
      }
      return false;
    }
  }
}