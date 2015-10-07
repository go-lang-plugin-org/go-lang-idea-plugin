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

package com.goide.debugger.gdb.gdbmi;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Class representing a variable value read from a GDB/MI stream.
 */
public class GdbMiValue {
  /**
   * Possible types the value can take.
   */
  public enum Type {
    String,
    Tuple,
    List
  }

  /**
   * Type of the value.
   */
  public Type type;

  /**
   * String. Will be null if type is not String.
   */
  public String string;

  /**
   * Tuple. Will be null if type is not Tuple.
   */
  public List<GdbMiResult> tuple;

  /**
   * List. Will be null if type is not List.
   */
  public GdbMiList list;

  /**
   * Default constructor.
   */
  public GdbMiValue() {
  }

  /**
   * Constructor; sets the type only.
   */
  public GdbMiValue(Type type) {
    this.type = type;
  }

  /**
   * Converts the value to a string.
   *
   * @return A string containing the value.
   */
  @NotNull
  public String toString() {
    StringBuilder sb = new StringBuilder();
    switch (type) {
      case String:
        sb.append("\"");
        sb.append(string);
        sb.append("\"");
        break;

      case Tuple: {
        sb.append("{");
        for (int i = 0; i != tuple.size(); ++i) {
          sb.append(tuple.get(i));
          if (i < tuple.size() - 1) {
            sb.append(", ");
          }
        }
        sb.append("}");
      }
      break;

      case List:
        sb.append(list);
        break;
    }
    return sb.toString();
  }
}
