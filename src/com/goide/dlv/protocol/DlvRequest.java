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

package com.goide.dlv.protocol;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.jsonProtocol.OutMessage;
import org.jetbrains.jsonProtocol.Request;

import java.io.IOException;

public abstract class DlvRequest<T> extends OutMessage implements Request<T> {
  protected boolean argumentsObjectStarted;

  public DlvRequest() {
    this(null);
  }

  protected DlvRequest(@Nullable String command) {
    try {
      writer.name("method").value(command == null ? getMethodName() : command);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
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
}
