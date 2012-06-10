package ro.redeul.google.go.inspection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Assert;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.util.TestUtils;

public abstract class AbstractProblemDescriptionBasedTest extends LightCodeInsightFixtureTestCase {
    protected abstract String getInspectionName();

    protected abstract List<ProblemDescriptor> detectProblems(GoFile file, InspectionManager inspectionManager);

    @Override
    protected final String getBasePath() {
        String pluginHomePathRelative = PluginPathManager.getPluginHomePathRelative("google-go-language");
        String systemName = FileUtil.toSystemIndependentName(pluginHomePathRelative);
        return systemName + "/testdata/inspection/" + getInspectionName() + "/";
    }

    protected void doTest() throws Exception {
        final List<String> data = readInput(getTestDataPath() + getTestName(true) + ".test");
        String expected = data.get(1).trim();
        Assert.assertEquals(expected, processFile(data.get(0)).trim());
    }

    private List<String> readInput(String filePath) throws IOException {
        List<String> data = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        String content = new String(FileUtil.loadFileText(new File(filePath)));

        Assert.assertNotNull(content);
        int pos = -1;
        while ((pos = content.indexOf(TestUtils.BEGIN_MARKER, pos + 1)) >= 0) {
            pos += TestUtils.BEGIN_MARKER.length();
            int endPos = content.indexOf("<end.", pos);
            String variable = content.substring(pos, endPos);
            String info = content.substring(endPos + 5, content.indexOf(">", endPos));
            sb.append(variable).append(" => ").append(info).append("\n");
            pos = endPos;
        }
        data.add(content.replaceAll(TestUtils.BEGIN_MARKER, "").replaceAll("<end\\.[^>]*>", ""));
        data.add(sb.toString());
        return data;
    }

    protected String processFile(String fileText) {
        GoFile file = (GoFile) myFixture.configureByText(GoFileType.INSTANCE, fileText);
        Document document = myFixture.getDocument(file);

        System.out.println(DebugUtil.psiToString(file, false, true));

        InspectionManager im = InspectionManager.getInstance(getProject());
        List<ProblemDescriptor> problems = detectProblems(file, im);

        Collections.sort(problems, new Comparator<ProblemDescriptor>() {
            @Override
            public int compare(ProblemDescriptor o1, ProblemDescriptor o2) {
                return o1.getStartElement()
                         .getTextOffset() - o2.getStartElement()
                                              .getTextOffset();
            }
        });

        StringBuilder sb = new StringBuilder();
        for (ProblemDescriptor pd : problems) {
            int start = pd.getStartElement().getTextOffset();
            int end = pd.getEndElement().getTextOffset() + pd.getEndElement().getTextLength();
            String text = document.getText(new TextRange(start, end));
            sb.append(text).append(" => ").append(pd.getDescriptionTemplate()).append("\n");
        }
        return sb.toString();
    }
}
