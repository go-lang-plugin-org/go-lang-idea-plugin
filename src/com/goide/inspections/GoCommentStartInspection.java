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

package com.goide.inspections;

import com.goide.psi.*;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;

/**
 * golint inspection from:
 * https://github.com/golang/lint/blob/32a87160691b3c96046c0c678fe57c5bef761456/lint.go#L744
 */
public class GoCommentStartInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitComment(PsiComment comment) {
        super.visitComment(comment);

        if (comment.getPrevSibling() instanceof PsiWhiteSpace &&
            comment.getPrevSibling().getPrevSibling() instanceof PsiComment &&
            comment.getPrevSibling().getTextLength() == 1) {
          return;
        }

        PsiElement commentParent = comment.getParent();
        if ((!(commentParent instanceof GoFile)) &&
            (!(commentParent instanceof GoTopLevelDeclaration))) {
          return;
        }

        String elementName = null;
        PsiElement nextElement = comment;
        boolean hadMoreThanOneSpace = false;
        boolean hadNewComment = false;
        while (true) {
          nextElement = nextElement.getNextSibling();
          if (nextElement instanceof PsiWhiteSpace) {
            if (nextElement.getTextLength() > 1) {
              hadMoreThanOneSpace = true;
            }
            continue;
          }
          if (!(nextElement instanceof PsiComment)) {
            break;
          }
          else if (hadMoreThanOneSpace) {
            hadNewComment = true;
          }
        }

        if (hadNewComment) {
          return;
        }

        if ((nextElement instanceof GoNamedElement)) {
          elementName = ((GoNamedElement)nextElement).getName();
        }
        else if (nextElement instanceof GoPackageClause) {
          PsiElement identifier = ((GoPackageClause)nextElement).getIdentifier();
          if (identifier == null) {
            return;
          }
          elementName = identifier.getText();
        }
        else if (nextElement instanceof GoTypeDeclaration) {
          if (((GoTypeDeclaration)nextElement).getTypeSpecList().size() == 0) {
            return;
          }
          elementName = ((GoTypeDeclaration)nextElement).getTypeSpecList().get(0).getName();
        }
        else if (nextElement instanceof GoConstSpec) {
          if (((GoConstSpec)nextElement).getConstDefinitionList().size() == 0) {
            return;
          }
          elementName = ((GoConstSpec)nextElement).getConstDefinitionList().get(0).getName();
        }
        else if (nextElement instanceof GoConstDeclaration) {
          if (((GoConstDeclaration)nextElement).getConstSpecList().size() == 0) {
            return;
          }
          elementName = ((GoConstDeclaration)nextElement).getConstSpecList().get(0).getConstDefinitionList().get(0).getName();
        }
        else if (nextElement instanceof GoVarSpec) {
          if (((GoVarSpec)nextElement).getVarDefinitionList().size() == 0) {
            return;
          }
          elementName = ((GoVarSpec)nextElement).getVarDefinitionList().get(0).getName();
        }
        else if (nextElement instanceof GoVarDeclaration) {
          if (((GoVarDeclaration)nextElement).getVarSpecList().size() == 0) {
            return;
          }
          elementName = ((GoVarDeclaration)nextElement).getVarSpecList().get(0).getVarDefinitionList().get(0).getName();
        }

        if (elementName == null ||
            (!(nextElement instanceof GoPackageClause)) && !StringUtil.isCapitalized(elementName)) {
          return;
        }

        String commentText = comment.getText();
        if (nextElement instanceof GoPackageClause) {
          if (!elementName.equals("main") && !commentText.startsWith("// Package " + elementName)) {
            String description = "Package comment should be of the form \"Package " + elementName + " ...\"\n";
            holder.registerProblem(comment, description, ProblemHighlightType.WEAK_WARNING);
          }
        }
        else if (!correctCommentStart(commentText, elementName)) {
          holder.registerProblem(comment, "Comment should start with '" + elementName + "'", ProblemHighlightType.WEAK_WARNING);
        }
        // +4 stands for //<space>Element_Name<space>
        else if (commentText.length() <= elementName.length() + 4) {
          holder.registerProblem(comment, "Comment should be meaningful or it should be removed", ProblemHighlightType.WEAK_WARNING);
        }

        if (hadMoreThanOneSpace) {
          holder.registerProblem(comment, "Comment is detached from the element", ProblemHighlightType.WEAK_WARNING);
        }
      }
    };
  }

  private static boolean correctCommentStart(String commentText, String elementName) {
    return commentText.startsWith("// " + elementName) ||
           commentText.startsWith("// A " + elementName) ||
           commentText.startsWith("// An " + elementName) ||
           commentText.startsWith("// The " + elementName);
  }
}
