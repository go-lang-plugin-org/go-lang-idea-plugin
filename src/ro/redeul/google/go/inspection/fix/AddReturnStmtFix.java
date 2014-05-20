package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import static ro.redeul.google.go.lang.psi.GoPsiElementFactory.createStatements;

public class AddReturnStmtFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    public AddReturnStmtFix(GoFunctionDeclaration function) {
        super(function);
    }

    @NotNull
    @Override
    public String getText() {
        return "Add return statement";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Function Declaration";
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        if (!(file instanceof GoFile) || editor == null) {
            return;
        }

        Document doc = editor.getDocument();
        int lineNumber = doc.getLineNumber(editor.getCaretModel().getOffset());

        GoFile goFile = (GoFile) file;
        final PsiElement rightCurly;
        final PsiElement block;
        if (startElement instanceof GoFunctionDeclaration) {
            block = ((GoFunctionDeclaration) startElement).getBlock();
            rightCurly = block.getLastChild();
        } else {
            rightCurly = startElement;
            block = rightCurly.getParent();
        }

        if (block == null || !(block instanceof GoBlockStatement) ||
                rightCurly == null || !"}".equals(rightCurly.getText())) {
            return;
        }

        final PsiElement[] elements = createStatements(goFile, "    return\n");
        if (elements.length == 0) {
            return;
        }

        WriteCommandAction writeCommandAction = new WriteCommandAction(file.getProject(), file) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                block.addRangeBefore(elements[0], elements[elements.length - 1], rightCurly);
            }
        };

        writeCommandAction.execute();
        editor.getCaretModel().moveToOffset(doc.getLineEndOffset(lineNumber));
        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
    }
}
