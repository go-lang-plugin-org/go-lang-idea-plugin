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
 * Class representing a stream record from a GDB/MI stream.
 */
public class GdbMiStreamRecord extends GdbMiRecord {
  /**
   * The contents of the record.
   */
  public String message;

  /**
   * Constructor.
   *
   * @param type      The record type.
   * @param userToken The user token. May be null.
   */
  public GdbMiStreamRecord(Type type, Long userToken) {
    this.type = type;
    this.userToken = userToken;
  }
}
