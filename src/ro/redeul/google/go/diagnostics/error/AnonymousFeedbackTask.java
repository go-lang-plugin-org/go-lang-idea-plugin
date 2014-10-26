/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ro.redeul.google.go.diagnostics.error;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import com.intellij.util.net.HttpConfigurable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;

import static ro.redeul.google.go.diagnostics.error.AnonymousFeedback.sendFeedback;

/**
 * Sends crash reports to Github.
 * Extensively inspired by the one used in the Android Studio.
 * https://android.googlesource.com/platform/tools/adt/idea/+/master/android/src/com/android/tools/idea/diagnostics/error/ErrorReporter.java
 * As per answer from here: http://devnet.jetbrains.com/message/5526206;jsessionid=F5422B4AF1AFD05AAF032636E5455E90#5526206
 */
public class AnonymousFeedbackTask extends Task.Backgroundable {
  private final Consumer<String> myCallback;
  private final Consumer<Exception> myErrorCallback;
  private final LinkedHashMap<String, String> myParams;

  public AnonymousFeedbackTask(@Nullable Project project,
                               @NotNull String title,
                               boolean canBeCancelled,
                               LinkedHashMap<String, String> params,
                               final Consumer<String> callback,
                               final Consumer<Exception> errorCallback) {
    super(project, title, canBeCancelled);

    myParams = params;
    myCallback = callback;
    myErrorCallback = errorCallback;
  }

  @Override
  public void run(@NotNull ProgressIndicator indicator) {
    indicator.setIndeterminate(true);
    try {
      String token = sendFeedback(new ProxyHttpConnectionFactory(), myParams);
      myCallback.consume(token);
    }
    catch (Exception e) {
      myErrorCallback.consume(e);
    }
  }

  private static class ProxyHttpConnectionFactory extends AnonymousFeedback.HttpConnectionFactory {
    @Override
    protected HttpURLConnection openHttpConnection(String url) throws IOException {
      return HttpConfigurable.getInstance().openHttpConnection(url);
    }
  }
}
