package com.goide.jps;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.MessageHandler;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.DoneSomethingNotification;

import java.util.List;

public class BuildResult implements MessageHandler {
  @NotNull private final List<BuildMessage> myErrorMessages = ContainerUtil.newArrayList();
  @NotNull private final List<BuildMessage> myWarnMessages = ContainerUtil.newArrayList();
  @NotNull private final List<BuildMessage> myInfoMessages = ContainerUtil.newArrayList();
  private boolean myUpToDate = true;

  @Override
  public void processMessage(@NotNull BuildMessage msg) {
    if (msg.getKind() == BuildMessage.Kind.ERROR) {
      myErrorMessages.add(msg);
      myUpToDate = false;
    }
    else if (msg.getKind() == BuildMessage.Kind.WARNING) {
      myWarnMessages.add(msg);
    }
    else {
      myInfoMessages.add(msg);
    }
    if (msg instanceof DoneSomethingNotification) {
      myUpToDate = false;
    }
  }

  public void assertUpToDate() {
    assert myUpToDate : "Project sources weren't up to date";
  }

  public void assertFailed() {
    assert !isSuccessful() : "Build not failed as expected";
  }

  public boolean isSuccessful() {
    return myErrorMessages.isEmpty();
  }

  public void assertSuccessful() {
    final Function<BuildMessage,String> toStringFunction = StringUtil.createToStringFunction(BuildMessage.class);
    assert isSuccessful() : "Build failed. \nErrors:\n" + StringUtil.join(myErrorMessages, toStringFunction, "\n") +
                            "\nInfo messages:\n" + StringUtil.join(myInfoMessages, toStringFunction, "\n");
  }

  @NotNull
  public List<BuildMessage> getMessages(@NotNull BuildMessage.Kind kind) {
    if (kind == BuildMessage.Kind.ERROR) return myErrorMessages;
    else if (kind == BuildMessage.Kind.WARNING) return myWarnMessages;
    else return myInfoMessages;
  }
}
