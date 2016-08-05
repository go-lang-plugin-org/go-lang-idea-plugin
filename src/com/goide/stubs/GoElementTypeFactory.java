/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

public class GoElementTypeFactory {
  private static final Map<String, Class> TYPES = new HashMap<String, Class>() {
    {
      put("ARRAY_OR_SLICE_TYPE", GoArrayOrSliceTypeImpl.class);
      put("CHANNEL_TYPE", GoChannelTypeImpl.class);
      put("FUNCTION_TYPE", GoFunctionTypeImpl.class);
      put("INTERFACE_TYPE", GoInterfaceTypeImpl.class);
      put("MAP_TYPE", GoMapTypeImpl.class);
      put("POINTER_TYPE", GoPointerTypeImpl.class);
      put("STRUCT_TYPE", GoStructTypeImpl.class);
      put("TYPE", GoTypeImpl.class);
      put("PAR_TYPE", GoParTypeImpl.class);
      put("SPEC_TYPE", GoSpecTypeImpl.class);
      put("TYPE_LIST", GoTypeListImpl.class);
    }
  };

  private GoElementTypeFactory() {}

  public static IStubElementType stubFactory(@NotNull String name) {
    if ("CONST_DEFINITION".equals(name)) return new GoConstDefinitionStubElementType(name);
    if ("FIELD_DEFINITION".equals(name)) return new GoFieldDefinitionStubElementType(name);
    if ("ANONYMOUS_FIELD_DEFINITION".equals(name)) return new GoAnonymousFieldDefinitionStubElementType(name);
    if ("FUNCTION_DECLARATION".equals(name)) return new GoFunctionDeclarationStubElementType(name);
    if ("METHOD_DECLARATION".equals(name)) return new GoMethodDeclarationStubElementType(name);
    if ("IMPORT_SPEC".equals(name)) return new GoImportSpecStubElementType(name);
    if ("PARAM_DEFINITION".equals(name)) return new GoParamDefinitionStubElementType(name);
    if ("RECEIVER".equals(name)) return new GoReceiverStubElementType(name);
    if ("TYPE_SPEC".equals(name)) return new GoTypeSpecStubElementType(name);
    if ("METHOD_SPEC".equals(name)) return new GoMethodSpecStubElementType(name);
    if ("CONST_SPEC".equals(name)) return new GoConstSpecStubElementType(name);
    if ("PACKAGE_CLAUSE".equals(name)) return GoPackageClauseStubElementType.INSTANCE;
    if ("VAR_SPEC".equals(name)) return new GoVarSpecStubElementType(name);
    if ("SHORT_VAR_DECLARATION".equals(name)) return new GoVarSpecStubElementType(name) {
      @NotNull
      @Override
      public GoVarSpec createPsi(@NotNull GoVarSpecStub stub) {
        return new GoShortVarDeclarationImpl(stub, this);
      }
    };
    if ("RECV_STATEMENT".equals(name)) return new GoVarSpecStubElementType(name) {
      @NotNull
      @Override
      public GoVarSpec createPsi(@NotNull GoVarSpecStub stub) {
        return new GoRecvStatementImpl(stub, this);
      }
    };
    if ("RANGE_CLAUSE".equals(name)) return new GoVarSpecStubElementType(name) {
      @NotNull
      @Override
      public GoVarSpec createPsi(@NotNull GoVarSpecStub stub) {
        return new GoRangeClauseImpl(stub, this);
      }
    };
    if ("VAR_DEFINITION".equals(name)) return new GoVarDefinitionStubElementType(name);
    if ("LABEL_DEFINITION".equals(name)) return new GoLabelDefinitionStubElementType(name);
    if ("PARAMETERS".equals(name)) return new GoParametersStubElementType(name);
    if ("SIGNATURE".equals(name)) return new GoSignatureStubElementType(name);
    if ("PARAMETER_DECLARATION".equals(name)) return new GoParameterDeclarationStubElementType(name);
    if ("RESULT".equals(name)) return new GoResultStubElementType(name);

    Class c = TYPES.get(name);
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
