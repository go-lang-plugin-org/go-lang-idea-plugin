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

import com.goide.dlv.JsonReaderEx;
import com.sun.istack.internal.NotNull;
import org.jetbrains.jsonProtocol.JsonParseMethod;

public abstract class DlvProtocolReader {
  @JsonParseMethod
  @NotNull
  public abstract ListTabsResult readListTabsResult(@NotNull JsonReaderEx reader, @NotNull String nextName);

  @JsonParseMethod
  @NotNull
  public abstract TabAttached readAttachToTabResult(@NotNull JsonReaderEx reader, @NotNull String nextName);

  @JsonParseMethod
  @NotNull
  public abstract ThreadInterrupted readThreadInterrupted(@NotNull JsonReaderEx reader, @NotNull String nextName);

  @JsonParseMethod
  @NotNull
  public abstract SetBreakpointResult readSetBreakpointResult(@NotNull JsonReaderEx reader, @NotNull String nextName);

  @JsonParseMethod
  @NotNull
  public abstract SourcesResult readSourcesResult(@NotNull JsonReaderEx reader, @NotNull String nextName);

  @JsonParseMethod
  @NotNull
  public abstract Source readSource(@NotNull JsonReaderEx reader, @NotNull String nextName);

  @JsonParseMethod
  @NotNull
  public abstract FramesResult readFramesResult(@NotNull JsonReaderEx reader, @NotNull String nextName);

  @JsonParseMethod
  @NotNull
  public abstract PrototypeAndPropertiesResult readPrototypeAndPropertiesResult(@NotNull JsonReaderEx reader, @NotNull String nextName);

  @JsonParseMethod
  @NotNull
  public abstract PropertyResult readPropertyResult(@NotNull JsonReaderEx reader, @NotNull String nextName);

  @JsonParseMethod
  @NotNull
  public abstract PrototypeResult readPrototypeResult(@NotNull JsonReaderEx reader, @NotNull String nextName);

  @JsonParseMethod
  @NotNull
  public abstract SourceResult readSourceResult(@NotNull JsonReaderEx reader, @NotNull String nextName);

  @JsonParseMethod
  @NotNull
  public abstract Bindings readBindings(@NotNull JsonReaderEx reader, @NotNull String nextName);
}