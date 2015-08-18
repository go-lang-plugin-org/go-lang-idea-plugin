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
 * Information about the execution threads in the target. This is returned from a -thread-info
 * request.
 */
@SuppressWarnings("unused")
@GdbMiDoneEvent(command = "-thread-info")
public class GdbThreadInfo extends GdbDoneEvent {
  /**
   * The threads.
   */
  @GdbMiField(name = "threads", valueType = GdbMiValue.Type.List)
  public List<GdbThread> threads;

  /**
   * The GDB ID of the current thread.
   */
  @GdbMiField(name = "current-thread-id", valueType = GdbMiValue.Type.String)
  public Integer currentThreadId;
}
