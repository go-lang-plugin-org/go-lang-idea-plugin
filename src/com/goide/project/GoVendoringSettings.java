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

package com.goide.project;

import com.intellij.util.ThreeState;
import com.intellij.util.text.VersionComparatorUtil;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Tag("vendoring")
public class GoVendoringSettings {
  @NotNull
  public ThreeState vendorSupportEnabled = ThreeState.UNSURE;

  public static boolean supportsVendoringByDefault(@Nullable String sdkVersion) {
    if (sdkVersion == null || sdkVersion.length() < 3) {
      return false;
    }
    return VersionComparatorUtil.compare(sdkVersion.substring(0, 3), "1.6") > 0;
  }

  public static boolean supportsVendoring(@Nullable String sdkVersion) {
    if (sdkVersion == null || sdkVersion.length() < 3) {
      return false;
    }
    return VersionComparatorUtil.compare(sdkVersion.substring(0, 3), "1.4") > 0;
  }
}
