package com.goide.jps.model;

import com.goide.GoLibrariesState;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.ex.JpsElementBase;

public class JpsGoLibraries extends JpsElementBase<JpsGoLibraries> {
  @NotNull
  private GoLibrariesState myState = new GoLibrariesState();

  public JpsGoLibraries(@Nullable GoLibrariesState state) {
    if (state != null) {
      myState = state;
    }
  }

  @NotNull
  public GoLibrariesState getState() {
    return myState;
  }

  @NotNull
  @Override
  public JpsGoLibraries createCopy() {
    GoLibrariesState newState = new GoLibrariesState();
    XmlSerializerUtil.copyBean(myState, newState);
    return new JpsGoLibraries(newState);
  }

  @Override
  public void applyChanges(@NotNull JpsGoLibraries modified) {
    XmlSerializerUtil.copyBean(modified, myState);
  }
}
