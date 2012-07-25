package ro.redeul.google.go.components;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;

public class GoFileTypeRegistrationValidator implements ApplicationComponent {

    @Override
    public void initComponent() {
        final FileTypeManager ftManager = FileTypeManager.getInstance();

        final FileType fileType =
            ftManager.getFileTypeByExtension(GoFileType.DEFAULT_EXTENSION);

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                ftManager.associateExtension(GoFileType.INSTANCE,
                                                   GoFileType.DEFAULT_EXTENSION);
            }
        });
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "GoFileTypeRegistrationValidator";
    }
}
