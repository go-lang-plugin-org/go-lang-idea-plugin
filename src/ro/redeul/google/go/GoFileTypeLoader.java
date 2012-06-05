package ro.redeul.google.go;

import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class GoFileTypeLoader extends FileTypeFactory {

    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(
                GoFileType.INSTANCE,
                new ExtensionFileNameMatcher(GoFileType.DEFAULT_EXTENSION));
    }

}
