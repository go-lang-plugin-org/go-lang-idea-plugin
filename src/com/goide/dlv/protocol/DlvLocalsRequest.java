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

import java.util.ArrayList;
import java.util.List;

public abstract class DlvLocalsRequest extends DlvRequest<List<Api.Variable>> {
  public DlvLocalsRequest() {
    final List<String> objects = new ArrayList<String>();
    objects.add(null);
    writeStringList(argumentsKeyName(), objects);
  }

  @Override
  protected boolean needObject() {
    return false;
  }

  public static class DlvLocalVarsRequest extends DlvLocalsRequest {
    @Override
    public String getMethodName() {
      return "RPCServer.ListLocalVars";
    }
  }

  public static class DlvFunctionArgsRequest extends DlvLocalsRequest {
    @Override
    public String getMethodName() {
      return "RPCServer.ListFunctionArgs";
    }
  }
}
