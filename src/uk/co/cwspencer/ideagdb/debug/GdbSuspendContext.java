package uk.co.cwspencer.ideagdb.debug;

import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.jetbrains.annotations.Nullable;
import uk.co.cwspencer.gdb.Gdb;
import uk.co.cwspencer.gdb.messages.GdbStoppedEvent;
import uk.co.cwspencer.gdb.messages.GdbThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GdbSuspendContext extends XSuspendContext {
    // The active stack
    private GdbExecutionStack m_stack;

    // All stacks
    private GdbExecutionStack[] m_stacks;

    /**
     * Constructor.
     *
     * @param gdb       Handle to the GDB instance.
     * @param stopEvent The stop event that caused the suspension.
     * @param threads   Thread information, if available.
     */
    public GdbSuspendContext(Gdb gdb, GdbStoppedEvent stopEvent, List<GdbThread> threads) {
        // Add all the threads to our list of stacks
        List<GdbExecutionStack> stacks = new ArrayList<GdbExecutionStack>();
        if (threads != null) {
            // Sort the list of threads by ID
            Collections.sort(threads, new Comparator<GdbThread>() {
                @Override
                public int compare(GdbThread o1, GdbThread o2) {
                    return o1.id.compareTo(o2.id);
                }
            });

            for (GdbThread thread : threads) {
                GdbExecutionStack stack = new GdbExecutionStack(gdb, thread);
                stacks.add(stack);
                if (thread.id.equals(stopEvent.threadId)) {
                    m_stack = stack;
                }
            }
        }

        if (m_stack == null) {
            // No thread object is available so we have to construct our own
            GdbThread thread = new GdbThread();
            thread.id = stopEvent.threadId;
            thread.frame = stopEvent.frame;
            m_stack = new GdbExecutionStack(gdb, thread);
            stacks.add(0, m_stack);
        }

        m_stacks = new GdbExecutionStack[stacks.size()];
        m_stacks = stacks.toArray(m_stacks);
    }

    /**
     * Gets the active stack.
     *
     * @return The active stack.
     */
    @Nullable
    @Override
    public XExecutionStack getActiveExecutionStack() {
        return m_stack;
    }

    /**
     * Gets all execution stacks.
     *
     * @return The execution stacks.
     */
    @Override
    public XExecutionStack[] getExecutionStacks() {
        return m_stacks;
    }
}
