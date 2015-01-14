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

import com.goide.psi.GoType;
import com.goide.psi.GoVarSpec;
import com.goide.psi.impl.*;
import com.goide.stubs.types.*;
import com.intellij.psi.stubs.IStubElementType;
import org.apache.xmlbeans.impl.common.NameUtil;
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
    if (name.equals("VAR_DEFINITION")) return new GoVarDefinitionStubElementType(name);
    if (name.equals("LABEL_DEFINITION")) return new GoLabelDefinitionStubElementType(name);
    if (name.equals("PARAMETERS")) return new GoParametersStubElementType(name);
    if (name.equals("SIGNATURE")) return new GoSignatureStubElementType(name);
    if (name.equals("PARAMETER_DECLARATION")) return new GoParameterDeclarationStubElementType(name);
    if (name.equals("RESULT")) return new GoResultStubElementType(name);

    if (name.equals("ARRAY_OR_SLICE_TYPE")) {
      return new GoTypeStubElementType(name) {
        @NotNull
        @Override
        public GoType createPsi(@NotNull GoTypeStub stub) {
          return new GoArrayOrSliceTypeImpl(stub, this);
        }
      };
    }
    if (name.equals("CHANNEL_TYPE")) {
      return new GoTypeStubElementType(name) {
        @NotNull
        @Override
        public GoType createPsi(@NotNull GoTypeStub stub) {
          return new GoChannelTypeImpl(stub, this);
        }
      };
    }
    if (name.equals("FUNCTION_TYPE")) {
      return new GoTypeStubElementType(name) {
        @NotNull
        @Override
        public GoType createPsi(@NotNull GoTypeStub stub) {
          return new GoFunctionTypeImpl(stub, this);
        }
      };
    }
    if (name.equals("INTERFACE_TYPE")) {
      return new GoTypeStubElementType(name) {
        @NotNull
        @Override
        public GoType createPsi(@NotNull GoTypeStub stub) {
          return new GoInterfaceTypeImpl(stub, this);
        }
      };
    }
    if (name.equals("MAP_TYPE")) {
      return new GoTypeStubElementType(name) {
        @NotNull
        @Override
        public GoType createPsi(@NotNull GoTypeStub stub) {
          return new GoMapTypeImpl(stub, this);
        }
      };
    }
    if (name.equals("POINTER_TYPE")) {
      return new GoTypeStubElementType(name) {
        @NotNull
        @Override
        public GoType createPsi(@NotNull GoTypeStub stub) {
          return new GoPointerTypeImpl(stub, this);
        }
      };
    }
    if (name.equals("RECEIVER_TYPE")) {
      return new GoTypeStubElementType(name) {
        @NotNull
        @Override
        public GoType createPsi(@NotNull GoTypeStub stub) {
          return new GoReceiverTypeImpl(stub, this);
        }
      };
    }
    if (name.equals("STRUCT_TYPE")) {
      return new GoTypeStubElementType(name) {
        @NotNull
        @Override
        public GoType createPsi(@NotNull GoTypeStub stub) {
          return new GoStructTypeImpl(stub, this);
        }
      };
    }
    if (name.equals("TYPE")) {
      return new GoTypeStubElementType(name) {
        @NotNull
        @Override
        public GoType createPsi(@NotNull GoTypeStub stub) {
          return new GoTypeImpl(stub, this);
        }
      };
    }
    if (name.equals("TYPE_LIST")) {
      return new GoTypeStubElementType(name) {
        @NotNull
        @Override
        public GoType createPsi(@NotNull GoTypeStub stub) {
          return new GoTypeListImpl(stub, this);
        }
      };
    }

    throw new RuntimeException("Unknown element type: " + name);
  }
  
  @SuppressWarnings({"unused", "UseOfSystemOutOrSystemErr"})
  private static void generateTypes(@NotNull String name) {
    if (name.contains("TYPE")) {
      String s = NameUtil.upperCamelCase(name.toLowerCase(), true);
      System.out.println("if (name.equals(\"" + name + "\")) {\n" +
                         "      return new GoTypeStubElementType(name) {\n" +
                         "        @NotNull\n" +
                         "        @Override\n" +
                         "        public GoType createPsi(@NotNull GoTypeStub stub) {\n" +
                         "          return new Go" + s + "Impl(stub, this);\n" +
                         "        }\n" +
                         "      };\n" +
                         "    }");
    }
  }
}
