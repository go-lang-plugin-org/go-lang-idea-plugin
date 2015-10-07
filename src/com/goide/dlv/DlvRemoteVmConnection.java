/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.debugger.Vm;
import org.jetbrains.debugger.connection.RemoteVmConnection;
import org.jetbrains.io.NettyUtil;

import java.net.InetSocketAddress;

public class DlvRemoteVmConnection extends RemoteVmConnection {
  @NotNull
  @Override
  public Bootstrap createBootstrap(@NotNull InetSocketAddress address, @NotNull final AsyncPromise<Vm> vmResult) {
    return createBootstrap().handler(new ChannelInitializer() {
      @Override
      protected void initChannel(@NotNull Channel channel) throws Exception {
        vmResult.setResult(new DlvVm(getDebugEventListener(), channel));
      }
    });
  }

  @NotNull
  protected Bootstrap createBootstrap() {
    return NettyUtil.oioClientBootstrap();
  }

  @NotNull
  @Override
  protected String connectedAddressToPresentation(@NotNull InetSocketAddress address, @NotNull Vm vm) {
    return address.toString();
  }
}