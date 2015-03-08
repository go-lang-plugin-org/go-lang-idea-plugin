/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

import com.goide.psi.*;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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
      List<PsiComment> comments = getPreviousNonWsComment(alone ? topLevel : element);
      String result = getSignature(element);
      if (!comments.isEmpty()) result += getCommentText(comments);
      return result.length() > 0 ? "<pre>" + result + "</pre>" : "";
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
      if (!comments.isEmpty()) return "<pre>" + getCommentText(comments) + "</pre>";
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
    return StringUtil.join(ContainerUtil.map(comments, new Function<PsiComment, String>() {
      @Override
      public String fun(@NotNull PsiComment c) {
        IElementType type = c.getTokenType();
        String text = c.getText();
        if (type == GoParserDefinition.LINE_COMMENT) {
          text = text.replaceAll("//", "");
        }
        else if (type == GoParserDefinition.MULTILINE_COMMENT) {
          text = StringUtil.trimEnd(text, "*/");
          text = StringUtil.trimStart(text, "/*");
          text = LEADING_TAB.matcher(text).replaceAll("");
        }
        return text;
      }
    }), "<br/>");
  }

  @NotNull
  private static String getSignature(PsiElement element) {
    if (!(element instanceof GoSignatureOwner)) {
      return "";
    }

    PsiElement identifier = null;
    if (element instanceof GoNamedSignatureOwner) {
      identifier = ((GoNamedSignatureOwner)element).getIdentifier();
    }
    GoSignature signature = ((GoSignatureOwner)element).getSignature();

    if (identifier == null && signature == null) {
      return "";
    }

    String result = " <b>func " + (identifier != null ? identifier.getText() : "") + "(";

    if (signature != null) {
      result += getParametersAsString(signature.getParameters());
    }

    result += ")";

    if (signature != null && signature.getResult() != null) {
      GoResult signatureResult = signature.getResult();
      if (signatureResult.getParameters() != null){
        String signatureParameters = getParametersAsString(signatureResult.getParameters());

        if (signatureParameters.length() > 0) {
          result += " (" + signatureParameters + ")";
        }
      }
      else if (signatureResult.getType() != null) {
        GoType signatureResultType = signatureResult.getType();
        if (signatureResultType instanceof GoTypeList) {
          result += " (" + signatureResult.getType().getText() + ")";
        } else {
          result += " " + signatureResult.getType().getText();
        }
      }
    }

    return result + "</b><br/>";
  }

  private static String getParametersAsString(@NotNull GoParameters parameters) {
    List<GoParameterDeclaration> paramDeclarations = parameters.getParameterDeclarationList();
    List<String> paramPresentations = ContainerUtil.newArrayListWithCapacity(2 * paramDeclarations.size());
    boolean isVariadic;
    for (GoParameterDeclaration paramDeclaration : paramDeclarations) {
      isVariadic = paramDeclaration.isVariadic();
      for (GoParamDefinition paramDefinition : paramDeclaration.getParamDefinitionList()) {
        String separator = isVariadic ? " ..." : " ";
        paramPresentations.add(paramDefinition.getText() + separator + paramDeclaration.getType().getText());
      }
      if (paramDeclaration.getParamDefinitionList().size() == 0) {
        paramPresentations.add(paramDeclaration.getType().getText());
      }
    }

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < paramPresentations.size(); ++i) {
      if (i != 0) {
        builder.append(", ");
      }
      builder.append(paramPresentations.get(i));
    }

    return builder.toString();
  }
}
