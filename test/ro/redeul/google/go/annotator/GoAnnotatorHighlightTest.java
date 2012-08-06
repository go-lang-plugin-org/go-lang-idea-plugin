package ro.redeul.google.go.annotator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationSession;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import org.junit.Assert;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoLightCodeInsightFixtureTestCase;
import ro.redeul.google.go.highlight.GoSyntaxHighlighter;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.util.GoTestUtils;

public class GoAnnotatorHighlightTest extends GoLightCodeInsightFixtureTestCase {
    public void testHighlight() throws Exception { doTest(); }
    public void testHighlightConsts() throws Exception { doTest(); }
    public void testNpeOnCallOrConversion() throws Exception { doTest(); }


    @Override
    protected String getTestDataRelativePath() {
        return "annotator/";
    }

    private void doTest() throws Exception {
        myFixture.addFileToProject("builtin.go", "package builtin\ntype byte byte\ntype int int\n");
        List<String> data;
        data = readInput(getTestDataPath() + getTestName(true) + ".go");
        String expected = data.get(1).trim();
        Assert.assertEquals(expected, processFile(data.get(0)).trim());
    }

    private List<String> readInput(String filePath) throws IOException {
        String content = new String(FileUtil.loadFileText(new File(filePath)));
        Assert.assertNotNull(content);
        String script = content.replace(GoTestUtils.MARKER_BEGIN, "").replaceAll("/\\*end\\.[^\\*/]*\\*/", "");
        return Arrays.asList(script, content);
    }

    protected String processFile(String fileText) throws Exception {
        GoFile file = (GoFile) myFixture.configureByText(GoFileType.INSTANCE, fileText);
        Document document = myFixture.getDocument(file);
        StringBuilder sb = new StringBuilder();
        int pos = 0;

        Set<TextAttributesKey> keys = getAllKeysFromGoSyntaxHighlighter();

        AnnotationHolderImpl holder = new AnnotationHolderImpl(new AnnotationSession(file));
        new GoAnnotator().annotate(file, holder);
        Collections.sort(holder, new Comparator<Annotation>() {
            @Override
            public int compare(Annotation o1, Annotation o2) {
                return o1.getStartOffset() - o2.getStartOffset();
            }
        });

        for (Annotation annotation : holder) {
            if (annotation.getSeverity() != HighlightSeverity.INFORMATION) {
                continue;
            }

            TextAttributesKey tak = annotation.getTextAttributes();
            if (tak == null || !keys.contains(tak)) {
                continue;
            }

            String tokenText = document.getText(new TextRange(annotation.getStartOffset(), annotation.getEndOffset()));
            if (pos > annotation.getStartOffset()) {
                int line = document.getLineNumber(annotation.getStartOffset()) + 1;
                fail(String.format("Token \"%s\" at line %d was highlighted multiple times.", tokenText, line));
            }

            sb.append(document.getText(new TextRange(pos, annotation.getStartOffset())))
                    .append("/*begin*/")
                    .append(tokenText)
                    .append("/*end.")
                    .append(tak.getExternalName())
                    .append("*/");
            pos = annotation.getEndOffset();
        }
        sb.append(document.getText(new TextRange(pos, document.getTextLength())));
        return sb.toString();
    }

    private Set<TextAttributesKey> getAllKeysFromGoSyntaxHighlighter() throws Exception {
        Set<TextAttributesKey> keys = new HashSet<TextAttributesKey>();
        for (Field field : GoSyntaxHighlighter.class.getFields()) {
            int modifiers = field.getModifiers();
            if (field.getType() == TextAttributesKey.class &&
                Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                keys.add((TextAttributesKey) field.get(null));
            }
        }
        return keys;
    }
}
