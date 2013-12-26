package ro.redeul.google.go.intentions;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Intention implements IntentionAction {

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return findMatchingElement(file, editor) != null;
    }

    @Nullable
    public PsiElement findMatchingElement(PsiFile file, Editor editor) {
        final int position = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(position);
        while (element != null) {
            if (satisfiedBy(element)) return element;
            if (isStopElement(element)) return null;
            element = element.getParent();
        }
        return null;
    }

    protected abstract boolean satisfiedBy(PsiElement element);

    boolean isStopElement(PsiElement element) {
        return element instanceof PsiFile;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        final VirtualFile virtualFile = file.getVirtualFile();
        final ReadonlyStatusHandler readonlyStatusHandler = ReadonlyStatusHandler.getInstance(project);
        final ReadonlyStatusHandler.OperationStatus operationStatus = readonlyStatusHandler.ensureFilesWritable(virtualFile);
        if (operationStatus.hasReadonlyFiles()) {
            return;
        }

        PsiElement element = findMatchingElement(file, editor);
        if (element == null || !element.isValid()) {
            return;
        }

        try {
            processIntention(element, editor);
        } catch (IntentionExecutionException e) {
            HintManager hintManager = HintManager.getInstance();
            if (e.getStartOffset() >= 0 && e.getLength() > 0) {
                int start = element.getTextOffset() + e.getStartOffset();
                int end = start + e.getLength();
                editor.getCaretModel().moveToOffset(end);
                editor.getSelectionModel().setSelection(start, end);
            }
            hintManager.showErrorHint(editor, e.getMessage());
        }
    }

    protected abstract void processIntention(@NotNull PsiElement element, Editor editor)
            throws IntentionExecutionException;

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    private String getPrefix() {
        final Class<? extends Intention> aClass = getClass();
        final String name = aClass.getSimpleName();
        final StringBuilder buffer = new StringBuilder(name.length() + 10);
        buffer.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            final char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                buffer.append('.');
                buffer.append(Character.toLowerCase(c));
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    @NotNull
    @Override
    public String getText() {
        return GoIntentionsBundle.message(getPrefix() + ".name");
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return GoIntentionsBundle.message(getPrefix() + ".family.name");
    }
}
