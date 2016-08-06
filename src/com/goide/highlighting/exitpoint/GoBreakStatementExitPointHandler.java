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

package com.goide.highlighting.exitpoint;

import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GoBreakStatementExitPointHandler extends HighlightUsagesHandlerBase<PsiElement> {
  @NotNull private final PsiElement myTarget;
  @Nullable private final GoBreakStatement myBreakStatement;
  @Nullable private final PsiElement myOwner;

  private GoBreakStatementExitPointHandler(@NotNull Editor editor,
                                           @NotNull PsiFile file,
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
    return Collections.singletonList(myTarget);
  }

  @Override
  protected void selectTargets(List<PsiElement> targets, @NotNull Consumer<List<PsiElement>> selectionConsumer) {
    selectionConsumer.consume(targets);
  }

  @Override
  public void computeUsages(List<PsiElement> targets) {
    PsiElement breakStmtOwner = findBreakStatementOwner();
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
        if (o == myBreakStatement || getBreakStatementOwnerOrResolve(o) == breakStmtOwner) {
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
        visitor.visitCompositeElement((GoCompositeElement)parent);
      }
    }
  }

  @Nullable
  private PsiElement findBreakStatementOwner() {
    if (myOwner != null) return myOwner;
    if (myBreakStatement != null) return getBreakStatementOwnerOrResolve(myBreakStatement);
    return null;
  }

  @Nullable
  public static PsiElement getBreakStatementOwnerOrResolve(@NotNull GoBreakStatement breakStatement) {
    GoLabelRef label = breakStatement.getLabelRef();
    if (label != null) {
      return label.getReference().resolve();
    }
    return GoPsiImplUtil.getBreakStatementOwner(breakStatement);
  }

  @Nullable
  public static GoBreakStatementExitPointHandler createForElement(@NotNull Editor editor,
                                                                  @NotNull PsiFile file,
                                                                  @NotNull PsiElement element) {
    PsiElement target = PsiTreeUtil.getParentOfType(element, GoBreakStatement.class, GoSwitchStatement.class, GoSelectStatement.class,
                                                    GoForStatement.class);
    if (target == null) {
      return null;
    }
    return target instanceof GoBreakStatement
           ? new GoBreakStatementExitPointHandler(editor, file, element, (GoBreakStatement)target, null)
           : new GoBreakStatementExitPointHandler(editor, file, element, null, target);
  }
}
