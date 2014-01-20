package com.goide;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.LayeredIcon;

import javax.swing.*;

public interface GoIcons {
  Icon ICON = IconLoader.findIcon("/icons/go.png");
  Icon TYPE = IconLoader.findIcon("/icons/type.png");
  Icon APPLICATION_RUN = Helper.createIconWithShift(AllIcons.Nodes.RunnableMark);
  Icon TEST_RUN = Helper.createIconWithShift(AllIcons.Nodes.JunitTestMark);
  Icon METHOD = AllIcons.Nodes.Method;
  Icon FUNCTION = AllIcons.Nodes.Function;
  Icon VARIABLE = AllIcons.Nodes.Variable;
  Icon CONST = new LayeredIcon(AllIcons.Nodes.Field, AllIcons.Nodes.FinalMark);  // todo: another icon
  Icon PARAMETER = AllIcons.Nodes.Parameter;
  Icon FIELD = AllIcons.Nodes.Field;
  Icon RECEIVER = AllIcons.Nodes.Parameter;
  Icon PACKAGE = AllIcons.Nodes.Package;
  Icon MODULE_ICON = IconLoader.findIcon("/icons/goModule.png");

  class Helper {
    public static LayeredIcon createIconWithShift(Icon mark) {
      LayeredIcon icon = new LayeredIcon(2) {
        @Override
        public int getIconHeight() {
          return ICON.getIconHeight();
        }
      };
      icon.setIcon(ICON, 0);
      icon.setIcon(mark, 1, 0, 8);
      return icon;
    }
  }
}
