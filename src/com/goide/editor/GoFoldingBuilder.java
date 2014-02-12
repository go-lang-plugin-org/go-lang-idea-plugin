package com.goide.editor;

import com.goide.GoParserDefinition;
import com.goide.psi.GoBlock;
import com.goide.psi.GoFile;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.GoImportDeclaration;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoFoldingBuilder extends FoldingBuilderEx implements DumbAware {
  @NotNull
  @Override
  public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
    if (!(root instanceof GoFile)) return FoldingDescriptor.EMPTY;
    GoFile file = (GoFile)root;

    final List<FoldingDescriptor> result = ContainerUtil.newArrayList();

    GoImportDeclaration[] imports = PsiTreeUtil.getChildrenOfType(file, GoImportDeclaration.class);
    if (imports != null) {
      for (GoImportDeclaration imp : imports) {
        PsiElement l = imp.getLparen();
        PsiElement r = imp.getRparen();
        if (l != null && r != null) {
          result.add(new FoldingDescriptor(imp, TextRange.create(l.getTextRange().getStartOffset(), r.getTextRange().getEndOffset())));
        }
      }
    }

    for (GoFunctionOrMethodDeclaration method : ContainerUtil.concat(file.getMethods(), file.getFunctions())) {
      GoBlock block = method.getBlock();
      if (block != null) result.add(new FoldingDescriptor(block, block.getTextRange()));
    }

    if (!quick) {
      PsiTreeUtil.processElements(file, new PsiElementProcessor() {
        @Override
        public boolean execute(@NotNull PsiElement element) {
          if (GoParserDefinition.COMMENTS.contains(element.getNode().getElementType()) && element.getTextRange().getLength() > 2) {
            result.add(new FoldingDescriptor(element, element.getTextRange()));
          }
          return true;
        }
      });
    }
    return result.toArray(new FoldingDescriptor[result.size()]);
  }

  @Nullable
  @Override
  public String getPlaceholderText(@NotNull ASTNode node) {
    PsiElement psi = node.getPsi();
    IElementType type = node.getElementType();
    if (psi instanceof GoBlock) return "{ ... }";
    if (psi instanceof GoImportDeclaration) return "...";
    if (GoParserDefinition.LINE_COMMENT == type) return "// ...";
    if (GoParserDefinition.MULTILINE_COMMENT == type) return "/* ... */";
    return null;
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull ASTNode node) {
    return false;
  }
}