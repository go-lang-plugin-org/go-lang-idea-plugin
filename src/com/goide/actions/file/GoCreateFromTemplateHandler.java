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

package com.goide.actions.file;

import com.goide.GoConstants;
import com.goide.GoFileType;
import com.intellij.ide.fileTemplates.DefaultCreateFromTemplateHandler;
import com.intellij.ide.fileTemplates.FileTemplate;

import java.util.Map;

public class GoCreateFromTemplateHandler extends DefaultCreateFromTemplateHandler {
  @Override
  public boolean handlesTemplate(FileTemplate template) {
    return template.isTemplateOfType(GoFileType.INSTANCE);
  }

  @Override
  public void prepareProperties(Map<String, Object> props) {
    Object name = props.get(FileTemplate.ATTRIBUTE_NAME);
    Object packageName = props.get(GoTemplatePropertiesProvider.GO_PACKAGE_NAME);
    if (name instanceof String && packageName instanceof String) {
      if (((String)name).endsWith(GoConstants.TEST_SUFFIX) || ((String)name).endsWith(GoConstants.TEST_SUFFIX_WITH_EXTENSION)) {
        props.put(GoTemplatePropertiesProvider.GO_PACKAGE_NAME_WITH_TEST_SUFFIX, packageName + GoConstants.TEST_SUFFIX);
      }
    }
  }
}
