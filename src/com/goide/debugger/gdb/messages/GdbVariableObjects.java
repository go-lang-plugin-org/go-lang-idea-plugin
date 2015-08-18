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
import com.goide.debugger.gdb.messages.annotations.GdbMiDoneEvent;
import com.goide.debugger.gdb.messages.annotations.GdbMiField;

import java.util.List;

/**
 * A list of GDB variable objects. This is returned from a -var-list-children request.
 */
@SuppressWarnings("unused")
@GdbMiDoneEvent(command = "-var-list-children")
public class GdbVariableObjects extends GdbDoneEvent {
  /**
   * The objects.
   */
  @GdbMiField(name = "children", valueType = GdbMiValue.Type.List)
  public List<GdbVariableObject> objects;
}
