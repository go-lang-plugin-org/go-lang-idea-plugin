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

package com.goide.debugger.gdb;

import com.goide.debugger.gdb.gdbmi.GdbMiResultRecord;
import com.goide.debugger.gdb.gdbmi.GdbMiStreamRecord;
import com.goide.debugger.gdb.messages.GdbEvent;

/**
 * Interface that users of the Gdb class must implement to receive events.
 */
public interface GdbListener {
  /**
   * Called when a GDB error occurs.
   *
   * @param ex The exception
   */
  void onGdbError(Throwable ex);

  /**
   * Called when GDB has started.
   */
  void onGdbStarted();

  /**
   * Called whenever a command is sent to GDB.
   *
   * @param command The command that was sent.
   * @param token   The token the command was sent with.
   */
  void onGdbCommandSent(String command, long token);

  /**
   * Called when an event is received from GDB.
   *
   * @param event The event.
   */
  void onGdbEventReceived(GdbEvent event);

  /**
   * Called when a stream record is received.
   * This should only be used for logging or advanced behaviour. Prefer to use
   * onGdbEventReceived() instead.
   *
   * @param record The record.
   */
  void onStreamRecordReceived(GdbMiStreamRecord record);

  /**
   * Called when a result record is received.
   * This should only be used for logging or advanced behaviour. Prefer to use
   * onGdbEventReceived() instead.
   *
   * @param record The record.
   */
  void onResultRecordReceived(GdbMiResultRecord record);
}
