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

import com.intellij.diagnostic.IdeErrorsDialog;
import com.intellij.diagnostic.LogMessageEx;
import com.intellij.diagnostic.ReportMessages;
import com.intellij.errorreport.bean.ErrorBean;
import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.idea.IdeaLogger;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Sends crash reports to Github.
 * Extensively inspired by the one used in the Android Studio.
 * https://android.googlesource.com/platform/tools/adt/idea/+/master/android/src/com/android/tools/idea/diagnostics/error/ErrorReporter.java
 * As per answer from here: http://devnet.jetbrains.com/message/5526206;jsessionid=F5422B4AF1AFD05AAF032636E5455E90#5526206
 */
public class ErrorReporter extends ErrorReportSubmitter {
  @Override
  public String getReportActionText() {
    return GoBundle.message("go.error.report.action");
  }

  @Override
  public boolean submit(@NotNull IdeaLoggingEvent[] events, String additionalInfo, @NotNull Component parentComponent, @NotNull Consumer<SubmittedReportInfo> consumer) {
    ErrorBean errorBean = new ErrorBean(events[0].getThrowable(), IdeaLogger.ourLastActionId);
    return doSubmit(events[0], parentComponent, consumer, errorBean, additionalInfo);
  }

  private static boolean doSubmit(final IdeaLoggingEvent event,
                                  final Component parentComponent,
                                  final Consumer<SubmittedReportInfo> callback,
                                  final ErrorBean bean,
                                  final String description) {
    final DataContext dataContext = DataManager.getInstance().getDataContext(parentComponent);

    bean.setDescription(description);
    bean.setMessage(event.getMessage());

    Throwable throwable = event.getThrowable();
    if (throwable != null) {
      final PluginId pluginId = IdeErrorsDialog.findPluginId(throwable);
      if (pluginId != null) {
        final IdeaPluginDescriptor ideaPluginDescriptor = PluginManager.getPlugin(pluginId);
        if (ideaPluginDescriptor != null && !ideaPluginDescriptor.isBundled()) {
          bean.setPluginName(ideaPluginDescriptor.getName());
          bean.setPluginVersion(ideaPluginDescriptor.getVersion());
        }
      }
    }

    Object data = event.getData();

    if (data instanceof LogMessageEx) {
      bean.setAttachments(((LogMessageEx)data).getAttachments());
    }

      LinkedHashMap<String, String> reportValues = IdeaITNProxy
      .getKeyValuePairs(bean,
                        ApplicationManager.getApplication(),
                        (ApplicationInfoEx)ApplicationInfo.getInstance(),
                        ApplicationNamesInfo.getInstance());

    final Project project = CommonDataKeys.PROJECT.getData(dataContext);

    Consumer<String> successCallback = new Consumer<String>() {
      @Override
      public void consume(String token) {
        final SubmittedReportInfo reportInfo = new SubmittedReportInfo(
          null, "Issue " + token, SubmittedReportInfo.SubmissionStatus.NEW_ISSUE);
        callback.consume(reportInfo);

        ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT,
                                                "Submitted",
                                                NotificationType.INFORMATION,
                                                null).setImportant(false).notify(project);
      }
    };

    Consumer<Exception> errorCallback = new Consumer<Exception>() {
      @Override
      public void consume(Exception e) {
        String message = GoBundle.message("go.error.report.message", e.getMessage());
        ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT,
                                                message,
                                                NotificationType.ERROR,
                                                NotificationListener.URL_OPENING_LISTENER).setImportant(false).notify(project);
      }
    };
    AnonymousFeedbackTask task =
      new AnonymousFeedbackTask(project, "Submitting error report", true, reportValues, successCallback, errorCallback);
    if (project == null) {
      task.run(new EmptyProgressIndicator());
    } else {
      ProgressManager.getInstance().run(task);
    }
    return true;
  }
}
