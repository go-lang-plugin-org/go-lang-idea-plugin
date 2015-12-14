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
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.sdk.GoSdkUtil;
import com.goide.stubs.index.GoTypesIndex;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.documentation.DocumentationManagerProtocol;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScopesCore;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xml.util.XmlStringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GoDocumentationProvider extends AbstractDocumentationProvider {
  private static final GoCommentsConverter COMMENTS_CONVERTER = new GoCommentsConverter();
  private static final Comparator<PsiElement> ELEMENT_BY_RANGE_COMPARATOR = new Comparator<PsiElement>() {
    @Override
    public int compare(PsiElement t1, PsiElement t2) {
      TextRange range1 = t1.getTextRange();
      TextRange range2 = t2.getTextRange();
      int startOffsetDiff = Comparing.compare(range1.getStartOffset(), range2.getStartOffset());
      return startOffsetDiff != 0 ? startOffsetDiff : Comparing.compare(range1.getEndOffset(), range2.getEndOffset());
    }
  };

  @NotNull
  public static String getCommentText(@NotNull List<PsiComment> comments, boolean withHtml) {
    return withHtml ? COMMENTS_CONVERTER.toHtml(comments) : COMMENTS_CONVERTER.toText(comments);
  }

  @NotNull
  public static List<PsiComment> getCommentsForElement(@Nullable PsiElement element) {
    List<PsiComment> comments = getCommentsInner(element);
    if (comments.isEmpty()) {
      if (element instanceof GoVarDefinition || element instanceof GoConstDefinition) {
        PsiElement parent = element.getParent(); // spec
        comments = getCommentsInner(parent);
        if (comments.isEmpty() && parent != null) {
          return getCommentsInner(parent.getParent()); // declaration
        }
      }
      else if (element instanceof GoTypeSpec) {
        return getCommentsInner(element.getParent()); // type declaration
      }
    }
    return comments;
  }

  @NotNull
  private static List<PsiComment> getCommentsInner(@Nullable PsiElement element) {
    if (element == null) {
      return ContainerUtil.emptyList();
    }
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

  @Nullable
  private static GoFile findDocFileForDirectory(@NotNull PsiDirectory directory) {
    PsiFile file = directory.findFile("doc.go");
    if (file instanceof GoFile) {
      return ((GoFile)file);
    }
    PsiFile directoryFile = directory.findFile(GoUtil.suggestPackageForDirectory(directory) + ".go");
    return directoryFile instanceof GoFile ? ((GoFile)directoryFile) : null;
  }

  @Nullable
  private static String getPackageComment(@Nullable GoFile file) {
    if (file != null) {
      // todo: remove after correct stubbing (comments needed in stubs)
      GoPackageClause pack = PsiTreeUtil.findChildOfType(file, GoPackageClause.class);
      String title = "<b>Package " + GoUtil.suggestPackageForDirectory(file.getParent()) + "</b>\n";
      String importPath = "<p><code>import \"" + StringUtil.notNullize(file.getImportPath()) + "\"</code></p>\n";
      return title + importPath + getCommentText(getCommentsForElement(pack), true);
    }
    return null;
  }

  @Nullable
  private static PsiElement adjustDocElement(@Nullable PsiElement element) {
    return element instanceof GoImportSpec ? ((GoImportSpec)element).getImportString().resolve() : element;
  }

  @NotNull
  private static String getSignature(PsiElement element) {
    if (element instanceof GoTypeSpec) {
      String name = ((GoTypeSpec)element).getName();
      return StringUtil.isNotEmpty(name) ? "type " + name : "";
    }
    if (element instanceof GoConstDefinition) {
      String name = ((GoConstDefinition)element).getName();
      if (StringUtil.isNotEmpty(name)) {
        String type = getTypePresentation(((GoConstDefinition)element).getGoType(null), getImportPathForElement(element));
        GoExpression value = ((GoConstDefinition)element).getValue();
        return "const " + name + (!type.isEmpty() ? " " + type : "") + (value != null ? " = " + value.getText() : "");
      }
    }
    if (element instanceof GoVarDefinition) {
      String name = ((GoVarDefinition)element).getName();
      if (StringUtil.isNotEmpty(name)) {
        String type = getTypePresentation(((GoVarDefinition)element).getGoType(null), getImportPathForElement(element));
        GoExpression value = ((GoVarDefinition)element).getValue();
        return "var " + name + (!type.isEmpty() ? " " + type : "") + (value != null ? " = " + value.getText() : "");
      }
    }

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
      builder.append(' ').append(getTypePresentation(type, getImportPathForElement(element)));
    }
    return builder.toString();
  }

  @NotNull
  private static String getParametersAsString(@NotNull GoParameters parameters) {
    final String contextImportPath = getImportPathForElement(parameters);
    return StringUtil.join(GoParameterInfoHandler.getParameterPresentations(parameters, new Function<PsiElement, String>() {
      @Override
      public String fun(PsiElement element) {
        return getTypePresentation(element, contextImportPath);
      }
    }), ", ");
  }

  @Nullable
  private static String getImportPathForElement(@Nullable PsiElement element) {
    PsiFile file = element != null ? element.getContainingFile() : null;
    return file instanceof GoFile ? ((GoFile)file).getImportPath() : null;
  }

  @NotNull
  private static String getTypePresentation(@Nullable PsiElement element, @Nullable String contextImportPath) {
    if (element instanceof GoType) {
      GoType type = ((GoType)element);
      if (type instanceof GoMapType) {
        GoType keyType = ((GoMapType)type).getKeyType();
        GoType valueType = ((GoMapType)type).getValueType();
        return replaceInnerTypes(type, contextImportPath, keyType, valueType);
      }
      else if (type instanceof GoChannelType) {
        return replaceInnerTypes(type, contextImportPath, ((GoChannelType)type).getType());
      }
      else if (type instanceof GoParType) {
        return replaceInnerTypes(type, contextImportPath, ((GoParType)type).getType());
      }
      else if (type instanceof GoArrayOrSliceType) {
        return replaceInnerTypes(type, contextImportPath, ((GoArrayOrSliceType)type).getType());
      }
      else if (type instanceof GoPointerType) {
        return replaceInnerTypes(type, contextImportPath, ((GoPointerType)type).getType());
      }
      else if (type instanceof GoTypeList) {
        return "(" + replaceInnerTypes(type, contextImportPath, ((GoTypeList)type).getTypeList()) + ")";
      }
      else if (type instanceof GoSpecType) {
        return getTypePresentation(GoPsiImplUtil.getTypeSpecSafe(type), contextImportPath);
      }
      GoTypeReferenceExpression typeRef = GoPsiImplUtil.getTypeReference(type);
      if (typeRef != null) {
        PsiElement resolve = typeRef.getReference().resolve();
        if (resolve != null) {
          return getTypePresentation(resolve, contextImportPath);
        }
      }
    }
    else if (element instanceof GoTypeSpec) {
      String ref = getReferenceText(element);
      String name = ((GoTypeSpec)element).getName();
      if (StringUtil.isNotEmpty(name)) {
        String importPath = getImportPathForElement(element);
        String packageName = ((GoTypeSpec)element).getContainingFile().getPackageName();
        if (StringUtil.isNotEmpty(packageName) && !GoPsiImplUtil.builtin(element) && !Comparing.equal(importPath, contextImportPath)) {
          return String.format("<a href=\"%s%s\">%s</a>.<a href=\"%s%s\">%s</a>",
                               DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL, StringUtil.notNullize(importPath), packageName,
                               DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL, ref, name);
        }
        return String.format("<a href=\"%s%s\">%s</a>", DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL, ref, name);
      }
    }

    return element != null ? element.getText() : "";
  }

  @NotNull
  private static String replaceInnerTypes(@NotNull GoType type, @Nullable String contextImportPath, GoType... innerTypes) {
    return replaceInnerTypes(type, contextImportPath, Arrays.asList(innerTypes));
  }

  @NotNull
  private static String replaceInnerTypes(@NotNull GoType type, @Nullable String contextImportPath, @NotNull List<GoType> innerTypes) {
    StringBuilder result = new StringBuilder();
    String typeText = type.getText();
    int initialOffset = type.getTextRange().getStartOffset();
    int lastStartOffset = type.getTextLength();
    ContainerUtil.sort(innerTypes, ELEMENT_BY_RANGE_COMPARATOR);
    for (int i = innerTypes.size() - 1; i >= 0; i--) {
      GoType innerType = innerTypes.get(i);
      if (innerType != null) {
        TextRange range = innerType.getTextRange().shiftRight(-initialOffset);
        result.insert(0, XmlStringUtil.escapeString(typeText.substring(range.getEndOffset(), lastStartOffset)));
        result.insert(0, getTypePresentation(innerType, contextImportPath));
        lastStartOffset = range.getStartOffset();
      }
    }
    result.insert(0, XmlStringUtil.escapeString(typeText.substring(0, lastStartOffset)));
    return result.length() > 0 ? result.toString() : typeText;
  }

  @Nullable
  private static String getReferenceText(@Nullable PsiElement element) {
    if (element instanceof GoNamedElement) {
      PsiFile file = element.getContainingFile();
      if (file instanceof GoFile) {
        String importPath = ((GoFile)file).getImportPath();
        if (element instanceof GoFunctionDeclaration || element instanceof GoTypeSpec) {
          String name = ((GoNamedElement)element).getName();
          if (StringUtil.isNotEmpty(name)) {
            return String.format("%s#%s", StringUtil.notNullize(importPath), name);
          }
        }
        else if (element instanceof GoMethodDeclaration) {
          GoType receiverType = ((GoMethodDeclaration)element).getReceiver().getType();
          String receiver = receiverType != null ? receiverType.getText() : null;
          String name = ((GoMethodDeclaration)element).getName();
          if (StringUtil.isNotEmpty(receiver) && StringUtil.isNotEmpty(name)) {
            return String.format("%s#%s.%s", StringUtil.notNullize(importPath), receiver, name);
          }
        }
      }
    }
    else if (element instanceof PsiDirectory && findDocFileForDirectory((PsiDirectory)element) != null) {
      return GoSdkUtil.getImportPath(((PsiDirectory)element));
    }

    return null;
  }

  @Override
  public String generateDoc(PsiElement element, PsiElement originalElement) {
    element = adjustDocElement(element);
    if (element instanceof GoNamedElement) {
      String signature = getSignature(element);
      signature = StringUtil.isNotEmpty(signature) ? "<b>" + signature + "</b>\n" : signature;
      return StringUtil.nullize(signature + getCommentText(getCommentsForElement(element), true));
    }
    else if (element instanceof PsiDirectory) {
      return getPackageComment(findDocFileForDirectory(((PsiDirectory)element)));
    }
    return null;
  }

  @Override
  public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
    String referenceText = getReferenceText(adjustDocElement(element));
    if (StringUtil.isNotEmpty(referenceText)) {
      return Collections.singletonList("https://godoc.org/" + referenceText);
    }
    return super.getUrlFor(element, originalElement);
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

  @Override
  public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
    if (context != null && !DumbService.isDumb(psiManager.getProject())) {
      Module module = ModuleUtilCore.findModuleForPsiElement(context);
      int hash = link.indexOf('#');
      String importPath = hash >= 0 ? link.substring(0, hash) : link;
      Project project = psiManager.getProject();
      VirtualFile directory = GoSdkUtil.findFileByRelativeToLibrariesPath(importPath, project, module);
      if (directory != null) {
        PsiDirectory psiDirectory = psiManager.findDirectory(directory);
        if (psiDirectory != null) {
          String anchor = link.substring(Math.min(hash + 1, link.length()));
          if (anchor.isEmpty()) {
            return psiDirectory;
          }
          return ContainerUtil.getFirstItem(GoTypesIndex.find(anchor, project, GlobalSearchScopesCore.directoryScope(psiDirectory, false)));
        }
      }
    }
    return super.getDocumentationElementForLink(psiManager, link, context);
  }
}
