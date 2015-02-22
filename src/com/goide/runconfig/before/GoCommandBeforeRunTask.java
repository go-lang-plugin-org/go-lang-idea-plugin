package com.goide.runconfig.before;

import com.intellij.execution.BeforeRunTask;
import com.intellij.openapi.util.text.StringUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

public class GoCommandBeforeRunTask extends BeforeRunTask<GoCommandBeforeRunTask> {
  @NonNls
  private static final String COMMAND_ATTRIBUTE = "command";

  private String myCommand;

  public GoCommandBeforeRunTask() {
    this(null);
  }

  public GoCommandBeforeRunTask(@Nullable String command) {
    super(GoBeforeRunTaskProvider.ID);
    myCommand = command;
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
    final String command = element.getAttributeValue(COMMAND_ATTRIBUTE);
    if (command != null) {
      myCommand = command;
    }
  }

  @Override
  public String toString() {
    return "go " + StringUtil.notNullize(myCommand);
  }
}
