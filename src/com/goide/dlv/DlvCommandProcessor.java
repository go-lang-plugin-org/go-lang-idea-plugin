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

import com.goide.dlv.protocol.DlvApi;
import com.goide.dlv.protocol.DlvResponse;
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

import static com.goide.dlv.protocol.DlvRequest.*;

public class DlvCommandProcessor extends CommandProcessor<JsonReaderEx, DlvResponse, DlvResponse> {
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
  public DlvResponse readIfHasSequence(@NotNull JsonReaderEx message) {
    return new DlvResponse.CommandResponseImpl(message, null);
  }

  @Override
  public int getSequence(@NotNull DlvResponse response) {
    return response.id();
  }

  @Override
  public void acceptNonSequence(JsonReaderEx message) {
  }

  public MessageManager<Request, JsonReaderEx, DlvResponse, DlvResponse> getMessageManager() {
    return messageManager;
  }

  @Override
  public void call(@NotNull DlvResponse response, @NotNull RequestCallback<DlvResponse> callback) {
    if (response.result() != null) {
      callback.onSuccess(response, this);
    }
    else {
      String message;
      if (response.error() == null) {
        message = "Internal messaging error";
      }
      else {
        DlvResponse.ErrorInfo errorInfo = response.error();
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
  public <RESULT> RESULT readResult(@NotNull String method, @NotNull DlvResponse successResponse) {
    JsonReaderEx result = successResponse.result();
    assert result != null : "success result should be not null";
    JsonReader reader = result.asGson();
    Object o = new GsonBuilder().create().fromJson(reader, getResultType(method));
    //noinspection unchecked
    return (RESULT)o;
  }

  @NotNull
  private static Type getResultType(@NotNull String method) {
    if (method.equals(CreateBreakpoint.class.getSimpleName())) return DlvApi.Breakpoint.class;
    if (method.equals(ClearBreakpoint.class.getSimpleName())) return DlvApi.Breakpoint.class;
    if (method.equals(Command.class.getSimpleName())) return DlvApi.DebuggerState.class;
    if (method.equals(StacktraceGoroutine.class.getSimpleName())) return new TypeToken<ArrayList<DlvApi.Location>>() {}.getType();
    if (method.equals(ListLocalVars.class.getSimpleName()) || method.equals(ListFunctionArgs.class.getSimpleName())) return new TypeToken<ArrayList<DlvApi.Variable>>() {}.getType();
    if (method.equals(EvalSymbol.class.getSimpleName())) return DlvApi.Variable.class;
    return Object.class;
  }
}