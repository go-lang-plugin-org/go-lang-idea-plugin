/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.ui;

import com.goide.GoConstants;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.util.PlatformUtils;
import org.jetbrains.annotations.NotNull;

public class PluginIsObsoleteNotification implements ApplicationComponent {
  private static final String GO_PLUGIN_IS_OBSOLETE_NOTIFICATION_SHOWN = "go.plugin.is.obsolete.notification.shown";

  @Override
  public void initComponent() {
    PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
    //noinspection SynchronizationOnLocalVariableOrMethodParameter
    synchronized (propertiesComponent) {
      if (propertiesComponent.getValue(GO_PLUGIN_IS_OBSOLETE_NOTIFICATION_SHOWN) != null) {
        return;
      }
      propertiesComponent.setValue(GO_PLUGIN_IS_OBSOLETE_NOTIFICATION_SHOWN, "1");
    }

    Notifications.Bus.notify(GoConstants.GO_NOTIFICATION_GROUP.createNotification(
      "The latest Go plugin version compatible with the IDE is used",
      "To receive Go plugin updates <a href=\"https://jetrbrains.com/" + PlatformUtils.getPlatformPrefix() + "\">install</a> new IDE version.",
      NotificationType.WARNING, NotificationListener.URL_OPENING_LISTENER));
  }

  @Override
  public void disposeComponent() {

  }

  @NotNull
  @Override
  public String getComponentName() {
    return getClass().getName();
  }
}