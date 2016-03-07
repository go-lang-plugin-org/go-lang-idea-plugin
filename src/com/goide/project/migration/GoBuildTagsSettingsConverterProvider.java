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

package com.goide.project.migration;

import com.goide.GoConstants;
import com.goide.project.GoBuildTargetSettings;
import com.intellij.conversion.*;
import com.intellij.ide.impl.convert.JDomConvertingUtil;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.serialization.JDomSerializationUtil;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

public class GoBuildTagsSettingsConverterProvider extends ConverterProvider {
  protected GoBuildTagsSettingsConverterProvider() {
    super("go-build-tags-settings");
  }

  @NotNull
  @Override
  public String getConversionDescription() {
    return "Go project settings storing mechanism has been changed so project and its modules need to be updated";
  }

  @NotNull
  @Override
  public ProjectConverter createConverter(@NotNull ConversionContext context) {
    return new ProjectConverter() {
      private GoBuildTargetSettings newSettings;

      @NotNull
      private File getGoBuildFlagsFile() {return new File(context.getSettingsBaseDir(), "goBuildFlags.xml");}

      @Nullable
      @Override
      public ConversionProcessor<ProjectSettings> createProjectFileConverter() {
        return new ConversionProcessor<ProjectSettings>() {
          @Override
          public boolean isConversionNeeded(@NotNull ProjectSettings settings) {
            Element oldSettings = JDomSerializationUtil.findComponent(settings.getRootElement(), "GoBuildFlags");
            return oldSettings != null;
          }

          @Override
          public void process(@NotNull ProjectSettings settings) throws CannotConvertException {
            Element oldSettings = JDomSerializationUtil.findComponent(settings.getRootElement(), "GoBuildFlags");
            if (oldSettings != null) {
              newSettings = XmlSerializer.deserialize(oldSettings, GoBuildTargetSettings.class);
              oldSettings.detach();
            }
          }
        };
      }

      @Override
      public Collection<File> getAdditionalAffectedFiles() {
        return Collections.singletonList(getGoBuildFlagsFile());
      }

      @Override
      public boolean isConversionNeeded() {
        return getGoBuildFlagsFile().exists();
      }

      @Override
      public void preProcessingFinished() throws CannotConvertException {
        File oldSettingsFile = getGoBuildFlagsFile();
        if (oldSettingsFile.exists()) {
          Element oldSettingsRoot = JDomConvertingUtil.loadDocument(oldSettingsFile).getRootElement();
          Element buildFlagsSettings = JDomSerializationUtil.findComponent(oldSettingsRoot, "GoBuildFlags");
          if (buildFlagsSettings != null) {
            newSettings = XmlSerializer.deserialize(buildFlagsSettings, GoBuildTargetSettings.class);
            buildFlagsSettings.detach();
            //noinspection ResultOfMethodCallIgnored
            oldSettingsFile.delete();
          }
        }
      }

      @Nullable
      @Override
      public ConversionProcessor<ModuleSettings> createModuleFileConverter() {
        return new ConversionProcessor<ModuleSettings>() {
          @Override
          public boolean isConversionNeeded(@NotNull ModuleSettings settings) {
            return getGoBuildFlagsFile().exists();
          }

          @Override
          public void process(@NotNull ModuleSettings settings) throws CannotConvertException {
            Element rootElement = settings.getRootElement();
            Element goComponent =
              JDomSerializationUtil.findOrCreateComponentElement(rootElement, GoConstants.GO_MODULE_SESTTINGS_SERVICE_NAME);
            Element buildTags = XmlSerializer.serialize(newSettings);
            Element existingBuildTags = goComponent.getChild(buildTags.getName());
            if (existingBuildTags != null) {
              existingBuildTags.detach();
            }
            goComponent.addContent(buildTags);
          }
        };
      }
    };
  }
}
