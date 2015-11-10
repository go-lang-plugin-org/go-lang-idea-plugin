/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

import com.goide.psi.GoType;
import com.goide.psi.GoVarSpec;
import com.goide.psi.impl.*;
import com.goide.stubs.types.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.ReflectionUtil;
import com.intellij.util.containers.HashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GoStubElementTypeFactory {
  private static Map<String, Class> TYPES = new HashMap<String, Class>() {
    {
      put("ARRAY_OR_SLICE_TYPE", GoArrayOrSliceTypeImpl.class);
      put("CHANNEL_TYPE", GoChannelTypeImpl.class);
      put("FUNCTION_TYPE", GoFunctionTypeImpl.class);
      put("INTERFACE_TYPE", GoInterfaceTypeImpl.class);
      put("MAP_TYPE", GoMapTypeImpl.class);
      put("POINTER_TYPE", GoPointerTypeImpl.class);
      put("RECEIVER_TYPE", GoReceiverTypeImpl.class);
      put("STRUCT_TYPE", GoStructTypeImpl.class);
      put("TYPE", GoTypeImpl.class);
      put("PAR_TYPE", GoParTypeImpl.class);
      put("SPEC_TYPE", GoSpecTypeImpl.class);
      put("TYPE_LIST", GoTypeListImpl.class);
    }
  };

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
    if (name.equals("CONST_SPEC")) return new GoConstSpecStubElementType(name);
    if (name.equals("VAR_SPEC")) return new GoVarSpecStubElementType(name);
    if (name.equals("SHORT_VAR_DECLARATION")) return new GoVarSpecStubElementType(name) {
      @NotNull
      @Override
      public GoVarSpec createPsi(@NotNull GoVarSpecStub stub) {
        return new GoShortVarDeclarationImpl(stub, this);
      }
    };
    if (name.equals("RECV_STATEMENT")) return new GoVarSpecStubElementType(name) {
      @NotNull
      @Override
      public GoVarSpec createPsi(@NotNull GoVarSpecStub stub) {
        return new GoRecvStatementImpl(stub, this);
      }
    };
    if (name.equals("RANGE_CLAUSE")) return new GoVarSpecStubElementType(name) {
      @NotNull
      @Override
      public GoVarSpec createPsi(@NotNull GoVarSpecStub stub) {
        return new GoRangeClauseImpl(stub, this);
      }
    };
    if (name.equals("VAR_DEFINITION")) return new GoVarDefinitionStubElementType(name);
    if (name.equals("LABEL_DEFINITION")) return new GoLabelDefinitionStubElementType(name);
    if (name.equals("PARAMETERS")) return new GoParametersStubElementType(name);
    if (name.equals("SIGNATURE")) return new GoSignatureStubElementType(name);
    if (name.equals("PARAMETER_DECLARATION")) return new GoParameterDeclarationStubElementType(name);
    if (name.equals("RESULT")) return new GoResultStubElementType(name);

    final Class c = TYPES.get(name);
    if (c != null) {
      return new GoTypeStubElementType(name) {
        @NotNull
        @Override
        public GoType createPsi(@NotNull GoTypeStub stub) {
          try {
            //noinspection unchecked
            return (GoType)ReflectionUtil.createInstance(c.getConstructor(stub.getClass(), IStubElementType.class), stub, this);
          }
          catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
          }
        }
      };
    }
    throw new RuntimeException("Unknown element type: " + name);
  }
}
