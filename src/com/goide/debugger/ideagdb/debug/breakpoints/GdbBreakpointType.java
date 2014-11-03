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

package com.goide.debugger.ideagdb.debug.breakpoints;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GdbBreakpointType extends XLineBreakpointType<GdbBreakpointProperties> {
  private static final Logger LOG = Logger.getInstance(GdbBreakpointType.class);

  public GdbBreakpointType() {
    super("gdb", "GDB Breakpoints");
  }

  @Nullable
  @Override
  public GdbBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
    LOG.warn("createBreakpointProperties: stub");
    return null;
  }

  @Override
  public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
    // TODO: We can't just always return true because otherwise it prevents Java breakpoints
    // being set. It seems like there should be a better way to do this though..
    String extension = file.getExtension();
    if (extension != null &&
        (extension.equals("c") ||
         extension.equals("cpp") ||
         extension.equals("cxx") ||
         extension.equals("cc") ||
         extension.equals("h") ||
         extension.equals("hpp") ||
         extension.equals("hh") ||
         extension.equals("go") ||
         extension.equals("hxx"))) {
      return true;
    }
    return false;
  }
}
