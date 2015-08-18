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

import com.goide.debugger.gdb.gdbmi.GdbMiRecord;
import com.goide.debugger.gdb.gdbmi.GdbMiValue;
import com.goide.debugger.gdb.messages.annotations.GdbMiEvent;
import com.goide.debugger.gdb.messages.annotations.GdbMiField;

/**
 * Event fired when an error occurs with a request.
 */
@SuppressWarnings("unused")
@GdbMiEvent(recordType = GdbMiRecord.Type.Immediate, className = "error")
public class GdbErrorEvent extends GdbEvent {
  /**
   * The error message.
   */
  @SuppressWarnings("unused")
  @GdbMiField(name = "msg", valueType = GdbMiValue.Type.String)
  public String message;
}
