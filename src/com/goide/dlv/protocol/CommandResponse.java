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

import com.goide.dlv.JsonReaderEx;
import com.google.gson.stream.JsonToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jsonProtocol.JsonOptionalField;
import org.jetbrains.jsonProtocol.JsonType;

import java.util.Collections;
import java.util.List;

@JsonType
public interface CommandResponse {
  int id();

  @JsonOptionalField
  JsonReaderEx result();

  @JsonOptionalField
  ErrorInfo error();

  @JsonType
  interface ErrorInfo {
    String message();

    @JsonOptionalField
    List<String> data();

    int code();
  }

  final class CommandResponseImpl implements CommandResponse {
    private CommandResponse.ErrorInfo _error;
    private int _id = -1;
    private JsonReaderEx _result;

    public CommandResponseImpl(JsonReaderEx reader, String name) {
      if (name == null) {
        if (reader.hasNext() && reader.beginObject().hasNext()) {
          name = reader.nextName();
        }
        else {
          return;
        }
      }

      do {
        if (name.equals("error")) {
          _error = new M5m(reader, null);
        }
        else if (name.equals("id")) {
          _id = reader.nextInt();
        }
        else if (name.equals("result")) {
          _result = reader.subReader();
          reader.skipValue();
        }
        else {
          reader.skipValue();
        }
      }
      while ((name = reader.nextNameOrNull()) != null);

      reader.endObject();
    }

    @Override
    public CommandResponse.ErrorInfo error() {
      return _error;
    }

    @Override
    public int id() {
      return _id;
    }

    @Override
    public JsonReaderEx result() {
      return _result;
    }
  }

  final class M5m implements CommandResponse.ErrorInfo {
    private int _code = -1;
    private List<String> _data = Collections.emptyList();
    private String _message;

    M5m(JsonReaderEx reader, String name) {
      _message = nextNullableString(reader);
    }

    @Override
    public int code() {
      return _code;
    }

    @Override
    public List<String> data() {
      return _data;
    }

    @NotNull
    @Override
    public String message() {
      return _message;
    }

    private static String nextNullableString(JsonReaderEx reader) {
      if (reader.peek() == JsonToken.NULL) {
        reader.nextNull();
        return null;
      }
      else {
        return reader.nextString();
      }
    }

  }
  
  
}