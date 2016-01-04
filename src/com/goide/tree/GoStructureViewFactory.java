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

package com.goide.tree;

import com.goide.GoIcons;
import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.ide.structureView.*;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.ActionShortcutProvider;
import com.intellij.ide.util.FileStructureNodeProvider;
import com.intellij.ide.util.treeView.smartTree.*;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GoStructureViewFactory implements PsiStructureViewFactory {
  @Nullable
  @Override
  public StructureViewBuilder getStructureViewBuilder(@NotNull final PsiFile psiFile) {
    return new TreeBasedStructureViewBuilder() {
      @NotNull
      @Override
      public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
        return new Model(psiFile);
      }

      @Override
      public boolean isRootNodeShown() {
        return false;
      }
    };
  }

  public static class Model extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {
    private static final List<NodeProvider> PROVIDERS =
      ContainerUtil.<NodeProvider>newSmartList(new TreeElementFileStructureNodeProvider());

    Model(@NotNull PsiFile file) {
      super(file, new Element(file));
      withSuitableClasses(GoFile.class).withSorters(ExportabilitySorter.INSTANCE, Sorter.ALPHA_SORTER);
    }

    @NotNull
    @Override
    public Filter[] getFilters() {
      return new Filter[]{new GoPrivateMembersFilter()};
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement structureViewTreeElement) {
      return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement structureViewTreeElement) {
      return false;
    }

    @NotNull
    @Override
    public Collection<NodeProvider> getNodeProviders() {
      return PROVIDERS;
    }

    private static class TreeElementFileStructureNodeProvider implements FileStructureNodeProvider<TreeElement>, ActionShortcutProvider {
      public static final String ID = "Show package structure";

      @NotNull
      @Override
      public ActionPresentation getPresentation() {
        return new ActionPresentationData(ID, null, GoIcons.PACKAGE);
      }

      @NotNull
      @Override
      public String getName() {
        return ID;
      }

      @NotNull
      @Override
      public Collection<TreeElement> provideNodes(@NotNull TreeElement node) {
        PsiElement psi = node instanceof Element ? ((Element)node).getElement() : null;
        if (psi instanceof GoFile) {
          GoFile orig = (GoFile)psi;
          List<TreeElement> result = ContainerUtil.newSmartList();
          for (GoFile f : GoPsiImplUtil.getAllPackageFiles(orig)) {
            if (f != orig) {
              ContainerUtil.addAll(result, new Element(f).getChildren());
            }
          }
          return result;
        }
        return Collections.emptyList();
      }

      @NotNull
      @Override
      public String getCheckBoxText() {
        return ID;
      }

      @NotNull
      @Override
      public Shortcut[] getShortcut() {
        throw new IncorrectOperationException("see getActionIdForShortcut()");
      }

      @NotNull
      @Override
      public String getActionIdForShortcut() {
        return "FileStructurePopup";
      }
    }
  }

  public static class Element extends PsiTreeElementBase<PsiElement> {
    public Element(@NotNull PsiElement e) {
      super(e);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
      List<StructureViewTreeElement> result = ContainerUtil.newArrayList();
      PsiElement element = getElement();
      if (element instanceof GoFile) {
        for (GoTypeSpec o : ((GoFile)element).getTypes()) result.add(new Element(o));
        for (GoConstDefinition o : ((GoFile)element).getConstants()) result.add(new Element(o));
        for (GoVarDefinition o : ((GoFile)element).getVars()) result.add(new Element(o));
        for (GoFunctionDeclaration o : ((GoFile)element).getFunctions()) result.add(new Element(o));
        for (GoMethodDeclaration o : ((GoFile)element).getMethods()) {
          GoReceiver receiver = o.getReceiver();
          GoType type = receiver.getType();
          GoTypeReferenceExpression expression = type != null ? type.getTypeReferenceExpression() : null;
          PsiReference reference = expression != null ? expression.getReference() : null;
          PsiElement resolve = reference != null ? reference.resolve() : null;
          if (resolve == null) {
            result.add(new Element(o));
          }
        }
      }
      else if (element instanceof GoTypeSpec) {
        GoTypeSpec typeSpec = (GoTypeSpec)element;
        GoType type = typeSpec.getSpecType().getType();
        for (GoMethodDeclaration m : GoPsiImplUtil.getMethods(typeSpec)) result.add(new Element(m));
        if (type instanceof GoStructType) {
          for (GoFieldDeclaration field : ((GoStructType)type).getFieldDeclarationList()) {
            for (GoFieldDefinition definition : field.getFieldDefinitionList()) result.add(new Element(definition));
            GoAnonymousFieldDefinition anon = field.getAnonymousFieldDefinition();
            if (anon != null) result.add(new Element(anon));
          }
        }
        else if (type instanceof GoInterfaceType) {
          for (GoMethodSpec m : ((GoInterfaceType)type).getMethodSpecList()) result.add(new Element(m));
        }
      }
      return result;
    }

    @Nullable
    @Override
    public String getPresentableText() {
      String textInner = getPresentationTextInner();
      return textInner != null ? textInner.replaceAll("\\(\\n", "(").replaceAll("\\n\\)", ")") : null;
    }

    @Nullable
    private String getPresentationTextInner() {
      PsiElement element = getElement();
      if (element == null) {
        return null;
      }
      String separator = ": ";
      if (element instanceof GoFile) {
        return ((GoFile)element).getName();
      }
      else if (element instanceof GoNamedSignatureOwner) {
        GoSignature signature = ((GoNamedSignatureOwner)element).getSignature();
        String signatureText = signature != null ? signature.getText() : "";
        PsiElement id = ((GoNamedSignatureOwner)element).getIdentifier();
        return (id != null ? id.getText() : "") + signatureText;
      }
      else if (element instanceof GoTypeSpec) {
        GoType type = ((GoTypeSpec)element).getSpecType().getType();
        String appendix = type instanceof GoStructType || type instanceof GoInterfaceType ?
                          "" :
                          separator + GoPsiImplUtil.getText(type);
        return ((GoTypeSpec)element).getName() + appendix;
      }
      else if (element instanceof GoNamedElement) {
        GoType type = ((GoNamedElement)element).getGoType(null);
        String typeText = type == null || element instanceof GoAnonymousFieldDefinition ? "" : separator + GoPsiImplUtil.getText(type);
        return ((GoNamedElement)element).getName() + typeText;
      }
      Logger.getInstance(GoStructureViewFactory.class).error("Cannot get presentation for " + element.getClass().getName());
      return null;
    }
  }
}
