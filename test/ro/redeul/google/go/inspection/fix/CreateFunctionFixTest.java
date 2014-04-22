package ro.redeul.google.go.inspection.fix;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class CreateFunctionFixTest extends GoEditorAwareTestCase {
    public void testSimple() throws Exception{ doTest(); }
    public void testLiteralFunction() throws Exception{ doTest(); }
    public void testLiteralFunctionPointerArg() throws Exception{ doTest(); }
    public void testLiteralFunctionSliceArg() throws Exception{ doTest(); }

    public void testLiteralFunctionComplexArg() throws Exception{ doTest(); }
    public void testLiteralFunctionBooleanExpArg() throws Exception{ doTest(); }
    public void testLiteralFunctionSmartGen() throws Exception{ doTest(); }
    public void testLiteralFunctionSmartGenVariadicArgs() throws Exception{ doTest(); }

    @Override
    protected void invoke(final Project project, final Editor editor, final GoFile file) {
        PsiElement element = file.findElementAt(editor.getSelectionModel().getSelectionStart());
        final GoLiteralExpression identifier = findParentOfType(element, GoLiteralExpression.class);
        assertNotNull(identifier);
        assertInstanceOf(identifier.getLiteral(), GoLiteralIdentifier.class);

        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                new CreateFunctionFix(identifier).invoke(project, file, editor, identifier, identifier);
            }
        }, "", null);
    }

    @Override
    protected String getTestDataRelativePath() {
        return "fixes/createFunction/";
    }

    private boolean testDataFileExists(String fileName) {
        String absName = getTestDataPath() + File.separator + fileName;
        return LocalFileSystem.getInstance().findFileByPath(absName) != null;
    }

    @Override
    protected void doTest() throws Exception {
        List<String> files = new LinkedList<String>();
        if (testDataFileExists("builtin.go")) {
            files.add("builtin.go");
        }
        Collections.reverse(files);
        myFixture.configureByFiles(files.toArray(new String[files.size()]));
        super.doTest();
    }

}
