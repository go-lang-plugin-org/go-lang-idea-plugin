package ro.redeul.google.go.inspection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.QuickFix;
import com.intellij.codeInspection.ex.ProblemDescriptorImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import org.junit.Assert;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoLightCodeInsightFixtureTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.util.GoTestUtils;

public abstract class GoInspectionTestCase
    extends GoLightCodeInsightFixtureTestCase {

    protected AbstractWholeGoFileInspection createInspection() {
        try {
            String inspectionName =
                getClass().getName().replaceAll("Test$", "");

            return (AbstractWholeGoFileInspection)
                Class.forName(inspectionName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getTestDataRelativePath() {
        try {
            String name = getClass().getSimpleName();
            name = name.replaceAll("(Inspection)?Test$", "");
            return String.format("inspection/%s/",
                                 lowercaseFirstLetter(name, true));
        } catch (Exception e) {
            return "inspection/undefined/";
        }
    }

    protected void detectProblems(GoFile file, InspectionResult result)
        throws IllegalAccessException, InstantiationException {
        createInspection().doCheckFile(file, result);
    }

    protected void doTest() throws Exception {
        List<String> data;
        // TODO: remove this once all the inheritors of this class have
        //      been updated to use .go test cases instead of .test
        try {
            data = readInput(getTestDataPath() + getTestName(true) + ".go");
        } catch (IOException e) {
            data = readInput(getTestDataPath() + getTestName(true) + ".test");
        }

        String expected = data.get(1).trim();
        Assert.assertEquals(expected, processFile(data.get(0)).trim());
    }

    @Override
    protected void tearDown() throws Exception {
//        removeContentRoots();
        super.tearDown();
    }

    private List<String> readInput(String filePath) throws IOException {
        List<String> data = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        String content = new String(FileUtil.loadFileText(new File(filePath)));

        Assert.assertNotNull(content);
        int pos = -1;
        while ((pos = content.indexOf(GoTestUtils.MARKER_BEGIN,
                                      pos + 1)) >= 0) {
            pos += GoTestUtils.MARKER_BEGIN.length();
            int endPos = content.indexOf("/*end.", pos);
            String variable = content.substring(pos, endPos);
            String info = content.substring(endPos + 6,
                                            content.indexOf("*/", endPos));
            sb.append(variable).append(" => ").append(info).append("\n");
            pos = endPos;
        }
        data.add(content.replaceAll(GoTestUtils.MARKER_BEGIN, "")
                        .replaceAll("/\\*end\\.[^\\*/]\\*/", ""));
        data.add(sb.toString());
        return data;
    }

    protected String processFile(String fileText)
        throws InstantiationException, IllegalAccessException {
        GoFile file = (GoFile) myFixture.configureByText(GoFileType.INSTANCE,
                                                         fileText);
        Document document = myFixture.getDocument(file);
        InspectionResult result = new InspectionResult(getProject());
        detectProblems(file, result);
        List<ProblemDescriptor> problems = result.getProblems();

        Collections.sort(problems, new Comparator<ProblemDescriptor>() {
            @Override
            public int compare(ProblemDescriptor o1, ProblemDescriptor o2) {
                return o1.getStartElement()
                        .getTextOffset() - o2.getStartElement()
                        .getTextOffset();
            }
        });

        Logger logger = Logger.getInstance("intellijjjj");

        StringBuilder sb = new StringBuilder();
        for (ProblemDescriptor pd : problems) {
            TextRange range;
            if (pd instanceof ProblemDescriptorImpl) {
                range = ((ProblemDescriptorImpl) pd).getTextRange();
            } else {
                int start = pd.getStartElement().getTextOffset();
                int end = pd.getEndElement()
                            .getTextOffset() + pd.getEndElement()
                                                 .getTextLength();
                range = new TextRange(start, end);
            }
            String text = document.getText(range);

            sb.append(text
                    .replaceAll("\"?.*(, )?/\\*begin\\*/([^\\*/]*)/\\*end\\.[^\\*/]*\\*/(\\\\n)?\"?", "$2")
                    ).append(" => ").append(pd.getDescriptionTemplate());

            QuickFix[] fixes = pd.getFixes();

            if (fixes == null || fixes.length == 0) {
                sb.append("\n");

                continue;
            }

            for (QuickFix fix : fixes) {
                sb.append("|").append(fix.getClass().getSimpleName());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
