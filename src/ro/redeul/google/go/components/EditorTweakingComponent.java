package ro.redeul.google.go.components;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.DocumentRunnable;
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.text.CharArrayUtil;
import ro.redeul.google.go.GoFileType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 7, 2010
 * Time: 4:13:53 PM
 */
public class EditorTweakingComponent extends FileDocumentManagerAdapter {

    @Override
    public void beforeDocumentSaving(final Document document) {

        if (!document.isWritable())
            return;

        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if (file == null || file.getFileType() != GoFileType.GO_FILE_TYPE) {
            return;
        }

        final EditorSettingsExternalizable settings = EditorSettingsExternalizable.getInstance();
        if (settings != null && settings.isEnsureNewLineAtEOF()) {
            return;
        }

        final int lines = document.getLineCount();
        if (lines > 0) {
            final int start = document.getLineStartOffset(lines - 1);
            final int end = document.getLineEndOffset(lines - 1);
            if (start != end) {
                ApplicationManager.getApplication().runWriteAction(new DocumentRunnable(document, null) {
                    public void run() {
                        CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
                            public void run() {
                                CharSequence content = document.getCharsSequence();
                                if (CharArrayUtil.containsOnlyWhiteSpaces(content.subSequence(start, end))) {
                                    document.deleteString(start, end);
                                } else {
                                    document.insertString(end, "\n");
                                }
                            }
                        });
                    }
                });
            }
        }
    }
}
