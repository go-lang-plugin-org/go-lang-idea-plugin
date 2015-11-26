/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.highlighting.exitpoint;

import com.goide.psi.*;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoBreakStatementExitPointHandler extends HighlightUsagesHandlerBase<PsiElement> {
  @NotNull private final PsiElement myTarget;
  @Nullable private final GoBreakStatement myBreakStatement;
  @Nullable private PsiElement myOwner;

  private GoBreakStatementExitPointHandler(Editor editor,
                                           PsiFile file,
                                           @NotNull PsiElement target,
                                           @Nullable GoBreakStatement breakStatement,
                                           @Nullable PsiElement owner) {
    super(editor, file);
    myTarget = target;
    myBreakStatement = breakStatement;
    myOwner = owner;
  }

  @NotNull
  @Override
  public List<PsiElement> getTargets() {
    return ContainerUtil.newSmartList(myTarget);
  }

  @Override
  protected void selectTargets(List<PsiElement> targets, @NotNull Consumer<List<PsiElement>> selectionConsumer) {
    selectionConsumer.consume(targets);
  }

  @Override
  public void computeUsages(List<PsiElement> targets) {
    final PsiElement breakStmtOwner;
    if (myOwner != null) {
      breakStmtOwner = myOwner;
    }
    else if (myBreakStatement != null) {
      breakStmtOwner = getBreakStatementOwner(myBreakStatement);
    }
    else {
      breakStmtOwner = null;
    }
    GoRecursiveVisitor visitor = new GoRecursiveVisitor() {
      @Override
      public void visitLabelDefinition(@NotNull GoLabelDefinition o) {
        if (o == breakStmtOwner) {
          addOccurrence(o);
        }
        super.visitLabelDefinition(o);
      }

      @Override
      public void visitBreakStatement(@NotNull GoBreakStatement o) {
        if (o == myBreakStatement || getBreakStatementOwner(o) == breakStmtOwner) {
          addOccurrence(o);
        }
        super.visitBreakStatement(o);
      }

      @Override
      public void visitSwitchStatement(@NotNull GoSwitchStatement o) {
        if (o == breakStmtOwner) {
          GoSwitchStart switchStart = o.getSwitchStart();
          if (switchStart != null) {
            addOccurrence(switchStart.getSwitch());
          }
        }
        super.visitSwitchStatement(o);
      }

      @Override
      public void visitForStatement(@NotNull GoForStatement o) {
        if (o == breakStmtOwner) {
          addOccurrence(o.getFor());
        }
        super.visitForStatement(o);
      }

      @Override
      public void visitSelectStatement(@NotNull GoSelectStatement o) {
        if (o == breakStmtOwner) {
          addOccurrence(o.getSelect());
        }
        super.visitSelectStatement(o);
      }
    };
    if (breakStmtOwner != null) {
      PsiElement parent = breakStmtOwner.getParent();
      if (parent instanceof GoCompositeElement) {
        visitor.visitCompositeElement(((GoCompositeElement)parent));
      }
    }
  }


  @Nullable
  private static PsiElement getBreakStatementOwner(@NotNull GoBreakStatement breakStmt) {
    GoLabelRef label = breakStmt.getLabelRef();
    if (label != null) {
      return label.getReference().resolve();
    }
    GoCompositeElement breaksOutOf =
      PsiTreeUtil.getParentOfType(breakStmt, GoSwitchStatement.class, GoForStatement.class, GoSelectStatement.class,
                                  GoFunctionLit.class);
    if (breaksOutOf instanceof GoFunctionLit) {
      return null;
    }
    return breaksOutOf;
  }

  @Nullable
  public static GoBreakStatementExitPointHandler createForElement(@NotNull Editor editor, @NotNull PsiFile file, @NotNull PsiElement element) {
    PsiElement target =
      PsiTreeUtil.getParentOfType(element, GoBreakStatement.class, GoSwitchStatement.class, GoSelectStatement.class, GoForStatement.class);
    if (target == null) return null;
    if (target instanceof GoBreakStatement) {
      return new GoBreakStatementExitPointHandler(editor, file, element, (GoBreakStatement)target, null);
    }
    return new GoBreakStatementExitPointHandler(editor, file, element, null, target);
  }
}
