package com.goide.appengine;

import com.goide.GoIcons;
import com.intellij.appengine.GoogleAppEngineIcons;
import com.intellij.ui.LayeredIcon;

import javax.swing.*;

public interface GoAppEngineIcons {
  Icon ICON = new LayeredIcon(GoIcons.ICON, GoogleAppEngineIcons.AppEngineMark);
}
