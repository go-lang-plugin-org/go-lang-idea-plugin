/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

package com.goide.dlv.rdp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jsonProtocol.JsonField;
import org.jetbrains.jsonProtocol.JsonOptionalField;

import java.util.Map;

public interface Grip {
  enum Type {
    NULL, UNDEFINED, OBJECT, NA_N, INFINITY, NO_ENUM_CONST
  }

  interface Preview {
    enum Kind {
      OBJECT, DOM_NODE, DOM_EVENT, OBJECT_WITH_URL, ARRAY_LIKE, MAP_LIKE, ERROR, NO_ENUM_CONST
    }

    long timestamp();

    @JsonOptionalField
    @Nullable
    Kind kind();

    @JsonOptionalField
    @Nullable
    String name();

    @JsonOptionalField
    @Nullable
    String message();

    @JsonOptionalField
    int length();

    @JsonOptionalField
    @Nullable
    Map<String, PropertyDescriptor> ownProperties();

    @JsonOptionalField
    @Nullable
    Map<String, SafeGetterValue> safeGetterValues();

    @JsonOptionalField
    int ownPropertiesLength();
  }

  @JsonOptionalField
  @Nullable
  String actor();

  @NotNull
  Type type();

  @Nullable
  @JsonField(name = "class", optional = true)
  String className();

  @Nullable
  @JsonOptionalField
  String displayString();

  @JsonOptionalField
  @Nullable
  Preview preview();
}
