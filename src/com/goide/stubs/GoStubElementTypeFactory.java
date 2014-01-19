package com.goide.stubs;

import com.goide.stubs.types.GoFunctionDeclarationStubElementType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class GoStubElementTypeFactory {
  @NotNull
  public static IElementType factory(@NotNull String name) {
    if (name.equals("FUNCTION_DECLARATION")) return new GoFunctionDeclarationStubElementType(name);


    throw new RuntimeException("Unknown element type: " + name);
  }
}
