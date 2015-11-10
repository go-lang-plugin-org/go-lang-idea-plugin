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

package com.goide.runconfig.before;

import com.intellij.execution.BeforeRunTask;
import com.intellij.openapi.util.text.StringUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;

public class GoCommandBeforeRunTask extends BeforeRunTask<GoCommandBeforeRunTask> {
  @NonNls
  private static final String COMMAND_ATTRIBUTE = "command";

  private String myCommand;

  public GoCommandBeforeRunTask() {
    super(GoBeforeRunTaskProvider.ID);
  }

  public String getCommand() {
    return myCommand;
  }

  public void setCommand(String command) {
    myCommand = command;
  }
  
  @Override
  public void writeExternal(Element element) {
    super.writeExternal(element);
    if (myCommand != null) {
      element.setAttribute(COMMAND_ATTRIBUTE, myCommand);
    }
  }

  @Override
  public void readExternal(Element element) {
    super.readExternal(element);
    String command = element.getAttributeValue(COMMAND_ATTRIBUTE);
    if (command != null) {
      myCommand = command;
    }
  }

  @Override
  public String toString() {
    return "go " + StringUtil.notNullize(myCommand);
  }
}
