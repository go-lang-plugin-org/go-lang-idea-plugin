package ro.redeul.google.go.intentions.parenthesis;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElementFactory;

import static ro.redeul.google.go.intentions.parenthesis.ParenthesisUtil.getRightParenthesis;
import static ro.redeul.google.go.intentions.parenthesis.ParenthesisUtil.hasOnlyOneDeclaration;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class RemoveDeclarationParenthesesIntention extends Intention {
    @Override
    protected boolean satisfiedBy(PsiElement element) {
        return getRightParenthesis(element) != null && hasOnlyOneDeclaration(element);
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Project project, Editor editor)
            throws IncorrectOperationException {

        PsiElement rightEnd = getRightParenthesis(element);
        if (rightEnd == null) {
            return;
        }

        PsiElement rightStart = getPrevNonWhitespaceSibling(rightEnd);
        if (rightStart == null) {
            return;
        }
        rightStart = rightStart.getNextSibling();

        PsiElement leftStart = getLeftParenthesis(element);
        if (leftStart == null) {
            return;
        }

        PsiElement leftEnd = getNextNonWhitespaceSibling(leftStart);
        if (leftEnd == null) {
            return;
        }
        leftEnd = leftEnd.getPrevSibling();

        Document document = editor.getDocument();
        int leftLine = document.getLineNumber(leftStart.getTextOffset());
        int rightLine = document.getLineNumber(rightEnd.getTextOffset());

        element.deleteChildRange(rightStart, rightEnd);
        element.deleteChildRange(leftStart, leftEnd);

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

        if (space == null || !isNodeOfType(space, GoElementTypes.wsNLS)) {
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
        if (statements.length > 0 && isNodeOfType(statements[0], GoElementTypes.wsNLS)) {
            space.replace(statements[0]);
        }
    }

    private PsiElement getNextNonWhitespaceSibling(PsiElement start) {
        PsiElement end = start;
        while ((end = end.getNextSibling()) != null) {
            if (!isNodeOfType(end, GoTokenTypeSets.WHITESPACES) && !isNodeOfType(end, GoElementTypes.wsNLS)) {
                break;
            }
        }
        return end;
    }

    private PsiElement getPrevNonWhitespaceSibling(PsiElement start) {
        PsiElement end = start;
        while ((end = end.getPrevSibling()) != null) {
            if (!isNodeOfType(end, GoTokenTypeSets.WHITESPACES) && !isNodeOfType(end, GoElementTypes.wsNLS)) {
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
