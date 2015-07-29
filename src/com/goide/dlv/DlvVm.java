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

import com.intellij.openapi.vfs.CharsetToolkit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.*;
import org.jetbrains.debugger.values.FunctionValue;
import org.jetbrains.io.ChannelBufferToString;
import org.jetbrains.io.SimpleChannelInboundHandlerAdapter;
import org.jetbrains.jsonProtocol.Request;

public class DlvVm extends VmBase implements StandaloneVmHelper.VmEx {
  @NotNull private final DlvCommandProcessor commandProcessor;
  @NotNull private final StandaloneVmHelper vmHelper;
  @NotNull private final BreakpointManagerBase<DlvBreakpoint> breakpointManager = new DummyBreakpointManager();
  @NotNull private final ScriptManagerBaseEx<ScriptBase> scriptManager = new DummyScriptManager();
  @NotNull private final DlvSuspendContextManager suspendContextManager;
  String threadActor;

  public DlvVm(@NotNull DebugEventListener tabListener, @NotNull Channel channel) {
    super(tabListener);

    vmHelper = new StandaloneVmHelper(this) {
      @Override
      public boolean fun(@NotNull Request message) {
        ByteBuf content = message.getBuffer();
        System.out.println("OUT: " + content.toString(CharsetToolkit.UTF8_CHARSET));
        return write(content);
      }
    };
    vmHelper.setChannel(channel);
    commandProcessor = new DlvCommandProcessor(vmHelper);
    suspendContextManager = new DlvSuspendContextManager(this);


    channel.pipeline().addLast(new JsonObjectDecoder(), new SimpleChannelInboundHandlerAdapter() {
      @Override
      protected void messageReceived(ChannelHandlerContext context, Object message) throws Exception {
        if (message instanceof ByteBuf) {
          System.out.println("IN: " + ((ByteBuf)message).toString(CharsetToolkit.UTF8_CHARSET));
          CharSequence string = ChannelBufferToString.readChars((ByteBuf)message);
          JsonReaderEx ex = new JsonReaderEx(string);
          getCommandProcessor().getMessageManager().processIncoming(ex);
        }
      }
    });
  }

  @Nullable
  @Override
  public Request createDisconnectRequest() {
    return null;
  }

  @NotNull
  @Override
  public AttachStateManager getAttachStateManager() {
    return vmHelper;
  }

  @Override
  @NotNull
  public final DlvCommandProcessor getCommandProcessor() {
    return commandProcessor;
  }

  @NotNull
  @Override
  public Promise<Void> setBreakOnException(@NotNull ExceptionCatchMode catchMode) {
    // todo we should pause thread and resume with specified pauseOnExceptions = true
    return Promise.resolve(null);
  }

  @NotNull
  @Override
  public ScriptManagerBase<ScriptBase> getScriptManager() {
    return scriptManager;
  }

  @NotNull
  @Override
  public BreakpointManagerBase<DlvBreakpoint> getBreakpointManager() {
    return breakpointManager;
  }

  @NotNull
  @Override
  public DlvSuspendContextManager getSuspendContextManager() {
    return suspendContextManager;
  }

  // stubs
  
  private static class DummyScriptManager extends ScriptManagerBaseEx<ScriptBase> {
    @Override
    public boolean containsScript(@NotNull Script script) {
      return true;
    }

    @NotNull
    @Override
    public Promise setSourceOnRemote(@NotNull Script script, @NotNull CharSequence newSource, boolean preview) {
      return Promise.DONE;
    }

    @NotNull
    @Override
    public Promise<Script> getScript(@NotNull FunctionValue function) {
      return Promise.resolve(null);
    }

    @Nullable
    @Override
    public Script getScript(@NotNull CallFrame frame) {
      return null;
    }

    @NotNull
    @Override
    protected Promise<String> loadScriptSource(@NotNull ScriptBase script) {
      return Promise.resolve("");
    }
  }

  private static class DummyBreakpointManager extends BreakpointManagerBase<DlvBreakpoint> {
    @Nullable
    @Override
    protected DlvBreakpoint createBreakpoint(@NotNull BreakpointTarget target,
                                             int line,
                                             int column,
                                             @Nullable String condition,
                                             int ignoreCount,
                                             boolean enabled) {
      return null;
    }

    @NotNull
    @Override
    protected Promise<Breakpoint> doSetBreakpoint(@NotNull BreakpointTarget target, @NotNull final DlvBreakpoint breakpoint) {
      return Promise.resolve(null);
    }

    @NotNull
    @Override
    protected Promise<Void> doClearBreakpoint(@NotNull DlvBreakpoint breakpoint) {
      return Promise.DONE;
    }

    @NotNull
    @Override
    public MUTE_MODE getMuteMode() {
      return MUTE_MODE.NONE;
    }
  }
}