package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.util.GoUtil;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.util.EditorUtil.pressEnter;
import static ro.redeul.google.go.util.EditorUtil.reformatLines;

public class CreateFunctionFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    private PsiFile workingFile = null;

    public CreateFunctionFix(@Nullable PsiElement element) {
        super(element);
    }


    public CreateFunctionFix(PsiElement element, PsiElement resolve) {
        super(element);
    }

    public CreateFunctionFix(PsiElement element, PsiFile workingFile) {
        super(element);
        this.workingFile = workingFile;
    }

    @NotNull
    @Override
    public String getText() {
        return "Create function \"" + getStartElement().getText() + "\"";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Variable Declaration";
    }


    @Override
    public void invoke(@NotNull final Project project,
                       @NotNull PsiFile file,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull final PsiElement startElement, @NotNull PsiElement endElement) {
        final PsiElement e;
        final int insertPoint;
        final Editor wEditor;
        final PsiFile wFile;
        final String fnArguments = GoUtil.InspectionGenFuncArgs(startElement);
        if (workingFile == null) {
            wFile = file;
            wEditor = editor;
            e = startElement;
            GoFunctionDeclaration fd = findParentOfType(e, GoFunctionDeclaration.class);
            while (fd instanceof GoLiteralFunction) {
                fd = findParentOfType(fd.getParent(), GoFunctionDeclaration.class);
            }


            if (fd != null) {
                insertPoint = fd.getTextRange().getEndOffset();
            } else {
                insertPoint = file.getTextRange().getEndOffset();
            }

        } else {
            e = startElement.getLastChild();
            insertPoint = workingFile.getTextLength();
            FileEditorManager.getInstance(project).openFile(workingFile.getVirtualFile(), true, true);
            wEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            wFile = this.workingFile;
        }


        Document doc = PsiDocumentManager.getInstance(project).getDocument(wFile);
        if (doc == null) {
            return;
        }

        int insertPoint1 = insertPoint;
        if (((GoFile) wFile).getPackageName().equals("")) {
            String packStr = String.format("\npackage %s", startElement.getFirstChild().getText());
            doc.insertString(insertPoint1, packStr);
            insertPoint1 += packStr.length();
        }

        doc.insertString(insertPoint1, String.format("\n\nfunc %s(%s) {\n}", e.getText(), fnArguments));
        if (wEditor != null) {
            int line = doc.getLineNumber(insertPoint1);
            int offset = doc.getLineEndOffset(line + 2);
            wEditor.getCaretModel().moveToOffset(offset);
            reformatLines(wFile, wEditor, line, line + 3);
            pressEnter(wEditor);
        }
    }
}
