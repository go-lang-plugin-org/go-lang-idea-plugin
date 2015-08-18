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

package yaml;

import java.util.Collections;
import java.util.EnumSet;

public interface YamlConstants {
  enum Value {
    REQUIRED, ADMIN, 
    UNAUTHORIZED, REDIRECT, 
    OPTIONAL, NEVER, ALWAYS,
    TRUE, FALSE
  }

  enum Key {
    APPLICATION(true, true),
    HANDLERS(true, true),
    VERSION(false, true),
    RUNTIME(true, true),
    API_VERSION(true, true),
    URL(true, false),
    THREADSAFE(false, true, Value.TRUE, Value.FALSE),
    DEFAULT_EXPIRATION(false, true),
    BUILTINS(false, true),
    INCLUDES(false, true),
    INBOUND_SERVICES(false, true),
    ADMIN_CONSOLE(false, true),
    ERROR_HANDLERS(false, true),
    MIME_TYPE(false, false),
    EXPIRATION(false, false),
    UPLOAD(false, false),
    AUTH_FAIL_ACTION(false, false, Value.UNAUTHORIZED, Value.REDIRECT),
    SECURE(false, false, Value.OPTIONAL, Value.NEVER, Value.ALWAYS),
    LOGIN(false, false, Value.REQUIRED, Value.ADMIN),
    SKIP_FILES(false, true),

    TYPE_SCRIPT(true, false),
    TYPE_STATIC_DIR(true, false),
    TYPE_STATIC_FILES(true, false);

    private final boolean myRequired;
    private final boolean myTopLevel;
    private final EnumSet<Value> myValues;

    Key(boolean required, boolean topLevel, Value... values) {
      myRequired = required;
      myTopLevel = topLevel;
      myValues = EnumSet.noneOf(Value.class);
      Collections.addAll(myValues, values);
    }
  }
}
