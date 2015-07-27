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

import com.goide.dlv.protocol.Api;
import com.goide.dlv.protocol.CommandResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.jsonProtocol.Request;
import org.jetbrains.rpc.CommandProcessor;
import org.jetbrains.rpc.MessageManager;
import org.jetbrains.rpc.MessageWriter;
import org.jetbrains.rpc.RequestCallback;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DlvCommandProcessor extends CommandProcessor<JsonReaderEx, CommandResponse, CommandResponse> {
  @NotNull private final MessageWriter myWriter;

  public DlvCommandProcessor(@NotNull MessageWriter writer) {
    myWriter = writer;
  }

  @Override
  public boolean write(@NotNull Request message) throws IOException {
    return myWriter.fun(message);
  }

  @Nullable
  @Override
  public CommandResponse readIfHasSequence(@NotNull JsonReaderEx message) {
    return new CommandResponse.CommandResponseImpl(message, null);
  }

  @Override
  public int getSequence(@NotNull CommandResponse response) {
    return response.id();
  }

  @Override
  public void acceptNonSequence(JsonReaderEx message) {
  }

  public MessageManager<Request, JsonReaderEx, CommandResponse, CommandResponse> getMessageManager() {
    return messageManager;
  }

  @Override
  public void call(@NotNull CommandResponse response, @NotNull RequestCallback<CommandResponse> callback) {
    if (response.result() != null) {
      callback.onSuccess(response, this);
    }
    else {
      String message;
      if (response.error() == null) {
        message = "Internal messaging error";
      }
      else {
        CommandResponse.ErrorInfo errorInfo = response.error();
        if (ContainerUtil.isEmpty(errorInfo.data())) {
          message = errorInfo.message();
        }
        else {
          List<String> messageList = new ArrayList<String>();
          messageList.add(errorInfo.message());
          messageList.addAll(errorInfo.data());
          message = messageList.toString();
        }
      }
      callback.onError(Promise.createError(message));
    }
  }

  @NotNull
  @Override
  public <RESULT> RESULT readResult(@NotNull String method, @NotNull CommandResponse successResponse) {
    JsonReader reader = successResponse.result().asGson();
    final Gson gson = new GsonBuilder().create();
    if (method.equals("RPCServer.StacktraceGoroutine")) {
      Type listType = new TypeToken<ArrayList<Api.Location>>() {
      }.getType();
      return gson.fromJson(reader, listType);
    }
    Object o = gson.fromJson(reader, getT(method));
    //noinspection unchecked
    return (RESULT)o;
  }

  @NotNull
  private static Type getT(@NotNull String method) {
    if (method.equals("RPCServer.CreateBreakpoint")) return Api.Breakpoint.class;
    if (method.equals("RPCServer.ClearBreakpoint")) return Api.Breakpoint.class;
    if (method.equals("RPCServer.Command")) return Api.DebuggerState.class;
    return Object.class;
  }
}