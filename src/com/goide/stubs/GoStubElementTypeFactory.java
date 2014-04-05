package com.goide.stubs;

import com.goide.stubs.types.*;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;

public class GoStubElementTypeFactory {
  @NotNull
  public static IStubElementType factory(@NotNull String name) {
    if (name.equals("CONST_DEFINITION")) return new GoConstDefinitionStubElementType(name);
    if (name.equals("FIELD_DEFINITION")) return new GoFieldDefinitionStubElementType(name);
    if (name.equals("ANONYMOUS_FIELD_DEFINITION")) return new GoAnonymousFieldDefinitionStubElementType(name);
    if (name.equals("FUNCTION_DECLARATION")) return new GoFunctionDeclarationStubElementType(name);
    if (name.equals("METHOD_DECLARATION")) return new GoMethodDeclarationStubElementType(name);
    if (name.equals("IMPORT_SPEC")) return new GoImportSpecStubElementType(name);
    if (name.equals("PARAM_DEFINITION")) return new GoParamDefinitionStubElementType(name);
    if (name.equals("RECEIVER")) return new GoReceiverStubElementType(name);
    if (name.equals("TYPE_SPEC")) return new GoTypeSpecStubElementType(name);
    if (name.equals("METHOD_SPEC")) return new GoMethodSpecStubElementType(name);
    if (name.equals("VAR_DEFINITION")) return new GoVarDefinitionStubElementType(name);
    if (name.equals("LABEL_DEFINITION")) return new GoLabelDefinitionStubElementType(name);
    if (name.equals("PARAMETERS")) return new GoParametersStubElementType(name);
    if (name.equals("SIGNATURE")) return new GoSignatureStubElementType(name);

    throw new RuntimeException("Unknown element type: " + name);
  }
}
