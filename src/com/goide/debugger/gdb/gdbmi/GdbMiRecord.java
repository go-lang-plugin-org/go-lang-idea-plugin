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

package com.goide.debugger.gdb.gdbmi;

/**
 * Class representing an individual record within a GDB/MI message.
 */
public abstract class GdbMiRecord {
  /**
   * Possible record types.
   */
  public enum Type {
    Immediate,  // Result: Immediate result for the last request
    Exec,       // Result: Asynchronous state change on the target
    Status,     // Result: Progress information about a long-running operation
    Notify,     // Result: Supplementary information
    Console,    // Stream: Textual response from a CLI command to be printed to the console
    Target,     // Stream: Output from the running application
    Log         // Stream: Log output from GDB
  }

  /**
   * The type of the record.
   */
  public Type type;

  /**
   * The user token from the record. May be null if none was specified.
   */
  public Long userToken;
}
