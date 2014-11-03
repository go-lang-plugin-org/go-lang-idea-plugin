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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a result record from a GDB/MI stream.
 */
public class GdbMiResultRecord extends GdbMiRecord {
  /**
   * The result/async class.
   */
  @Nullable public String className;

  /**
   * The results.
   */
  @NotNull public List<GdbMiResult> results = new ArrayList<GdbMiResult>();

  /**
   * Constructor.
   *
   * @param type      The record type.
   * @param userToken The user token. May be null.
   */
  public GdbMiResultRecord(Type type, Long userToken) {
    this.type = type;
    this.userToken = userToken;
  }

  /**
   * Converts the record to a string.
   *
   * @return A string containing the class name and any results.
   */
  @NotNull
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(className);
    if (!results.isEmpty()) {
      sb.append(": [");
      for (int i = 0; i != results.size(); ++i) {
        sb.append(results.get(i));
        if (i < results.size() - 1) {
          sb.append(", ");
        }
      }
      sb.append("]");
    }
    return sb.toString();
  }
}
