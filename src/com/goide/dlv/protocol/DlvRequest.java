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

package com.goide.dlv.protocol;

import com.intellij.util.containers.ContainerUtil;
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
  protected boolean argumentsObjectStarted;

  private DlvRequest() {
    try {
      writer.name("method").value(getMethodName());
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getMethodName() {
    return "RPCServer." + getClass().getSimpleName();
  }

  @Override
  protected final void beginArguments() throws IOException {
    if (!argumentsObjectStarted) {
      argumentsObjectStarted = true;
      if (needObject()) {
        writer.name(argumentsKeyName());
        writer.beginArray();
        writer.beginObject();
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
          writer.endObject();
          writer.endArray();
        }
      }
      writer.name(getIdKeyName()).value(id);
      writer.endObject();
      writer.close();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected final String getIdKeyName() {
    return "id";
  }

  protected final String argumentsKeyName() {
    return "params";
  }

  public final static class ClearBreakpoint extends DlvRequest<DlvApi.Breakpoint> {
    public ClearBreakpoint(int id) {
      writeSingletonIntArray(argumentsKeyName(), id);
    }

    @Override
    protected boolean needObject() {
      return false;
    }
  }

  public final static class CreateBreakpoint extends DlvRequest<DlvApi.Breakpoint> {
    public CreateBreakpoint(String path, int line) {
      writeString("file", path);
      writeInt("line", line);
    }
  }

  public final static class StacktraceGoroutine extends DlvRequest<List<DlvApi.Location>> {
    public StacktraceGoroutine() {
      writeInt("Id", -1);
      writeInt("Depth", 100);
    }
  }

  private abstract static class Locals<T> extends DlvRequest<T> {
    private Locals() {
      List<String> objects = ContainerUtil.newArrayListWithCapacity(1);
      objects.add(null);
      writeStringList(argumentsKeyName(), objects);
    }

    @Override
    protected boolean needObject() {
      return false;
    }
  }

  public final static class ListLocalVars extends Locals<List<DlvApi.Variable>> {
  }

  public final static class ListFunctionArgs extends Locals<List<DlvApi.Variable>> {
  }

  public final static class Command extends DlvRequest<DlvApi.DebuggerState> {
    public Command(@Nullable String command) {
      writeString("Name", command);
    }
  }

  public final static class EvalSymbol extends DlvRequest<DlvApi.Variable> {
    public EvalSymbol(String symbol) {
      writeStringList(argumentsKeyName(), ContainerUtil.newSmartList(symbol));
    }

    @Override
    protected boolean needObject() {
      return false;
    }
  }
}
