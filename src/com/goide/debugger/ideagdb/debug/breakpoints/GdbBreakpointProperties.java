package com.goide.debugger.ideagdb.debug.breakpoints;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import org.jetbrains.annotations.Nullable;

public class GdbBreakpointProperties extends XBreakpointProperties {
  private static final Logger LOG = Logger.getInstance(GdbBreakpointProperties.class);

  @Nullable
  @Override
  public Object getState() {
    LOG.warn("getState: stub");
    return null;
  }

  @Override
  public void loadState(Object state) {
    LOG.warn("loadState: stub");
  }
}
