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
import com.intellij.openapi.vfs.CharsetToolkit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.DebugEventListener;
import org.jetbrains.debugger.ExceptionCatchMode;
import org.jetbrains.debugger.StandaloneVmHelper;
import org.jetbrains.debugger.VmBase;
import org.jetbrains.io.ChannelBufferToString;
import org.jetbrains.io.SimpleChannelInboundHandlerAdapter;
import org.jetbrains.jsonProtocol.Request;

public class DlvVm extends VmBase implements StandaloneVmHelper.VmEx {
  final DlvCommandProcessor commandProcessor;
  private final StandaloneVmHelper vmHelper;
  private final DlvBreakpointManager breakpointManager = new DlvBreakpointManager(this);
  private final DlvScriptManager scriptManager = new DlvScriptManager();
  private final DlvSuspendContextManager suspendContextManager;
  String tabActor;
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
          System.out.println(((ByteBuf)message).toString(CharsetToolkit.UTF8_CHARSET));
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
    return tabActor == null ? null : DlvRequest.detach(tabActor);
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
  public DlvScriptManager getScriptManager() {
    return scriptManager;
  }

  @NotNull
  @Override
  public DlvBreakpointManager getBreakpointManager() {
    return breakpointManager;
  }

  @NotNull
  @Override
  public DlvSuspendContextManager getSuspendContextManager() {
    return suspendContextManager;
  }
}