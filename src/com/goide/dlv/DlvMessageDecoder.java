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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.io.MessageDecoder;
import org.jetbrains.rpc.CommandProcessor;

// https://wiki.mozilla.org/Remote_Debugging_Protocol_Stream_Transport
public abstract class DlvMessageDecoder extends MessageDecoder {
  private State state = State.LENGTH;

  private enum State {
    LENGTH, CONTENT
  }

  @Override
  protected void messageReceived(@NotNull ChannelHandlerContext context, @NotNull ByteBuf input) throws Exception {
    while (true) {
      switch (state) {
        case LENGTH: {
          if (!readUntil(':', input, builder)) {
            return;
          }

          state = State.CONTENT;
          contentLength = parseContentLength();
          builder.setLength(0);
        }

        case CONTENT: {
          CharSequence result = readChars(input);
          if (result == null) {
            return;
          }

          if (CommandProcessor.LOG.isDebugEnabled()) {
            CommandProcessor.LOG.debug("IN: " + result);
          }

          contentLength = 0;
          state = State.LENGTH;

          processMessage(result, context);
        }
      }
    }
  }

  protected abstract void processMessage(@NotNull CharSequence response, @NotNull ChannelHandlerContext context);
}