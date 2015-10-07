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

package com.goide.debugger.gdb.messages;

import com.goide.debugger.gdb.gdbmi.GdbMiValue;
import com.goide.debugger.gdb.messages.annotations.GdbMiEnum;
import com.goide.debugger.gdb.messages.annotations.GdbMiField;
import com.goide.debugger.gdb.messages.annotations.GdbMiObject;

/**
 * The changes to a GDB variable object since the last update.
 */
@SuppressWarnings("unused")
@GdbMiObject
public class GdbVariableObjectChange {
  /**
   * Possible scope values for the variable object.
   */
  @GdbMiEnum
  public enum InScope {
    /**
     * The variable object is in scope and the value is valid.
     */
    True,
    /**
     * The value is not currently valid, but may come back into scope.
     */
    False,
    /**
     * The variable object is no longer valid because the file being debugged has changed.
     */
    Invalid
  }

  /**
   * The name of the variable object.
   */
  @GdbMiField(name = "name", valueType = GdbMiValue.Type.String)
  public String name;

  /**
   * The value of the variable.
   */
  @GdbMiField(name = "value", valueType = GdbMiValue.Type.String)
  public String value;

  /**
   * Whether the variable object is still in scope.
   */
  @GdbMiField(name = "in_scope", valueType = GdbMiValue.Type.String)
  public InScope inScope;

  /**
   * Whether the value's type has changed since the last update
   */
  @GdbMiField(name = "type_changed", valueType = GdbMiValue.Type.String)
  public Boolean typeChanged;

  /**
   * The new type, if typeChanged is true.
   */
  @GdbMiField(name = "new_type", valueType = GdbMiValue.Type.String)
  public String newType;

  /**
   * The new number of children of the object.
   */
  @GdbMiField(name = "new_num_children", valueType = GdbMiValue.Type.String)
  public Integer newNumChildren;

  /**
   * A hint about how to display the value.
   */
  @GdbMiField(name = "displayhint", valueType = GdbMiValue.Type.String)
  public GdbVariableObject.DisplayHint displayHint;

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

  // TODO: What does this field contain?
  //@GdbMiField(name = "new_children", valueType = GdbMiValue.Type.??)
  //public List<??> newChildren;
}
