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
import org.jetbrains.jsonProtocol.OutMessage;
import org.jetbrains.jsonProtocol.Request;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class DlvRequest<RESULT> extends OutMessage implements Request<RESULT> {
  @NotNull public final Method method;

  public DlvRequest(@NotNull String actor, @NotNull Method type) {
    this.method = type;

    writeString("to", actor);
    writeString("type", method.protocolName);
  }

  // ugly protocol term is "type", but here we use correct term "method"
  public enum Method {
    RESUME,
    SET_BREAKPOINT, DELETE, 
    PROTOTYPE_AND_PROPERTIES, PROPERTY, PROTOTYPE, THREAD_GRIP, THREAD_GRIPS, RELEASE_MANY
    ;

    public final String protocolName;

    Method() {
      protocolName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
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