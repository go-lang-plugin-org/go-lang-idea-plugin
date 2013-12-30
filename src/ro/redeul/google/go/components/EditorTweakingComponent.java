package ro.redeul.google.go.components;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.DocumentRunnable;
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Sep 7, 2010
 */
public class EditorTweakingComponent extends FileDocumentManagerAdapter {

  @Override
  public void beforeDocumentSaving(@NotNull final Document document) {

    if (!document.isWritable())
      return;

    VirtualFile file = FileDocumentManager.getInstance().getFile(document);
    if (file == null || file.getFileType() != GoFileType.INSTANCE) {
      return;
    }

    CharSequence text = document.getCharsSequence();

    int start = text.length();
    while (start > 0 && isWhiteSpace(text.charAt(start - 1)))
      start--;

    if (start >= 0) {
      final int from = start;
      final int to = text.length();

      ApplicationManager.getApplication().runWriteAction(
        new DocumentRunnable(document, null) {
          public void run() {
            CommandProcessor.getInstance().runUndoTransparentAction(
              new Runnable() {
                public void run() {
                  document.replaceString(from, to, "\n");
                }
              });
          }
        });
    }
  }

  private boolean isWhiteSpace(char ch) {
    return Character.isWhitespace(ch);
  }
}
