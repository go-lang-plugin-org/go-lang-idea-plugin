package com.goide;

import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

public class GoStructureViewFactory implements PsiStructureViewFactory {
  @Nullable
  @Override
  public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
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
    public Model(@NotNull PsiFile psiFile) {
      super(psiFile, new Element(psiFile));
      withSuitableClasses(GoFile.class);
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement structureViewTreeElement) {
      return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement structureViewTreeElement) {
      return false;
    }
  }

  public static class Element implements StructureViewTreeElement, ItemPresentation, NavigationItem {
    private final PsiElement myElement;
    public static final int TRIM_LENGTH = 20;

    public Element(PsiElement element) {
      this.myElement = element;
    }

    @Override
    public Object getValue() {
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
      return myElement.getText();
    }

    @Override
    public ItemPresentation getPresentation() {
      return this;
    }

    @Override
    public TreeElement[] getChildren() {
      ArrayList<TreeElement> result = new ArrayList<TreeElement>();
      if (myElement instanceof GoFile) {
        for (GoTypeSpec o : ((GoFile)myElement).getTypes()) result.add(new Element(o));
        for (GoConstDefinition o : ((GoFile)myElement).getConsts()) result.add(new Element(o));
        for (GoVarDefinition o : ((GoFile)myElement).getVars()) result.add(new Element(o));
        for (GoFunctionDeclaration o : ((GoFile)myElement).getFunctions()) result.add(new Element(o));
        for (GoMethodDeclaration o : ((GoFile)myElement).getMethods()) result.add(new Element(o));
      }
      else if (myElement instanceof GoTypeSpec) {
        GoType type = ((GoTypeSpec)myElement).getType();
        if (type instanceof GoStructType) {
          for (GoFieldDeclaration field : ((GoStructType)type).getFieldDeclarationList()) {
            for (GoFieldDefinition definition : field.getFieldDefinitionList()) {
              result.add(new Element(definition));
            }
            GoAnonymousFieldDefinition anon = field.getAnonymousFieldDefinition();
             if (anon != null) result.add(new Element(anon));
          }
        }
        else if (type instanceof GoInterfaceType) {
          for (GoMethodSpec spec : ((GoInterfaceType)type).getMethodSpecList()) {
            result.add(new Element(spec));
          }
        }
      }
      return result.toArray(new TreeElement[result.size()]);
    }

    @Override
    public String getPresentableText() {
      if (myElement instanceof GoFile) return ((GoFile)myElement).getName();
      else if (myElement instanceof GoFunctionOrMethodDeclaration) {
        String receiver = myElement instanceof GoMethodDeclaration ?
                          ((GoMethodDeclaration)myElement).getReceiver().getType().getText() + "." :
                          "";
        GoSignature signature = ((GoFunctionOrMethodDeclaration)myElement).getSignature();
        String signatureText = signature != null ? signature.getText() : "";
        return receiver + ((GoFunctionOrMethodDeclaration)myElement).getIdentifier().getText() + trim(signatureText);
      }
      else if (myElement instanceof GoTypeSpec) {
        GoType type = ((GoTypeSpec)myElement).getType();
        String appendix = type instanceof GoStructType || type instanceof GoInterfaceType ?
                          "" :
                          (type != null ? ":" + trim(GoPsiImplUtil.getText(type)) : "");
        return ((GoTypeSpec)myElement).getIdentifier().getText() + appendix;
      }
      else if (myElement instanceof GoNamedElement) {
        GoType type = ((GoNamedElement)myElement).getGoType();
        String typeText = type == null ? "" : ":" + trim(GoPsiImplUtil.getText(type));
        return ((GoNamedElement)myElement).getName() + typeText;
      }
      throw new AssertionError(myElement.getClass().getName());
    }

    @NotNull
    private static String trim(@NotNull String text) {
      return StringUtil.first(text, TRIM_LENGTH, true);
    }

    @Nullable
    @Override
    public String getLocationString() {
      return null;
    }

    @Override
    public Icon getIcon(boolean open) {
      if (!myElement.isValid()) return null;
      return myElement.getIcon(0);
    }
  }
}
