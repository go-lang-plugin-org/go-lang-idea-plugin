/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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
import com.intellij.ide.util.ActionShortcutProvider;
import com.intellij.ide.util.FileStructureNodeProvider;
import com.intellij.ide.util.treeView.smartTree.*;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Iconable;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
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

    public static final List<NodeProvider> PROVIDERS = ContainerUtil.<NodeProvider>newSmartList(new TreeElementFileStructureNodeProvider());

    public Model(@NotNull PsiFile psiFile) {
      super(psiFile, new Element(psiFile));
      withSuitableClasses(GoFile.class).withSorters(ExportabilitySorter.INSTANCE, Sorter.ALPHA_SORTER);
    }

    @NotNull
    @Override
    public Filter[] getFilters() {
      return new Filter[] {new GoPrivateMembersFilter()};
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
        PsiElement psi = node instanceof Element ? ((Element)node).myElement : null;
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

  public static class Element implements StructureViewTreeElement, ItemPresentation, NavigationItem {
    @NotNull private final PsiElement myElement;

    public Element(@NotNull PsiElement element) {
      myElement = element;
    }

    @NotNull
    @Override
    public PsiElement getValue() {
      return myElement;
    }

    @Override
    public void navigate(boolean requestFocus) {
      ((Navigatable)myElement).navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
      return ((Navigatable)myElement).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
      return ((Navigatable)myElement).canNavigateToSource();
    }

    @Nullable
    @Override
    public String getName() {
      if (myElement instanceof GoNamedElement) return ((GoNamedElement)myElement).getName();
      return myElement.getText();
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
      return this;
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
      List<TreeElement> result = ContainerUtil.newArrayList();
      if (myElement instanceof GoFile) {
        for (GoTypeSpec o : ((GoFile)myElement).getTypes()) result.add(new Element(o));
        for (GoConstDefinition o : ((GoFile)myElement).getConstants()) result.add(new Element(o));
        for (GoVarDefinition o : ((GoFile)myElement).getVars()) result.add(new Element(o));
        for (GoFunctionDeclaration o : ((GoFile)myElement).getFunctions()) result.add(new Element(o));
      }
      else if (myElement instanceof GoTypeSpec) {
        GoTypeSpec typeSpec = (GoTypeSpec)myElement;
        GoType type = (typeSpec).getType();
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
      return result.toArray(new TreeElement[result.size()]);
    }

    @Override
    public String getPresentableText() {
      String separator = ": ";      
      if (myElement instanceof GoFile) return ((GoFile)myElement).getName();
      else if (myElement instanceof GoFunctionOrMethodDeclaration) {
        GoSignature signature = ((GoFunctionOrMethodDeclaration)myElement).getSignature();
        String signatureText = signature != null ? signature.getText() : "";
        PsiElement id = ((GoFunctionOrMethodDeclaration)myElement).getIdentifier();
        return (id != null ? id.getText() : "") + signatureText;
      }
      else if (myElement instanceof GoTypeSpec) {
        GoType type = ((GoTypeSpec)myElement).getType();
        String appendix = type instanceof GoStructType || type instanceof GoInterfaceType ?
                          "" :
                          (type != null ? separator + GoPsiImplUtil.getText(type) : "");
        return ((GoTypeSpec)myElement).getName() + appendix;
      }
      else if (myElement instanceof GoNamedElement) {
        GoType type = ((GoNamedElement)myElement).getGoType(null);
        String typeText = type == null || myElement instanceof GoAnonymousFieldDefinition ? "" : separator + GoPsiImplUtil.getText(type);
        return ((GoNamedElement)myElement).getName() + typeText;
      }
      throw new AssertionError(myElement.getClass().getName());
    }

    @Nullable
    @Override
    public String getLocationString() {
      return null;
    }

    @Override
    public Icon getIcon(boolean open) {
      if (!myElement.isValid()) return null;
      return myElement.getIcon(Iconable.ICON_FLAG_VISIBILITY);
    }
  }
}
