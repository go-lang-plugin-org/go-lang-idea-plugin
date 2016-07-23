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

package com.goide;

import com.goide.editor.GoParameterInfoHandler;
import com.goide.project.GoVendoringUtil;
import com.goide.psi.*;
import com.goide.psi.impl.GoCType;
import com.goide.psi.impl.GoLightType;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.sdk.GoPackageUtil;
import com.goide.sdk.GoSdkUtil;
import com.goide.stubs.index.GoAllPrivateNamesIndex;
import com.goide.stubs.index.GoAllPublicNamesIndex;
import com.goide.stubs.index.GoIdFilter;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.documentation.DocumentationManagerProtocol;
import com.intellij.lang.ASTNode;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.IdFilter;
import com.intellij.xml.util.XmlStringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GoDocumentationProvider extends AbstractDocumentationProvider {
  private static final Logger LOG = Logger.getInstance(GoDocumentationProvider.class);
  private static final GoCommentsConverter COMMENTS_CONVERTER = new GoCommentsConverter();

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
      return (GoFile)file;
    }
    PsiFile directoryFile = directory.findFile(GoUtil.suggestPackageForDirectory(directory) + ".go");
    return directoryFile instanceof GoFile ? (GoFile)directoryFile : null;
  }

  @Nullable
  private static String getPackageComment(@Nullable GoFile file) {
    if (file != null) {
      boolean vendoringEnabled = GoVendoringUtil.isVendoringEnabled(ModuleUtilCore.findModuleForPsiElement(file));
      // todo: remove after correct stubbing (comments needed in stubs)
      GoPackageClause pack = PsiTreeUtil.findChildOfType(file, GoPackageClause.class);
      String title = "<b>Package " + GoUtil.suggestPackageForDirectory(file.getParent()) + "</b>\n";
      String importPath = "<p><code>import \"" + StringUtil.notNullize(file.getImportPath(vendoringEnabled)) + "\"</code></p>\n";
      return title + importPath + getCommentText(getCommentsForElement(pack), true);
    }
    return null;
  }

  @Nullable
  private static PsiElement adjustDocElement(@Nullable PsiElement element) {
    return element instanceof GoImportSpec ? ((GoImportSpec)element).getImportString().resolve() : element;
  }

  @NotNull
  private static String getSignature(PsiElement element, PsiElement context) {
    if (element instanceof GoTypeSpec) {
      String name = ((GoTypeSpec)element).getName();
      return StringUtil.isNotEmpty(name) ? "type " + name : "";
    }
    GoDocumentationPresentationFunction presentationFunction = new GoDocumentationPresentationFunction(getImportPathForElement(element));
    if (element instanceof GoConstDefinition) {
      String name = ((GoConstDefinition)element).getName();
      if (StringUtil.isNotEmpty(name)) {
        String type = getTypePresentation(((GoConstDefinition)element).getGoType(null), presentationFunction);
        GoExpression value = ((GoConstDefinition)element).getValue();
        return "const " + name + (!type.isEmpty() ? " " + type : "") + (value != null ? " = " + value.getText() : "");
      }
    }
    if (element instanceof GoVarDefinition) {
      String name = ((GoVarDefinition)element).getName();
      if (StringUtil.isNotEmpty(name)) {
        String type = getTypePresentation(((GoVarDefinition)element).getGoType(GoPsiImplUtil.createContextOnElement(context)), presentationFunction);
        GoExpression value = ((GoVarDefinition)element).getValue();
        return "var " + name + (!type.isEmpty() ? " " + type : "") + (value != null ? " = " + value.getText() : "");
      }
    }
    if (element instanceof GoParamDefinition) {
      String name = ((GoParamDefinition)element).getName();
      if (StringUtil.isNotEmpty(name)) {
        String type = getTypePresentation(((GoParamDefinition)element).getGoType(GoPsiImplUtil.createContextOnElement(context)), presentationFunction);
        return "var " + name + (!type.isEmpty() ? " " + type : "");
      }
    }
    if (element instanceof GoReceiver) {
      String name = ((GoReceiver)element).getName();
      if (StringUtil.isNotEmpty(name)) {
        String type = getTypePresentation(((GoReceiver)element).getGoType(GoPsiImplUtil.createContextOnElement(context)), presentationFunction);
        return "var " + name + (!type.isEmpty() ? " " + type : "");
      }
    }


    return element instanceof GoSignatureOwner ? getSignatureOwnerTypePresentation((GoSignatureOwner)element, presentationFunction) : "";
  }

  @NotNull
  private static String getSignatureOwnerTypePresentation(@NotNull GoSignatureOwner signatureOwner,
                                                          @NotNull Function<PsiElement, String> presentationFunction) {
    PsiElement identifier = null;
    if (signatureOwner instanceof GoNamedSignatureOwner) {
      identifier = ((GoNamedSignatureOwner)signatureOwner).getIdentifier();
    }
    GoSignature signature = signatureOwner.getSignature();

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
      builder.append(' ').append(getTypePresentation(type, presentationFunction));
    }
    return builder.toString();
  }

  @NotNull
  private static String getParametersAsString(@NotNull GoParameters parameters) {
    String contextImportPath = getImportPathForElement(parameters);
    return StringUtil.join(GoParameterInfoHandler.getParameterPresentations(parameters, element -> getTypePresentation(element, new GoDocumentationPresentationFunction(contextImportPath))), ", ");
  }

  @Nullable
  private static String getImportPathForElement(@Nullable PsiElement element) {
    PsiFile file = element != null ? element.getContainingFile() : null;
    return file instanceof GoFile ? ((GoFile)file).getImportPath(false) : null;
  }

  @NotNull
  public static String getTypePresentation(@Nullable PsiElement element, @NotNull Function<PsiElement, String> presentationFunction) {
    if (element instanceof GoType) {
      GoType type = (GoType)element;
      if (type instanceof GoMapType) {
        GoType keyType = ((GoMapType)type).getKeyType();
        GoType valueType = ((GoMapType)type).getValueType();
        return "map[" + getTypePresentation(keyType, presentationFunction) + "]" +
               getTypePresentation(valueType, presentationFunction);
      }
      if (type instanceof GoChannelType) {
        ASTNode typeNode = type.getNode();
        GoType innerType = ((GoChannelType)type).getType();
        ASTNode innerTypeNode = innerType != null ? innerType.getNode() : null;
        if (typeNode != null && innerTypeNode != null) {
          StringBuilder result = new StringBuilder();
          for (ASTNode node : typeNode.getChildren(null)) {
            if (node.equals(innerTypeNode)) {
              break;
            }
            if (node.getElementType() != TokenType.WHITE_SPACE) {
              result.append(XmlStringUtil.escapeString(node.getText()));
            }
          }
          result.append(" ").append(getTypePresentation(innerType, presentationFunction));
          return result.toString();
        }
      }
      if (type instanceof GoParType) {
        return "(" + getTypePresentation(((GoParType)type).getActualType(), presentationFunction) + ")";
      }
      if (type instanceof GoArrayOrSliceType) {
        return "[]" + getTypePresentation(((GoArrayOrSliceType)type).getType(), presentationFunction);
      }
      if (type instanceof GoPointerType) {
        GoType inner = ((GoPointerType)type).getType();
        return inner instanceof GoSpecType ? getTypePresentation(inner, presentationFunction)
                                           : "*" + getTypePresentation(inner, presentationFunction);
      }
      if (type instanceof GoTypeList) {
        return "(" + StringUtil.join(((GoTypeList)type).getTypeList(), element1 -> getTypePresentation(element1, presentationFunction), ", ") + ")";
      }
      if (type instanceof GoFunctionType) {
        return getSignatureOwnerTypePresentation((GoFunctionType)type, presentationFunction);
      }
      if (type instanceof GoSpecType) {
        return getTypePresentation(GoPsiImplUtil.getTypeSpecSafe(type), presentationFunction);
      }
      if (type instanceof GoCType) {
        return "C";
      }
      if (type instanceof GoLightType) {
        LOG.error("Cannot build presentable text for type: " + type.getClass().getSimpleName() + " - " + type.getText());
        return "";
      }
      if (type instanceof GoStructType) {
        StringBuilder result = new StringBuilder("struct {");
        result.append(StringUtil.join(((GoStructType)type).getFieldDeclarationList(), declaration -> {
          GoAnonymousFieldDefinition anon = declaration.getAnonymousFieldDefinition();
          String fieldString = anon != null ? getTypePresentation(anon.getGoTypeInner(null), presentationFunction)
                                            : StringUtil.join(declaration.getFieldDefinitionList(), PsiElement::getText, ", ") +
                                              " " + getTypePresentation(declaration.getType(), presentationFunction);
          GoTag tag = declaration.getTag();
          return fieldString + (tag != null ? tag.getText() : "");
        }, "; "));
        return result.append("}").toString();
      }
      GoTypeReferenceExpression typeRef = GoPsiImplUtil.getTypeReference(type);
      if (typeRef != null) {
        PsiElement resolve = typeRef.resolve();
        if (resolve != null) {
          return getTypePresentation(resolve, presentationFunction);
        }
      }
    }

    return presentationFunction.fun(element);
  }

  @Nullable
  public static String getLocalUrlToElement(@NotNull PsiElement element) {
    if (element instanceof GoTypeSpec || element instanceof PsiDirectory) {
      return getReferenceText(element, true);
    }
    return null;
  }

  @Nullable
  private static String getReferenceText(@Nullable PsiElement element, boolean includePackageName) {
    if (element instanceof GoNamedElement) {
      PsiFile file = element.getContainingFile();
      if (file instanceof GoFile) {
        String importPath = ((GoFile)file).getImportPath(false);
        if (element instanceof GoFunctionDeclaration || element instanceof GoTypeSpec) {
          String name = ((GoNamedElement)element).getName();
          if (StringUtil.isNotEmpty(name)) {
            String fqn = getElementFqn((GoNamedElement)element, name, includePackageName);
            return String.format("%s#%s", StringUtil.notNullize(importPath), fqn);
          }
        }
        else if (element instanceof GoMethodDeclaration) {
          GoType receiverType = ((GoMethodDeclaration)element).getReceiverType();
          String receiver = getReceiverTypeText(receiverType);
          String name = ((GoMethodDeclaration)element).getName();
          if (StringUtil.isNotEmpty(receiver) && StringUtil.isNotEmpty(name)) {
            String fqn = getElementFqn((GoNamedElement)element, name, includePackageName);
            return String.format("%s#%s.%s", StringUtil.notNullize(importPath), receiver, fqn);
          }
        }
      }
    }
    else if (element instanceof PsiDirectory && findDocFileForDirectory((PsiDirectory)element) != null) {
      return GoSdkUtil.getImportPath((PsiDirectory)element, false);
    }

    return null;
  }

  private static String getReceiverTypeText(@Nullable GoType type) {
    if (type == null) return null;
    if (type instanceof GoPointerType) {
      GoType inner = ((GoPointerType)type).getType();
      if (inner != null) return inner.getText();
    }
    return type.getText();
  }

  @NotNull
  private static String getElementFqn(@NotNull GoNamedElement element, @NotNull String name, boolean includePackageName) {
    if (includePackageName) {
      String packageName = element.getContainingFile().getPackageName();
      if (!StringUtil.isEmpty(packageName)) {
        return packageName + "." + name;
      }
    }
    return name;
  }

  @Override
  public String generateDoc(PsiElement element, PsiElement originalElement) {
    element = adjustDocElement(element);
    if (element instanceof GoNamedElement) {
      String signature = getSignature(element, originalElement);
      signature = StringUtil.isNotEmpty(signature) ? "<b>" + signature + "</b>\n" : signature;
      return StringUtil.nullize(signature + getCommentText(getCommentsForElement(element), true));
    }
    if (element instanceof PsiDirectory) {
      return getPackageComment(findDocFileForDirectory((PsiDirectory)element));
    }
    return null;
  }

  @Override
  public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
    String referenceText = getReferenceText(adjustDocElement(element), false);
    if (StringUtil.isNotEmpty(referenceText)) {
      return Collections.singletonList("https://godoc.org/" + referenceText);
    }
    return super.getUrlFor(element, originalElement);
  }

  @Nullable
  @Override
  public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
    if (element instanceof GoNamedElement) {
      String result = getSignature(element, originalElement);
      if (StringUtil.isNotEmpty(result)) return result;
    }
    return super.getQuickNavigateInfo(element, originalElement);
  }

  @Override
  public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
    if (context != null && !DumbService.isDumb(psiManager.getProject())) {
      // it's important to ask module on file, otherwise module won't be found for elements in libraries files [zolotov]
      Module module = ModuleUtilCore.findModuleForPsiElement(context.getContainingFile());
      int hash = link.indexOf('#');
      String importPath = hash >= 0 ? link.substring(0, hash) : link;
      Project project = psiManager.getProject();
      VirtualFile directory = GoPackageUtil.findByImportPath(importPath, project, module);
      PsiDirectory psiDirectory = directory != null ? psiManager.findDirectory(directory) : null;
      String anchor = hash >= 0 ? link.substring(Math.min(hash + 1, link.length())) : null;
      if (StringUtil.isNotEmpty(anchor)) {
        GlobalSearchScope scope = psiDirectory != null ? GoPackageUtil.packageScope(psiDirectory, null)
                                                       : GlobalSearchScope.projectScope(project);
        IdFilter idFilter = GoIdFilter.getFilesFilter(scope);
        GoNamedElement element = ContainerUtil.getFirstItem(StubIndex.getElements(GoAllPublicNamesIndex.ALL_PUBLIC_NAMES, anchor, project,
                                                                                  scope, idFilter, GoNamedElement.class));
        if (element != null) {
          return element;
        }
        return ContainerUtil.getFirstItem(StubIndex.getElements(GoAllPrivateNamesIndex.ALL_PRIVATE_NAMES, anchor, project, scope, idFilter,
                                                                GoNamedElement.class));
      }
      else {
        return psiDirectory;
      }
    }
    return super.getDocumentationElementForLink(psiManager, link, context);
  }

  private static class GoDocumentationPresentationFunction implements Function<PsiElement, String> {
    private final String myContextImportPath;

    public GoDocumentationPresentationFunction(@Nullable String contextImportPath) {
      myContextImportPath = contextImportPath;
    }

    @Override
    public String fun(PsiElement element) {
      if (element instanceof GoTypeSpec) {
        String localUrl = getLocalUrlToElement(element);
        String name = ((GoTypeSpec)element).getName();
        if (StringUtil.isNotEmpty(name)) {
          String importPath = getImportPathForElement(element);
          GoFile file = ((GoTypeSpec)element).getContainingFile();
          String packageName = file.getPackageName();
          if (StringUtil.isNotEmpty(packageName) &&
              !GoPsiImplUtil.isBuiltinFile(file) &&
              !Comparing.equal(importPath, myContextImportPath)) {
            return String.format("<a href=\"%s%s\">%s</a>.<a href=\"%s%s\">%s</a>",
                                 DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL, StringUtil.notNullize(importPath), packageName,
                                 DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL, localUrl, name);
          }
          return String.format("<a href=\"%s%s\">%s</a>", DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL, localUrl, name);
        }
      }
      return element != null ? element.getText() : "";
    }
  }
}