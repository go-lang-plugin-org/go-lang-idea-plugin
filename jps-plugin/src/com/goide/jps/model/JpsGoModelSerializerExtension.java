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

package com.goide.jps.model;

import com.goide.GoConstants;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.serialization.JpsGlobalExtensionSerializer;
import org.jetbrains.jps.model.serialization.JpsModelSerializerExtension;
import org.jetbrains.jps.model.serialization.JpsProjectExtensionSerializer;
import org.jetbrains.jps.model.serialization.library.JpsSdkPropertiesSerializer;
import org.jetbrains.jps.model.serialization.module.JpsModulePropertiesSerializer;

import java.util.Collections;
import java.util.List;

public class JpsGoModelSerializerExtension extends JpsModelSerializerExtension {
  @NotNull
  @Override
  public List<? extends JpsModulePropertiesSerializer<?>> getModulePropertiesSerializers() {
    return Collections.singletonList(JpsGoModuleType.createModuleLibrariesSerializer());
  }
  
  @NotNull
  @Override
  public List<? extends JpsProjectExtensionSerializer> getProjectExtensionSerializers() {
    return Collections.singletonList(new JpsGoProjectLibrariesSerializer());
  }

  @NotNull
  @Override
  public List<? extends JpsGlobalExtensionSerializer> getGlobalExtensionSerializers() {
    return Collections.singletonList(new JpsGoApplicationLibrariesSerializer());
  }

  @NotNull
  @Override
  public List<? extends JpsSdkPropertiesSerializer<?>> getSdkPropertiesSerializers() {
    return Collections.singletonList(new JpsSdkPropertiesSerializer<JpsDummyElement>(GoConstants.SDK_TYPE_ID, JpsGoSdkType.INSTANCE) {
      @NotNull
      @Override
      public JpsDummyElement loadProperties(@Nullable Element propertiesElement) {
        return JpsElementFactory.getInstance().createDummyElement();
      }

      @Override
      public void saveProperties(@NotNull JpsDummyElement properties, @NotNull Element element) {
      }
    });
  }
}
