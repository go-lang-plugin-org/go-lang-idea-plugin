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

package com.goide;

import com.goide.editor.GoParameterInfoHandler;
import com.goide.psi.*;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xml.util.XmlStringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class GoDocumentationProvider extends AbstractDocumentationProvider {
  private static final Pattern LEADING_TAB = Pattern.compile("^\\t", Pattern.MULTILINE);

  @Override
  public String generateDoc(PsiElement element, PsiElement originalElement) {
    if (element instanceof GoImportSpec) {
      element = ((GoImportSpec)element).getImportString().resolve();
    }
    if (element instanceof GoNamedElement) {
      GoTopLevelDeclaration topLevel = PsiTreeUtil.getParentOfType(element, GoTopLevelDeclaration.class);
      Collection<PsiElement> children = PsiTreeUtil.findChildrenOfType(topLevel, element.getClass());
      boolean alone = children.size() == 1 && children.iterator().next().equals(element);
      String signature = getSignature(element);
      signature = StringUtil.isNotEmpty(signature) ? "<b>" + signature + "</b>\n" : signature;
      String commentText = getCommentText(getPreviousNonWsComment(alone ? topLevel : element));
      return StringUtil.isNotEmpty(commentText) ? signature + commentText : signature;
    }
    else if (element instanceof PsiDirectory) {
      String comments = getPackageComment(((PsiDirectory)element).findFile("doc.go"));
      if (comments != null) return comments;
      return getPackageComment(((PsiDirectory)element).findFile(((PsiDirectory)element).getName() + ".go"));
    }
    return null;
  }

  @Nullable
  private static String getPackageComment(@Nullable PsiFile file) {
    if (file instanceof GoFile) {
      // todo: remove after correct stubbing (comments needed in stubs)
      GoPackageClause pack = PsiTreeUtil.findChildOfType(file, GoPackageClause.class);
      List<PsiComment> comments = getPreviousNonWsComment(pack);
      if (!comments.isEmpty()) return getCommentText(comments);
    }
    return null;
  }

  @NotNull
  private static List<PsiComment> getPreviousNonWsComment(@Nullable PsiElement element) {
    if (element == null) return ContainerUtil.emptyList();
    List<PsiComment> result = ContainerUtil.newArrayList();
    PsiElement e;
    for (e = element.getPrevSibling(); e != null; e = e.getPrevSibling()) {
      if (e instanceof PsiWhiteSpace) {
        if (e.getText().contains("\n\n")) return result;
        continue;
      }
      if (e instanceof PsiComment) {
        result.add(0, (PsiComment)e);
      }
      else {
        return result;
      }
    }
    return result;
  }

  @NotNull
  private static String getCommentText(@NotNull List<PsiComment> comments) {
    StringBuilder result = new StringBuilder();
    StringBuilder currentBlock = new StringBuilder();
    Iterator<PsiComment> iterator = comments.iterator();
    while (iterator.hasNext()) {
      PsiComment comment = iterator.next();
      IElementType type = comment.getTokenType();
      String text = comment.getText();
      if (type == GoParserDefinition.LINE_COMMENT) {
        text = text.replaceAll("//", "");
      }
      else if (type == GoParserDefinition.MULTILINE_COMMENT) {
        text = StringUtil.trimEnd(text, "*/");
        text = StringUtil.trimStart(text, "/*");
        text = LEADING_TAB.matcher(text).replaceAll("");
      }
      text = XmlStringUtil.escapeString(text.trim());
      currentBlock.append(text);
      if ((text.isEmpty() || !iterator.hasNext()) && currentBlock.length() > 0) {
        result.append("<p>").append(currentBlock).append("</p>\n");
        currentBlock.setLength(0);
      }
      else {
        currentBlock.append('\n'); // just for prettier testdata
      }
    }
    return result.toString();
  }

  @NotNull
  private static String getSignature(PsiElement element) {
    if (!(element instanceof GoSignatureOwner)) return "";

    PsiElement identifier = null;
    if (element instanceof GoNamedSignatureOwner) {
      identifier = ((GoNamedSignatureOwner)element).getIdentifier();
    }
    GoSignature signature = ((GoSignatureOwner)element).getSignature();

    if (identifier == null && signature == null) {
      return "";
    }

    StringBuilder builder = new StringBuilder("func ").append(identifier != null ? identifier.getText() : "").append('(');
    if (signature != null) {
      builder.append(getParametersAsString(signature.getParameters()));
    }
    builder.append(')');

    GoResult result = signature != null ? signature.getResult() : null;
    GoParameters parameters = result != null ? result.getParameters() : null;
    GoType type = result != null ? result.getType() : null;

    if (parameters != null) {
      String signatureParameters = getParametersAsString(parameters);
      if (!signatureParameters.isEmpty()) {
        builder.append(" (").append(signatureParameters).append(')');
      }
    }
    else if (type != null) {
      if (type instanceof GoTypeList) {
        builder.append(" (").append(XmlStringUtil.escapeString(type.getText())).append(')');
      }
      else {
        builder.append(' ').append(XmlStringUtil.escapeString(type.getText()));
      }
    }
    return builder.toString();
  }

  @NotNull
  private static String getParametersAsString(@NotNull GoParameters parameters) {
    return StringUtil.join(GoParameterInfoHandler.getParameterPresentations(parameters), ", ");
  }

  @Nullable
  @Override
  public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
    if (element instanceof GoNamedElement) {
      String result = getSignature(element);
      if (StringUtil.isNotEmpty(result)) return result;
    }
    return super.getQuickNavigateInfo(element, originalElement);
  }
}
