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

package com.goide.configuration;

import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.ColorUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

public class GoUIUtil {
  private GoUIUtil() {}

  @NotNull
  public static JTextPane createDescriptionPane() {
    JTextPane result = new JTextPane();
    result.addHyperlinkListener(new BrowserHyperlinkListener());
    result.setContentType("text/html");
    Font descriptionFont = UIUtil.getLabelFont(UIUtil.FontSize.SMALL);
    HTMLEditorKit editorKit = UIUtil.getHTMLEditorKit();
    editorKit.getStyleSheet().addRule("body, p {" +
                                      "color:#" + ColorUtil.toHex(UIUtil.getLabelFontColor(UIUtil.FontColor.BRIGHTER)) + ";" +
                                      "font-family:" + descriptionFont.getFamily() + ";" +
                                      "font-size:" + descriptionFont.getSize() + "pt;}");
    result.setHighlighter(null);
    result.setEditorKit(editorKit);
    return result;
  }
}
