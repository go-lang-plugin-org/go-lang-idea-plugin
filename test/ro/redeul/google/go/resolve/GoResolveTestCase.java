package ro.redeul.google.go.resolve;

import java.io.File;

import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.ResolveTestCase;

public abstract class GoResolveTestCase extends ResolveTestCase {

    protected abstract String getRelativeDataPath();

    @Override
    protected String getTestDataPath() {
        String pluginHome =
            FileUtil.toSystemIndependentName(
                PluginPathManager.getPluginHomePathRelative(
                    "google-go-language")) + "/testdata";

        String communityPath =
            PlatformTestUtil.getCommunityPath()
                            .replace(File.separatorChar, '/');

        String path =
            String.format("%s%s/%s/%s", communityPath, pluginHome,
                          getBasePath(), getRelativeDataPath());

        if (new File(path).exists())
            return path;

        return String.format("%s%s/../%s/%s", communityPath, pluginHome,
                             getBasePath(), getRelativeDataPath());
    }

    private String getBasePath() {
        return "resolve";
    }

    protected PsiReference resolve() throws Exception {
        return configureByFile(getTestName(false) + ".go");
    }

    protected String getTestName() {
        String name = getTestName(false);

        name = StringUtil.trimStart(name, "test");
        if (StringUtil.isEmpty(name)) {
            return getRelativeDataPath();
        }

        String processedName = getRelativeDataPath() + File.separator;
        boolean isFirst = true;

        for (String s : name.split("(?<=\\p{Lower})(?=\\p{Upper})")) {
            if (isFirst) {
                processedName += s.toLowerCase();
                isFirst = false;
            } else {
                processedName += File.separator + s.toLowerCase();
            }
        }

        return processedName;
    }
}
