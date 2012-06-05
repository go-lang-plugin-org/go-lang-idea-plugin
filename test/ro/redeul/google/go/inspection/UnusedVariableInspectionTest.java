package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Assert;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.findUsages.GoVariableUsageStatVisitor;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.util.TestUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class UnusedVariableInspectionTest extends LightCodeInsightFixtureTestCase {
    public void testSimple() throws Exception{ doTest(); }
    public void testIfScope() throws Exception{ doTest(); }
    public void testForScope() throws Exception{ doTest(); }
    public void testTypeFields() throws Exception{ doTest(); }
    public void testUnusedConst() throws Exception{ doTest(); }
    public void testUnusedParameter() throws Exception{ doTest(); }
    public void testLocalHidesGlobal() throws Exception{ doTest(); }

    @Override
    protected String getBasePath() {
        String pluginHomePathRelative = PluginPathManager.getPluginHomePathRelative("google-go-language");
        return FileUtil.toSystemIndependentName(pluginHomePathRelative) + "/testdata/inspection/unusedVariable/";
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
        GoFile file = (GoFile) myFixture.configureByText(GoFileType.GO_FILE_TYPE, fileText);

        System.out.println(DebugUtil.psiToString(file, false, true));

        new GoVariableUsageStatVisitor(InspectionManager.getInstance(getProject())).visitElement(file);

        InspectionManager im = InspectionManager.getInstance(getProject());
        ProblemDescriptor[] problems = new UnusedVariableInspection().doCheckFile(file, im);

        Arrays.sort(problems, new Comparator<ProblemDescriptor>() {
            @Override
            public int compare(ProblemDescriptor o1, ProblemDescriptor o2) {
                return o1.getPsiElement().getTextOffset() - o2.getPsiElement().getTextOffset();
            }
        });

        StringBuilder sb = new StringBuilder();
        for (ProblemDescriptor pd : problems) {
            sb.append(pd.getPsiElement().getText()).append(" => ").append(pd.getDescriptionTemplate()).append("\n");
        }
        return sb.toString();
    }
}
