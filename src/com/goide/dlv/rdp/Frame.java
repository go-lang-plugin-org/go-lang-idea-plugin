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

import org.jetbrains.annotations.Nullable;
import org.jetbrains.jsonProtocol.JsonField;
import org.jetbrains.jsonProtocol.JsonOptionalField;

public interface Frame {
  interface Function {
    @JsonOptionalField
    @Nullable
    String name();

    @JsonOptionalField
    @Nullable
    String displayName();

    @JsonOptionalField
    @Nullable
    String userDisplayName();
  }

  interface Environment {
    enum Type {
      OBJECT, FUNCTION, WITH, BLOCK, NO_ENUM_CONST
    }

    String actor();

    Type type();

    @JsonOptionalField
    @Nullable
    Bindings bindings();

    @JsonOptionalField
    @Nullable
    Function function();

    @JsonOptionalField
    @Nullable
    Grip object();

    @JsonOptionalField
    @Nullable
    Environment parent();
  }

  String actor();

  Environment environment();

  @JsonField(name = "this")
  Grip receiver();

  Location where();

  interface SourceYetAnotherPoorFirefoxRdpStructure {
    String actor();
  }

  // Firefox RDP is ugly - script is not specified in paused event data
  @JsonOptionalField
  @Nullable
  SourceYetAnotherPoorFirefoxRdpStructure source();
}
