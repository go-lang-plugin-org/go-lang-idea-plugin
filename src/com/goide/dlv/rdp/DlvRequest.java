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

package com.goide.dlv.rdp;

import com.google.common.base.CaseFormat;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.debugger.StepAction;
import org.jetbrains.jsonProtocol.OutMessage;
import org.jetbrains.jsonProtocol.Request;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class DlvRequest<RESULT> extends OutMessage implements Request<RESULT> {
  public final Method method;

  public DlvRequest(@NotNull String actor, @NotNull Method type) {
    this.method = type;

    writeString("to", actor);
    writeString("type", method.protocolName);
  }

  // ugly protocol term is "type", but here we use correct term "method"
  public enum Method {
    INITIAL, LIST_TABS,
    RESUME, ATTACH_TO_TAB("attach", "tabAttached"), ATTACH_TO_THREAD("attach", "paused"), DETACH, INTERRUPT,
    SET_BREAKPOINT, DELETE, FRAMES,
    CLIENT_EVALUATE(null, "resumed"),
    SOURCES, SOURCE,
    PROTOTYPE_AND_PROPERTIES, PROPERTY, PROTOTYPE, BINDINGS, THREAD_GRIP, THREAD_GRIPS, RELEASE_MANY,
    _NONE_
    ;

    public final String protocolName;
    public final String uglyProtocolResponseType;

    Method() {
      this(null, null);
    }

    //Method(@Nullable String uglyProtocolResponseType) {
    //  this(null, uglyProtocolResponseType);
    //}

    Method(@Nullable String protocolName, @Nullable String uglyProtocolResponseType) {
      this.protocolName = protocolName == null ? CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name()) : protocolName;
      if (uglyProtocolResponseType == null) {
        this.uglyProtocolResponseType = this.protocolName + (this.protocolName.charAt(this.protocolName.length() - 1) == 'e' ? "d" : "ed");
      }
      else {
        this.uglyProtocolResponseType = uglyProtocolResponseType;
      }
    }
  }

  @NotNull
  public static Request<PrototypeAndPropertiesResult> getPrototypeAndProperties(@NotNull String gripActor) {
    return new DlvRequest<PrototypeAndPropertiesResult>(gripActor, Method.PROTOTYPE_AND_PROPERTIES);
  }

  @NotNull
  public static Request<PropertyResult> getProperty(@NotNull String gripActor, @NotNull String name) {
    DlvRequest<PropertyResult> request = new DlvRequest<PropertyResult>(gripActor, Method.PROPERTY);
    request.writeString("name", name);
    return request;
  }

  @NotNull
  public static Request<PrototypeResult> getPrototype(@NotNull String gripActor) {
    return new DlvRequest<PrototypeResult>(gripActor, Method.PROTOTYPE);
  }

  @NotNull
  public static Request<Bindings> getBindings(@NotNull String environmentActor) {
    return new DlvRequest<Bindings>(environmentActor, Method.BINDINGS);
  }

  @SuppressWarnings("unused")
  @NotNull
  public static Request<Void> pauseToThreadGrip(@NotNull String actor) {
    return new DlvRequest<Void>(actor, Method.THREAD_GRIP);
  }

  @NotNull
  public static Request<Void> pauseLifetimeGripsToThreadLifetime(@NotNull String threadActor, @NotNull List<String> objectActors) {
    DlvRequest<Void> request = new DlvRequest<Void>(threadActor, Method.THREAD_GRIPS);
    request.writeStringList("actors", objectActors);
    return request;
  }

  @NotNull
  public static DlvRequest<Void> release(@NotNull String threadActor, @NotNull Collection<String> objectActors) {
    DlvRequest<Void> request = new DlvRequest<Void>(threadActor, Method.RELEASE_MANY);
    request.writeStringList("actors", objectActors);
    return request;
  }

  @NotNull
  public static Request<SourceResult> getScriptSource(@NotNull String actor) {
    return new DlvRequest<SourceResult>(actor, Method.SOURCE);
  }

  @NotNull
  public static Request<FramesResult> getFrames(@NotNull String thread) {
    return getFrames(thread, 0);
  }

  @NotNull
  public static Request<FramesResult> getFrames(@NotNull String thread, int start) {
    DlvRequest<FramesResult> request = new DlvRequest<FramesResult>(thread, Method.FRAMES);
    if (start != 0) {
      request.writeInt("start", start);
    }
    return request;
  }

  @NotNull
  public static Request<ListTabsResult> listTabs() {
    return new DlvRequest<ListTabsResult>("root", Method.LIST_TABS);
  }

  @NotNull
  public static Request<TabAttached> attachToTab(@NotNull String actor) {
    return new DlvRequest<TabAttached>(actor, Method.ATTACH_TO_TAB);
  }

  @NotNull
  public static Request<Void> detach(@NotNull String actor) {
    return new DlvRequest<Void>(actor, Method.DETACH);
  }

  @NotNull
  public static Request<ThreadInterrupted> attachToThread(@NotNull String actor) {
    return new DlvRequest<ThreadInterrupted>(actor, Method.ATTACH_TO_THREAD);
  }

  @NotNull
  public static Request<Void> resumeThread(@NotNull String thread, boolean pauseOnExceptions) {
    return resumeThread(thread, StepAction.CONTINUE, pauseOnExceptions);
  }

  // ugly and poor  rdp — step out doesn't work correctly, we must check — is step out really performed (we must step of from current function)
  // it is your responsibility to workaround ugly  rdp issue
  @NotNull
  public static Request<Void> resumeThread(@NotNull String thread, @NotNull StepAction stepAction, boolean pauseOnExceptions) {
    DlvRequest<Void> request = new DlvRequest<Void>(thread, Method.RESUME);
    try {
      JsonWriter writer = request.writer;
      if (pauseOnExceptions) {
        writer.name("pauseOnExceptions").value(true);
      }
      if (stepAction != StepAction.CONTINUE) {
        // yes,  rdp is ugly, it is very unclear how to step in/over/out
        String stupidRawStepType;
        writer.name("resumeLimit").beginObject().name("type");
        // ugly unclear poor  rdp - see mozilla sources firefox/toolkit/devtools/client/dbg-client.jsm to understand this shit
        switch (stepAction) {
          case IN:
            stupidRawStepType = "step";
            break;
          case OVER:
            stupidRawStepType = "next";
            break;
          case OUT:
            stupidRawStepType = "finish";
            break;
          default:
            throw new UnsupportedOperationException();
        }
        writer.value(stupidRawStepType).endObject();
      }
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    return request;
  }

  @NotNull
  public static Request<ThreadInterrupted> interrupt(@NotNull String thread) {
    return new DlvRequest<ThreadInterrupted>(thread, Method.INTERRUPT);
  }

  @NotNull
  public static Request<SourcesResult> sources(@NotNull String thread) {
    return new DlvRequest<SourcesResult>(thread, Method.SOURCES);
  }

  @NotNull
  public static DlvRequest<SetBreakpointResult> setBreakpoint(@NotNull String thread, @NotNull String url, int line, int column) {
    DlvRequest<SetBreakpointResult> request = new DlvRequest<SetBreakpointResult>(thread, Method.SET_BREAKPOINT);
    try {
      JsonWriter writer = request.writer;
      writer.name("location");
      writer.beginObject();
      writer.name("url").value(url);
      // ugly protocol: line and column numbers start with 1
      writer.name("line").value(line + 1);
      if (column != -1) {
        writer.name("column").value(column + 1);
      }
      writer.endObject();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    return request;
  }

  @NotNull
  public static DlvRequest<Void> deleteBreakpoint(@NotNull String actor) {
    return new DlvRequest<Void>(actor, Method.DELETE);
  }

  @NotNull
  public static DlvRequest<Void> evaluate(@NotNull String threadActor, @NotNull String expression, @NotNull String frame) {
    DlvRequest<Void> request = new DlvRequest<Void>(threadActor, Method.CLIENT_EVALUATE);
    try {
      JsonWriter writer = request.writer;
      writer.name("expression").value(expression);
      writer.name("frame").value(frame);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    return request;
  }

  @Nullable
  @Override
  public String getMethodName() {
    return null;
  }

  @Override
  public void finalize(int id) {
    try {
      writer.endObject();
      writer.close();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}