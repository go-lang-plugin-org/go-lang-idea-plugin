package ro.redeul.google.go.components;

import com.intellij.AppTopics;
import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import com.intellij.util.text.CharArrayUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 7, 2010
 * Time: 4:13:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditorTweakingComponent implements ProjectComponent {

    private Project project;
    private MessageBusConnection connection;

    public EditorTweakingComponent(Project project) {
        this.project = project;
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "EditorTweakingComponent";
    }

    public void projectOpened() {

        connection = project.getMessageBus().connect(project);

        connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC, new FileDocumentManagerAdapter() {
            @Override
            public void beforeDocumentSaving(final Document document) {

                VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                if (file == null || file.getFileType() != GoFileType.GO_FILE_TYPE) {
                    return;
                }

                final EditorSettingsExternalizable settings = EditorSettingsExternalizable.getInstance();
                if ( settings != null && settings.isEnsureNewLineAtEOF() ) {
                    return;
                }

                CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
                    public void run() {
                        final int lines = document.getLineCount();
                        if (lines > 0) {
                            int start = document.getLineStartOffset(lines - 1);
                            int end = document.getLineEndOffset(lines - 1);
                            if (start != end) {
                                CharSequence content = document.getCharsSequence();
                                if (CharArrayUtil.containsOnlyWhiteSpaces(content.subSequence(start, end))) {
                                    document.deleteString(start, end);
                                } else {
                                    document.insertString(end, "\n");
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public void projectClosed() {
        connection.disconnect();
    }
}
