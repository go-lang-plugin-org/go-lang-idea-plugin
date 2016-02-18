/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.dlv.protocol;

import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jsonProtocol.OutMessage;
import org.jetbrains.jsonProtocol.Request;

import java.io.IOException;
import java.util.List;

/**
 * Please add your requests as a subclasses, otherwise reflection won't work.
 *
 * @param <T> type of callback
 * @see com.goide.dlv.DlvCommandProcessor#getResultType(String)
 */
public abstract class DlvRequest<T> extends OutMessage implements Request<T> {
  private static final String PARAMS = "params";
  private static final String ID = "id";
  private boolean argumentsObjectStarted;

  private DlvRequest() {
    try {
      getWriter().name("method").value(getMethodName());
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  @Override
  public String getMethodName() {
    return "RPCServer." + getClass().getSimpleName();
  }

  @Override
  public final void beginArguments() {
    if (!argumentsObjectStarted) {
      argumentsObjectStarted = true;
      if (needObject()) {
        try {
          getWriter().name(PARAMS);
          getWriter().beginArray();
          getWriter().beginObject();
        }
        catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  protected boolean needObject() {
    return true;
  }

  @Override
  public final void finalize(int id) {
    try {
      if (argumentsObjectStarted) {
        if (needObject()) {
          getWriter().endObject();
          getWriter().endArray();
        }
      }
      getWriter().name(ID).value(id);
      getWriter().endObject();
      getWriter().close();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public final static class ClearBreakpoint extends DlvRequest<DlvApi.Breakpoint> {
    public ClearBreakpoint(int id) {
      writeSingletonIntArray(PARAMS, id);
    }

    @Override
    protected boolean needObject() {
      return false;
    }
  }

  public final static class CreateBreakpoint extends DlvRequest<DlvApi.Breakpoint> {
    public CreateBreakpoint(String path, int line) {
      writeString("file", path);
      writeLong("line", line);
    }
  }

  public final static class StacktraceGoroutine extends DlvRequest<List<DlvApi.Location>> {
    public StacktraceGoroutine() {
      writeLong("Id", -1);
      writeLong("Depth", 100);
    }
  }

  private abstract static class Locals<T> extends DlvRequest<T> {
    Locals(int frameId) {
      writeLong("GoroutineID", -1);
      writeLong("Frame", frameId);
    }
  }

  public final static class ListLocalVars extends Locals<List<DlvApi.Variable>> {
    public ListLocalVars(int frameId) {
      super(frameId);
    }
  }

  public final static class ListFunctionArgs extends Locals<List<DlvApi.Variable>> {
    public ListFunctionArgs(int frameId) {
      super(frameId);
    }
  }

  public final static class Command extends DlvRequest<DlvApi.DebuggerState> {
    public Command(@Nullable String command) {
      writeString("Name", command);
    }
  }

  public final static class Detach extends DlvRequest<Integer> {
    public Detach(boolean kill) {
      try {
        beginArguments();
        getWriter().name(PARAMS).beginArray().value(kill).endArray();
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    protected boolean needObject() {
      return false;
    }
  }

  public final static class EvalSymbol extends DlvRequest<DlvApi.Variable> {
    public EvalSymbol(@NotNull String symbol, int frameId) {
      try {
        getWriter().name(PARAMS).beginArray();
        writeScope(frameId, getWriter())
          .name("Symbol").value(symbol)
          .endObject().endArray();
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    protected boolean needObject() {
      return false;
    }
  }

  @NotNull
  private static JsonWriter writeScope(int frameId, @NotNull JsonWriter writer) throws IOException {
    // todo: ask vladimir how to simplify this
    return writer.beginObject()
      .name("Scope").beginObject()
      .name("GoroutineID").value(-1)
      .name("Frame").value(frameId).endObject();
  }

  public final static class SetSymbol extends DlvRequest<Object> {
    public SetSymbol(@NotNull String symbol, @NotNull String value, int frameId) {
      try {
        getWriter().name(PARAMS).beginArray();
        writeScope(frameId, getWriter())
          .name("Symbol").value(symbol)
          .name("Value").value(value)
          .endObject().endArray();
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    protected boolean needObject() {
      return false;
    }
  }
}
