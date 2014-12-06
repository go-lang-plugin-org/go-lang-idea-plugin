/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader
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

package com.goide.completion;

public class GoCompletionUtil {
  public static final int KEYWORD_PRIORITY = 20;
  public static final int CONTEXT_KEYWORD_PRIORITY = 25;
  public static final int FUNCTION_PRIORITY = 10;
  public static final int FUNCTION_WITH_PACKAGE_PRIORITY = 0;
  public static final int TYPE_PRIORITY = 15;
  public static final int TYPE_CONVERSION = 15;
  public static final int VAR_PRIORITY = 15;
  public static final int LABEL_PRIORITY = 15;
  public static final int PACKAGE_PRIORITY = 5;

  private GoCompletionUtil() {
    
  }
}
