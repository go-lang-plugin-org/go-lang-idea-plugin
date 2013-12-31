package uk.co.cwspencer.ideagdb.debug.breakpoints;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GdbBreakpointType extends XLineBreakpointType<GdbBreakpointProperties> {
    private static final Logger m_log =
            Logger.getInstance("#uk.co.cwspencer.ideagdb.debug.breakpoints.GdbBreakpointType");

    public GdbBreakpointType() {
        super("gdb", "GDB Breakpoints");
    }

    @Nullable
    @Override
    public GdbBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        m_log.warn("createBreakpointProperties: stub");
        return null;
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        // TODO: We can't just always return true because otherwise it prevents Java breakpoints
        // being set. It seems like there should be a better way to do this though..
        String extension = file.getExtension();
        return extension != null && extension.equals("go");
    }
}
