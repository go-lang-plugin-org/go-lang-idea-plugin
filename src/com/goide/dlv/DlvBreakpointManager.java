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

package com.goide.dlv;

import com.goide.dlv.rdp.DlvRequest;
import com.goide.dlv.rdp.Location;
import com.goide.dlv.rdp.LocationImpl;
import com.goide.dlv.rdp.SetBreakpointResult;
import com.intellij.util.Function;
import com.intellij.util.SmartList;
import com.intellij.util.Url;
import com.intellij.util.Urls;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.Breakpoint;
import org.jetbrains.debugger.BreakpointManagerBase;
import org.jetbrains.debugger.BreakpointTarget;

import java.util.Collections;
import java.util.List;

public class DlvBreakpointManager extends BreakpointManagerBase<DlvBreakpoint> {
  private final DlvVm vm;

  public DlvBreakpointManager(@NotNull DlvVm vm) {
    this.vm = vm;
  }

  @NotNull
  public List<Breakpoint> findRelatedBreakpoints(@Nullable List<String> breakpointActors) {
    if (ContainerUtil.isEmpty(breakpointActors)) {
      return Collections.emptyList();
    }

    SmartList<Breakpoint> result = new SmartList<Breakpoint>();
    for (DlvBreakpoint breakpoint : breakpoints) {
      String actor = breakpoint.actor;
      if (actor != null && breakpointActors.contains(actor)) {
        result.add(breakpoint);
      }
    }
    return result;
  }

  @Override
  protected DlvBreakpoint createBreakpoint(@NotNull BreakpointTarget target, int line, int column, @Nullable String condition, int ignoreCount, boolean enabled) {
    return new DlvBreakpoint(target, line, column, condition, enabled);
  }

  @Override
  protected Promise<Breakpoint> doSetBreakpoint(@NotNull BreakpointTarget target, @NotNull final DlvBreakpoint breakpoint) {
    final String scriptUrl = ((BreakpointTarget.ScriptName)target).getName();
    return vm.commandProcessor.send(DlvRequest.setBreakpoint(vm.threadActor, scriptUrl, breakpoint.getLine(), breakpoint.getColumn())).then(
      new Function<SetBreakpointResult, Breakpoint>() {
        @Override
        public Breakpoint fun(SetBreakpointResult result) {
          Location location;
          Location rawLocation = result.actualLocation();
          // if location and actualLocation are the same, then the actualLocation property can be omitted
          if (rawLocation == null) {
            Url url = Urls.parseEncoded(scriptUrl);
            assert url != null;
            location = new LocationImpl(scriptUrl, breakpoint.getLine(), breakpoint.getColumn());
          }
          else {
            Url url = Urls.parseEncoded(rawLocation.url());
            assert url != null;
            location = new LocationImpl(scriptUrl, rawLocation.line() - 1, rawLocation.column());
          }
          breakpoint.setRemoteData(result.actor(), location);
          notifyBreakpointResolvedListener(breakpoint);
          return breakpoint;
        }
      });
  }

  @NotNull
  @Override
  protected Promise<Void> doClearBreakpoint(@NotNull DlvBreakpoint breakpoint) {
    String actor = breakpoint.actor;
    assert actor != null;
    return vm.commandProcessor.send(DlvRequest.deleteBreakpoint(actor));
  }

  @NotNull
  @Override
  public MUTE_MODE getMuteMode() {
    return MUTE_MODE.NONE;
  }
}