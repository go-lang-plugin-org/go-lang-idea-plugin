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

import com.goide.debugger.gdb.gdbmi.GdbMiRecord;
import com.goide.debugger.gdb.gdbmi.GdbMiValue;
import com.goide.debugger.gdb.messages.annotations.GdbMiEvent;
import com.goide.debugger.gdb.messages.annotations.GdbMiField;

import java.util.Map;

/**
 * Event fired when GDB connects to a remote target.
 */
@SuppressWarnings("unused")
@GdbMiEvent(recordType = GdbMiRecord.Type.Immediate, className = "connected")
public class GdbConnectedEvent extends GdbEvent {
  /**
   * The execution address.
   */
  @SuppressWarnings("unused")
  @GdbMiField(name = "addr", valueType = GdbMiValue.Type.String,
              valueProcessor = "com.goide.debugger.gdb.messages.GdbMiMessageConverterUtils.hexStringToLong")
  public Long address;

  /**
   * The name of the function.
   */
  @SuppressWarnings("unused")
  @GdbMiField(name = "func", valueType = GdbMiValue.Type.String, valueProcessor =
    "com.goide.debugger.gdb.messages.GdbMiMessageConverterUtils.passThroughIfNotQQ")
  public String function;

  /**
   * The arguments to the function.
   */
  @SuppressWarnings("unused")
  @GdbMiField(name = "args", valueType = GdbMiValue.Type.List)
  public Map<String, String> arguments;
}
