/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.LayeredIcon;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public interface GoIcons {
  Icon ICON = IconLoader.findIcon("/icons/go.png");
  Icon TYPE = IconLoader.findIcon("/icons/type.png");
  Icon APPLICATION_RUN = Helper.createIconWithShift(ICON, AllIcons.Nodes.RunnableMark);
  Icon TEST_RUN = Helper.createIconWithShift(ICON, AllIcons.Nodes.JunitTestMark);
  Icon METHOD = AllIcons.Nodes.Method;
  Icon FUNCTION = AllIcons.Nodes.Function;
  Icon VARIABLE = AllIcons.Nodes.Variable;
  Icon CONSTANT = new LayeredIcon(AllIcons.Nodes.Field, AllIcons.Nodes.FinalMark);  // todo: another icon
  Icon PARAMETER = AllIcons.Nodes.Parameter;
  Icon FIELD = AllIcons.Nodes.Field;
  Icon LABEL = null; // todo: we need an icon here!
  Icon RECEIVER = AllIcons.Nodes.Parameter;
  Icon PACKAGE = AllIcons.Nodes.Package;
  Icon MODULE_ICON = IconLoader.findIcon("/icons/goModule.png");
  Icon DEBUG = ICON;
  Icon DIRECTORY = PlatformIcons.DIRECTORY_CLOSED_ICON;

  class Helper {
    @NotNull
    public static LayeredIcon createIconWithShift(@NotNull final Icon base, Icon mark) {
      LayeredIcon icon = new LayeredIcon(2) {
        @Override
        public int getIconHeight() {
          return base.getIconHeight();
        }
      };
      icon.setIcon(base, 0);
      icon.setIcon(mark, 1, 0, base.getIconWidth() / 2);
      return icon;
    }
  }
}
