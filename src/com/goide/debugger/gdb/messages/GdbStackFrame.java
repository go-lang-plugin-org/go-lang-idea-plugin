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
import com.goide.debugger.gdb.messages.annotations.GdbMiField;
import com.goide.debugger.gdb.messages.annotations.GdbMiObject;

import java.util.Map;

/**
 * Class representing information about a stack frame from GDB.
 */
@SuppressWarnings("unused")
@GdbMiObject
public class GdbStackFrame {
  /**
   * The position of the frame within the stack, where zero is the top of the stack.
   */
  @GdbMiField(name = "level", valueType = GdbMiValue.Type.String)
  public Integer level;

  /**
   * The execution address.
   */
  @GdbMiField(name = "addr", valueType = GdbMiValue.Type.String,
              valueProcessor = "com.goide.debugger.gdb.messages.GdbMiMessageConverterUtils.hexStringToLong")
  public Long address;

  /**
   * The name of the function.
   */
  @GdbMiField(name = "func", valueType = GdbMiValue.Type.String, valueProcessor =
    "com.goide.debugger.gdb.messages.GdbMiMessageConverterUtils.passThroughIfNotQQ")
  public String function;

  /**
   * The arguments to the function.
   */
  @GdbMiField(name = "args", valueType = GdbMiValue.Type.List)
  public Map<String, String> arguments;

  /**
   * The relative path to the file being executed.
   */
  @GdbMiField(name = "file", valueType = GdbMiValue.Type.String)
  public String fileRelative;

  /**
   * The absolute path to the file being executed.
   */
  @GdbMiField(name = "fullname", valueType = GdbMiValue.Type.String)
  public String fileAbsolute;

  /**
   * The line number being executed.
   */
  @GdbMiField(name = "line", valueType = GdbMiValue.Type.String)
  public Integer line;

  /**
   * The module where the function is defined.
   */
  @GdbMiField(name = "from", valueType = GdbMiValue.Type.String)
  public String module;
}
