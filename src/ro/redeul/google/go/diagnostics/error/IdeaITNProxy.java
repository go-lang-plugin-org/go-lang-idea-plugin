/*
 * Copyright 2000-2013 JetBrains s.r.o.
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
package ro.redeul.google.go.diagnostics.error;

import com.intellij.errorreport.bean.ErrorBean;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.diagnostic.Attachment;
import com.intellij.util.SystemProperties;

import java.util.LinkedHashMap;

/**
 * Sends crash reports to Github.
 * Extensively inspired by the one used in the Android Studio.
 * https://android.googlesource.com/platform/tools/adt/idea/+/master/android/src/com/android/tools/idea/diagnostics/error/ErrorReporter.java
 * As per answer from here: http://devnet.jetbrains.com/message/5526206;jsessionid=F5422B4AF1AFD05AAF032636E5455E90#5526206
 */
public class IdeaITNProxy {
    public static LinkedHashMap<String, String> getKeyValuePairs(ErrorBean error,
                                                              Application application,
                                                              ApplicationInfoEx appInfo,
                                                              ApplicationNamesInfo namesInfo) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>(21);

        params.put("error.description", error.getDescription());

        params.put("Plugin Name", error.getPluginName());
        params.put("Plugin Version", error.getPluginVersion());

        params.put("OS Name", SystemProperties.getOsName());
        params.put("Java version", SystemProperties.getJavaVersion());
        params.put("Java vm vendor", SystemProperties.getJavaVmVendor());

        params.put("App Name", namesInfo.getProductName());
        params.put("App Full Name", namesInfo.getFullProductName());
        params.put("App Version name", appInfo.getVersionName());
        params.put("Is EAP", Boolean.toString(appInfo.isEAP()));
        params.put("App Build", appInfo.getBuild().asString());
        params.put("App Version", appInfo.getFullVersion());

        params.put("Last Action", error.getLastAction());

        //params.put("previous.exception", error.getPreviousException() == null ? null : Integer.toString(error.getPreviousException()));

        params.put("error.message", error.getMessage());
        params.put("error.stacktrace", error.getStackTrace());

        for (Attachment attachment : error.getAttachments()) {
            params.put("attachment.name", attachment.getName());
            params.put("attachment.value", attachment.getEncodedBytes());
        }

        return params;
    }
}
