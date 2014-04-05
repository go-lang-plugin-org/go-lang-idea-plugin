package com.goide.psi.impl;

import com.goide.GoTypes;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.GoSignature;
import com.goide.psi.GoType;
import com.goide.stubs.GoFunctionOrMethodDeclarationStub;
import com.goide.stubs.types.GoSignatureStubElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class GoFunctionOrMethodDeclarationImpl<T extends GoFunctionOrMethodDeclarationStub<?>> extends GoNamedElementImpl<T>
  implements GoFunctionOrMethodDeclaration {
  public GoFunctionOrMethodDeclarationImpl(@NotNull T stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public GoFunctionOrMethodDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Nullable
  @Override
  public GoSignature getSignatureSafe() {
    StubElement stub = getStub();
    if (stub != null) {
      PsiElement[] array = stub.getChildrenByType(GoTypes.SIGNATURE, GoSignatureStubElementType.ARRAY_FACTORY);
      return (GoSignature)ArrayUtil.getFirstElement(array);
    }
    return getSignature();
  }

  public GoType getGoType() {
    return GoPsiImplUtil.getGoType(this);
  }
}
