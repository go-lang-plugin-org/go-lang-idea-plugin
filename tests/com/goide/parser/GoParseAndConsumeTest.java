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

package com.goide.parser;

import com.goide.GoParserDefinition;
import com.intellij.util.indexing.IndexingDataKeys;

public class GoParseAndConsumeTest extends GoParserTestBase {
  public GoParseAndConsumeTest()                 { super("parser", "go", new GoParserDefinition()); }

  public void testTypeInBlock() throws Exception { doTest(); }

  protected void doTest() throws Exception {
    String name = getTestName();
    String text = loadFile(name + "." + myFileExt);
    myFile = createPsiFile(name, text);
    myFile.putUserData(IndexingDataKeys.VIRTUAL_FILE, myFile.getVirtualFile());
    ensureParsed(myFile);
    checkResult(name, myFile);
  }
}
