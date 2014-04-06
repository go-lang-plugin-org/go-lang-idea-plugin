package com.goide.psi.impl;

import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.GoType;
import com.goide.stubs.GoFunctionOrMethodDeclarationStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;

abstract public class GoFunctionOrMethodDeclarationImpl<T extends GoFunctionOrMethodDeclarationStub<?>> extends GoNamedElementImpl<T>
  implements GoFunctionOrMethodDeclaration {
  public GoFunctionOrMethodDeclarationImpl(@NotNull T stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public GoFunctionOrMethodDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public GoType getGoType() {
    return GoPsiImplUtil.getGoType(this);
  }
}