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

package com.goide.dlv.protocol;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class DlvApi {
  private final static Logger LOG = Logger.getInstance(DlvApi.class);

  // DebuggerState represents the current context of the debugger.
  public static class DebuggerState {
    // Breakpoint is the current breakpoint at which the debugged process is
    // suspended, and may be empty if the process is not suspended.
    public Breakpoint breakPoint;
    // CurrentThread is the currently selected debugger thread.
    public Thread currentThread;
    // SelectedGoroutine is the currently selected goroutine
    public Goroutine currentGoroutine;
    // Information requested by the current breakpoint
    public BreakpointInfo breakPointInfo;
    // Exited indicates whether the debugged process has exited.
    public boolean exited;
    public int exitStatus;
    // Filled by RPCClient.Continue, indicates an error
    public String err;
  }

  // Breakpoint addresses a location at which process execution may be
  // suspended.
  public static class Breakpoint {
    // ID is a unique identifier for the breakpoint.
    public int id;
    // Addr is the address of the breakpoint.
    public int addr;
    // File is the source file for the breakpoint.
    public String file;
    // Line is a line in File for the breakpoint.
    public int line;
    // FunctionName is the name of the function at the current breakpoint, and
    // may not always be available.
    public String functionName;
    // tracepoint flag
    @SerializedName("continue")
    public boolean tracepoint;
    // number of stack frames to retrieve
    public int stacktrace;
    // retrieve goroutine information
    public boolean goroutine;
    // variables to evaluate
    public List<String> variables;
    // number of times a breakpoint has been reached in a certain goroutine
    public Object hitCount; // todo: check type map[string]uint64
    // number of times a breakpoint has been reached
    public long totalHitCount;
  }

  // Thread is a thread within the debugged process.
  public static class Thread {
    // ID is a unique identifier for the thread.
    public int id;
    // PC is the current program counter for the thread.
    public long pc;
    // File is the file for the program counter.
    public String file;
    // Line is the line number for the program counter.
    public int line;
    // Function is function information at the program counter. May be nil.
    public Function function;
  }

  public static class Location {
    public long pc;
    public String file;
    public int line;
    public Function function;
  }

  // Function represents thread-scoped function information.
  public static class Function {
    // Name is the function name.
    public String name;
    public int value;
    public byte type;
    public int goclass;
    // Args are the function arguments in a thread context.
    public List<Variable> args;
    // Locals are the thread local variables.
    public List<Variable> locals;
  }

  // Variable describes a variable.
  public static class Variable {
    // Name of the variable or struct member
    public String name;
    // Go type of the variable
    public String type;
    // Address of the variable or struct member
    public Object addr;
    // Type of the variable after resolving any typedefs
    public String realType;

    public int kind;

    //Strings have their length capped at proc.maxArrayValues, use Len for the real length of a string
    //Function variables will store the name of the function in this field
    public String value;

    // Number of elements in an array or a slice, number of keys for a map, number of struct members for a struct, length of strings
    public long len;
    // Cap value for slices
    public long cap;
    // Array and slice elements, member fields of structs, key/value pairs of maps, value of complex numbers
    // The Name field in this slice will always be the empty string except for structs (when it will be the field name) and for complex numbers (when it will be "real" and "imaginary")
    // For maps each map entry will have to items in this slice, even numbered items will represent map keys and odd numbered items will represent their values
    // This field's length is capped at proc.maxArrayValues for slices and arrays and 2*proc.maxArrayValues for maps, in the circumnstances where the cap takes effect len(Children) != Len
    // The other length cap applied to this field is related to maximum recursion depth, when the maximum recursion depth is reached this field is left empty, contrary to the previous one this cap also applies to structs (otherwise structs will always have all thier member fields returned)
    public Variable[] children;
    // Unreadable addresses will have this field set
    public String unreadable;

    @NotNull
    private Kind getKind() {
      try {
        return Kind.values()[kind];
      }
      catch (Exception e) {
        LOG.warn("Unknown kind '" + kind + "' of variable '" + name + "'");
        return Kind.Invalid;
      }
    }

    private enum Kind {
      Invalid,
      Bool,
      Int, Int8, Int16, Int32, Int64, Uint, Uint8, Uint16, Uint32, Uint64,
      Uintptr,
      Float32, Float64,
      Complex64, Complex128,
      Array, Chan, Func, Interface, Map, Ptr, Slice, String, Struct,
      UnsafePointer;

      private boolean isNumber() {
        return compareTo(Int) >= 0 && compareTo(Complex128) <= 0;
      }

    }
    
    public boolean isSlice() {
      return getKind() == Kind.Slice;
    }
    
    public boolean isArray() {
      return getKind() == Kind.Array;
    }

    public boolean isNumber() { return getKind().isNumber(); }

    public boolean isString() { return getKind() == Kind.String; }

    public boolean isBool() { return getKind() == Kind.Bool; }

    public boolean isStructure() {
      return getKind() == Kind.Struct;
    }

    public boolean isPtr() {
      return getKind() == Kind.Ptr;
    }
  }

  // Goroutine represents the information relevant to Delve from the runtime's
  // internal G structure.
  public static class Goroutine {
    // ID is a unique identifier for the goroutine.
    public int id;
    // PC is the current program counter for the goroutine.
    public long pc;
    // File is the file for the program counter.
    public String file;
    // Line is the line number for the program counter.
    public int line;
    // Current location of the goroutine
    public Location currentLoc;
    // Current location of the goroutine, excluding calls inside runtime
    public Location userCurrentLoc;
    // Location of the go instruction that started this goroutine
    public Location goStatementLoc;
  }

  // DebuggerCommand is a command which changes the debugger's execution state.
  public static class DebuggerCommand {
    // Name is the command to run.
    public String name;
    // ThreadID is used to specify which thread to use with the SwitchThread
    // command.
    public int threadID;
    // GoroutineID is used to specify which thread to use with the SwitchGoroutine
    // command.
    public int goroutineID; // `json:"goroutineID,omitempty"`
  }

  // Informations about the current breakpoint
  public static class BreakpointInfo {
    public List<Location> stacktrace;
    public Goroutine goroutine;
    public List<Variable> variables;
    public List<Variable> arguments;
  }

  public static class EvalScope {
    public int GoroutineID;
    public int Frame;
  }

  public static final String CONTINUE = "continue";
  public static final String STEP = "step";
  public static final String NEXT = "next";
  public static final String SWITCH_THREAD = "switchThread";
  public static final String HALT = "halt";
  public static final String SWITCH_GOROUTINE = "switchGoroutine";
}
