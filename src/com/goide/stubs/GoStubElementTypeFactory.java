package com.goide.stubs;

import com.goide.stubs.types.GoConstDefinitionStubElementType;
import com.goide.stubs.types.GoFieldDefinitionStubElementType;
import com.goide.stubs.types.GoFunctionDeclarationStubElementType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class GoStubElementTypeFactory {
  @NotNull
  public static IElementType factory(@NotNull String name) {
    if (name.equals("FUNCTION_DECLARATION")) return new GoFunctionDeclarationStubElementType(name);
    if (name.equals("CONST_DEFINITION")) return new GoConstDefinitionStubElementType(name);
    if (name.equals("FIELD_DEFINITION")) return new GoFieldDefinitionStubElementType(name);

    throw new RuntimeException("Unknown element type: " + name);
  }
}
