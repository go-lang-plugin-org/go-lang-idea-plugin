package com.goide.debugger.ideagdb.debug;

import com.goide.debugger.gdb.Gdb;
import com.goide.debugger.gdb.messages.GdbStoppedEvent;
import com.goide.debugger.gdb.messages.GdbThread;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GdbSuspendContext extends XSuspendContext {
  private GdbExecutionStack myStack;
  @NotNull private final GdbExecutionStack[] myStacks;

  /**
   * @param gdb       Handle to the GDB instance.
   * @param stopEvent The stop event that caused the suspension.
   * @param threads   Thread information, if available.
   */
  public GdbSuspendContext(Gdb gdb, @NotNull GdbStoppedEvent stopEvent, @Nullable List<GdbThread> threads) {
    // Add all the threads to our list of stacks
    List<GdbExecutionStack> stacks = new ArrayList<GdbExecutionStack>();
    if (threads != null) {
      // Sort the list of threads by ID
      Collections.sort(threads, new Comparator<GdbThread>() {
        @Override
        public int compare(@NotNull GdbThread o1, @NotNull GdbThread o2) {
          return o1.id.compareTo(o2.id);
        }
      });

      for (GdbThread thread : threads) {
        GdbExecutionStack stack = new GdbExecutionStack(gdb, thread);
        stacks.add(stack);
        if (thread.id.equals(stopEvent.threadId)) {
          myStack = stack;
        }
      }
    }

    if (myStack == null) {
      // No thread object is available so we have to construct our own
      GdbThread thread = new GdbThread();
      thread.id = stopEvent.threadId;
      thread.frame = stopEvent.frame;
      myStack = new GdbExecutionStack(gdb, thread);
      stacks.add(0, myStack);
    }

    myStacks = stacks.toArray(new GdbExecutionStack[stacks.size()]);
  }

  @Nullable
  @Override
  public XExecutionStack getActiveExecutionStack() {
    return myStack;
  }

  @NotNull
  @Override
  public XExecutionStack[] getExecutionStacks() {
    return myStacks;
  }
}
