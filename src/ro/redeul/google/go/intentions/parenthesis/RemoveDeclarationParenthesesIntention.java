package ro.redeul.google.go.intentions.parenthesis;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElementFactory;

import static ro.redeul.google.go.intentions.parenthesis.ParenthesisUtil.getRightParenthesis;
import static ro.redeul.google.go.intentions.parenthesis.ParenthesisUtil.hasOnlyOneDeclaration;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;

public class RemoveDeclarationParenthesesIntention extends Intention {
    @Override
    protected boolean satisfiedBy(PsiElement element) {
        return getRightParenthesis(element) != null && hasOnlyOneDeclaration(element);
    }

    @Override
    protected void processIntention(@NotNull final PsiElement element, Editor editor)
            throws IncorrectOperationException {

        final PsiElement rightEnd = getRightParenthesis(element);
        if (rightEnd == null) {
            return;
        }

        PsiElement rightStart = getPrevNonWhitespaceSibling(rightEnd);
        if (rightStart == null) {
            return;
        }
        rightStart = rightStart.getNextSibling();

        final PsiElement leftStart = getLeftParenthesis(element);
        if (leftStart == null) {
            return;
        }

        final PsiElement leftEnd = getNextNonWhitespaceSibling(leftStart);
        if (leftEnd == null) {
            return;
        }

        Document document = editor.getDocument();
        int leftLine = document.getLineNumber(leftStart.getTextOffset());
        int rightLine = document.getLineNumber(rightEnd.getTextOffset());

        final PsiElement finalRightStart = rightStart;
        WriteCommandAction writeCommandAction = new WriteCommandAction(editor.getProject()) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                element.deleteChildRange(finalRightStart, rightEnd);
                element.getNode().removeRange(leftStart.getNode(), leftEnd.getNode());
            }
        };
        writeCommandAction.execute();

        // if parentheses are not in the same line, delete line ending white space and new line
        if (leftLine != rightLine) {
            deleteLineEditingWhiteSpaceAndNewLine(element);
        }
    }

    private void deleteLineEditingWhiteSpaceAndNewLine(PsiElement element) {
        PsiElement space = element;
        if (isNodeOfType(space, GoTokenTypeSets.WHITESPACES)) {
            space.delete();
            space = space.getNextSibling();
        }

        if (space == null || !isNewLineNode(space)) {
            return;
        }

        // if there is only one new line, delete it
        if (space.getTextLength() == 1) {
            space.delete();
            return;
        }

        // if there are multiple new lines, delete only one new line.
        PsiFile file = space.getContainingFile();
        if (!(file instanceof GoFile)) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < space.getTextLength(); i++) {
            sb.append("\n");
        }
        PsiElement[] statements = GoPsiElementFactory.createStatements((GoFile) file, sb.toString());
        if (statements.length > 0 && isNewLineNode(statements[0])) {
            space.replace(statements[0]);
        }
    }

    private PsiElement getNextNonWhitespaceSibling(PsiElement start) {
        while ((start = start.getNextSibling()) != null) {
            if (!isWhiteSpaceNode(start)) {
                break;
            }
        }
        return start;
    }

    private PsiElement getPrevNonWhitespaceSibling(PsiElement start) {
        PsiElement end = start;
        while ((end = end.getPrevSibling()) != null) {
            if (!isWhiteSpaceNode(end)) {
                break;
            }
        }
        return end;
    }

    private PsiElement getLeftParenthesis(PsiElement element) {
        PsiElement start = element.getFirstChild();
        while (start != null) {
            if (start instanceof LeafPsiElement && "(".equals(start.getText())) {
                break;
            }
            start = start.getNextSibling();
        }
        return start;
    }
}
