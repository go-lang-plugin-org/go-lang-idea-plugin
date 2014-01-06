package com.goide;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.LayeredIcon;

import javax.swing.*;

public interface GoIcons {
  Icon ICON = IconLoader.findIcon("/icons/go.png");
  Icon TYPE = IconLoader.findIcon("/icons/type.png"); // todo: retina support
  Icon METHOD = AllIcons.Nodes.Method;
  Icon FUNCTION = AllIcons.Nodes.Function;
  Icon VARIABLE = AllIcons.Nodes.Variable;
  Icon CONST = new LayeredIcon(AllIcons.Nodes.Field, AllIcons.Nodes.FinalMark);  // todo: another icon
  Icon PARAMETER = AllIcons.Nodes.Parameter;
  Icon PACKAGE = AllIcons.Nodes.Package;
  Icon MODULE_ICON = IconLoader.findIcon("/icons/goModule.png");
}
