/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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
    if (name.equals("PARAMETER_DECLARATION")) return new GoParameterDeclarationStubElementType(name);
    if (name.equals("RESULT")) return new GoResultStubElementType(name);

    throw new RuntimeException("Unknown element type: " + name);
  }
}
