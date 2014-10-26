package ro.redeul.google.go.diagnostics.error;

import com.google.gson.Gson;
import com.intellij.ide.plugins.IdeaPluginDescriptorImpl;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnonymousFeedback {

    public AnonymousFeedback() {
    }

    public static String sendFeedback(
            AnonymousFeedback.HttpConnectionFactory httpConnectionFactory,
            LinkedHashMap<String, String> environmentDetails) throws IOException {

        sendFeedback(httpConnectionFactory, convertToGithubIssueFormat(environmentDetails));

        return Long.toString(System.currentTimeMillis());
    }

    private static byte[] convertToGithubIssueFormat(LinkedHashMap<String, String> environmentDetails) {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>(5);
        result.put("title", "[auto-generated] Crash in plugin");
        result.put("body", generateGithubIssueBody(environmentDetails));

        return ((new Gson()).toJson(result)).getBytes(Charset.forName("UTF-8"));
    }

    private static String generateGithubIssueBody(LinkedHashMap<String, String> body) {
        String errorDescription = body.get("error.description");
        if (errorDescription == null) {
            errorDescription = "";
        }
        body.remove("error.description");

        String errorMessage = body.get("error.message");
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "invalid error";
        }
        body.remove("error.message");

        String stackTrace = body.get("error.stacktrace");
        if (stackTrace == null || stackTrace.isEmpty()) {
            stackTrace = "invalid stacktrace";
        }
        body.remove("error.stacktrace");

        String result = "";

        if (!errorDescription.isEmpty()) {
            result += errorDescription + "\n\n";
        }

        for (Map.Entry<String, String> entry : body.entrySet()) {
            result += entry.getKey() + ": " + entry.getValue() + "\n";
        }

        result += "\n```\n" + stackTrace + "\n```\n";

        result += "\n```\n" + errorMessage + "\n```";

        return result;
    }

    private static void sendFeedback(AnonymousFeedback.HttpConnectionFactory httpConnectionFactory, byte[] payload) throws IOException {
        String url = "https://api.github.com/repos/go-lang-plugin-org/go-lang-idea-plugin/issues";
        String userAgent = "golang IntelliJ IDEA plugin";

        IdeaPluginDescriptorImpl pluginDescriptor = (IdeaPluginDescriptorImpl) PluginManager.getPlugin(PluginId.getId("ro.redeul.google.go"));
        if (pluginDescriptor != null) {
            String name = pluginDescriptor.getName();
            String version = pluginDescriptor.getVersion();
            userAgent = name + " (" + version + ")";
        }

        HttpURLConnection httpURLConnection = connect(httpConnectionFactory, url);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("User-Agent", userAgent);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Authorization", "token xxxx");
        OutputStream outputStream = httpURLConnection.getOutputStream();

        try {
            outputStream.write(payload);
        } finally {
            outputStream.close();
        }

        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode != 201) {
            throw new RuntimeException("Expected HTTP_CREATED (201), obtained " + responseCode);
        }
    }

    private static HttpURLConnection connect(AnonymousFeedback.HttpConnectionFactory httpConnectionFactory, String url) throws IOException {
        HttpURLConnection httpURLConnection = httpConnectionFactory.openHttpConnection(url);
        httpURLConnection.setConnectTimeout(5000);
        httpURLConnection.setReadTimeout(5000);
        return httpURLConnection;
    }

    public static class HttpConnectionFactory {
        public HttpConnectionFactory() {
        }

        protected HttpURLConnection openHttpConnection(String url) throws IOException {
            return (HttpURLConnection) ((new URL(url)).openConnection());
        }
    }
}
