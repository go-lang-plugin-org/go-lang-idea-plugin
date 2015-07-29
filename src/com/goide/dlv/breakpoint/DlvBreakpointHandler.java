/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

package com.goide.dlv.breakpoint;

import com.goide.dlv.DlvDebugProcess;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import org.jetbrains.annotations.NotNull;

public class DlvBreakpointHandler extends XBreakpointHandler<XLineBreakpoint<DlvBreakpointProperties>> {
  private final DlvDebugProcess myDebugProcess;

  public DlvBreakpointHandler(DlvDebugProcess debugProcess) {
    super(DlvBreakpointType.class);
    myDebugProcess = debugProcess;
  }

  @Override
  public void registerBreakpoint(@NotNull XLineBreakpoint<DlvBreakpointProperties> breakpoint) {
    myDebugProcess.addBreakpoint(breakpoint);
  }

  @Override
  public void unregisterBreakpoint(@NotNull XLineBreakpoint<DlvBreakpointProperties> breakpoint, boolean temporary) {
    myDebugProcess.removeBreakpoint(breakpoint);
  }
}
