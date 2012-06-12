package ro.redeul.google.go.inspection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import org.junit.Assert;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoLightCodeInsightFixtureTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.util.GoTestUtils;

public abstract class GoInspectionTestCase<T extends AbstractWholeGoFileInspection>
    extends GoLightCodeInsightFixtureTestCase
{
    Class<T> inspectionType;

    protected GoInspectionTestCase(Class<T> inspectionType) {
        this.inspectionType = inspectionType;
    }

    @Override
    protected String getTestDataRelativePath() {
        try {
            return String.format("inspection/%s/",
                                 inspectionType.newInstance().getID());
        } catch (Exception e) {
            return "inspection/undefined/";
        }
    }

    protected List<ProblemDescriptor> detectProblems(GoFile file, InspectionManager inspectionManager)
        throws IllegalAccessException, InstantiationException
    {
        return inspectionType.newInstance().doCheckFile(file, inspectionManager, true);
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
        while ((pos = content.indexOf(GoTestUtils.MARKER_BEGIN, pos + 1)) >= 0) {
            pos += GoTestUtils.MARKER_BEGIN.length();
            int endPos = content.indexOf("<end.", pos);
            String variable = content.substring(pos, endPos);
            String info = content.substring(endPos + 5, content.indexOf(">", endPos));
            sb.append(variable).append(" => ").append(info).append("\n");
            pos = endPos;
        }
        data.add(content.replaceAll(GoTestUtils.MARKER_BEGIN, "").replaceAll("<end\\.[^>]*>", ""));
        data.add(sb.toString());
        return data;
    }

    protected String processFile(String fileText)
        throws InstantiationException, IllegalAccessException {
        GoFile file = (GoFile) myFixture.configureByText(GoFileType.INSTANCE, fileText);
        Document document = myFixture.getDocument(file);

//        System.out.println(DebugUtil.psiToString(file, false, true));

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
