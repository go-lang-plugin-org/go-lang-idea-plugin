package com.goide.debugger.ideagdb.debug.breakpoints;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import org.jetbrains.annotations.Nullable;

public class GdbBreakpointProperties extends XBreakpointProperties {
  private static final Logger m_log =
    Logger.getInstance("#com.goide.debugger.ideagdb.debug.breakpoints.GdbBreakpointProperties");

  @Nullable
  @Override
  public Object getState() {
    m_log.warn("getState: stub");
    return null;
  }

  @Override
  public void loadState(Object state) {
    m_log.warn("loadState: stub");
  }
}
