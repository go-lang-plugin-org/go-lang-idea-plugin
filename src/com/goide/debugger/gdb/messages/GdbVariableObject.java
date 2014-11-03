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

package com.goide.debugger.gdb.messages;

import com.goide.debugger.gdb.gdbmi.GdbMiValue;
import com.goide.debugger.gdb.messages.annotations.GdbMiDoneEvent;
import com.goide.debugger.gdb.messages.annotations.GdbMiEnum;
import com.goide.debugger.gdb.messages.annotations.GdbMiField;
import com.goide.debugger.gdb.messages.annotations.GdbMiObject;
import org.jetbrains.annotations.Nullable;

/**
 * A GDB variable object. This is returned from a -var-create request.
 */
@SuppressWarnings("unused")
@GdbMiDoneEvent(command = "-var-create")
@GdbMiObject
public class GdbVariableObject extends GdbDoneEvent {
  /**
   * Possible display hints.
   */
  @GdbMiEnum
  public enum DisplayHint {
    Array,
    Map,
    String
  }

  /**
   * The name of the variable object. Note this is NOT the name of the variable.
   */
  @GdbMiField(name = "name", valueType = GdbMiValue.Type.String)
  public String name;

  /**
   * The expression which the variable object represents.
   */
  @GdbMiField(name = "exp", valueType = GdbMiValue.Type.String)
  public String expression;

  /**
   * The number of children of the object. This is not necessarily reliable for dynamic variable
   * objects, in which case you must use the hasMore field.
   */
  @GdbMiField(name = "numchild", valueType = GdbMiValue.Type.String)
  public Integer numChildren;

  /**
   * The scalar value of the variable. This should not be relied upon for aggregate types
   * (structs, etc.) or for dynamic variable objects.
   */
  @Nullable @GdbMiField(name = "value", valueType = GdbMiValue.Type.String)
  public String value;

  /**
   * The type of the variable. Note this is the derived type of the object, which does not
   * necessarily match the declared type.
   */
  @GdbMiField(name = "type", valueType = GdbMiValue.Type.String)
  public String type;

  /**
   * The thread the variable is bound to, if any.
   */
  @GdbMiField(name = "thread-id", valueType = GdbMiValue.Type.String)
  public Integer threadId;

  /**
   * Whether the variable object is frozen.
   */
  @GdbMiField(name = "frozen", valueType = GdbMiValue.Type.String)
  public Boolean isFrozen;

  /**
   * For dynamic objects this specifies whether there appear to be any more children available.
   */
  @GdbMiField(name = "has_more", valueType = GdbMiValue.Type.String)
  public Boolean hasMore;

  /**
   * Whether this is a dynamic variable object.
   */
  @GdbMiField(name = "dynamic", valueType = GdbMiValue.Type.String)
  public Boolean isDynamic;

  /**
   * A hint about how to display the value.
   */
  @GdbMiField(name = "displayhint", valueType = GdbMiValue.Type.String)
  public DisplayHint displayHint;
}
