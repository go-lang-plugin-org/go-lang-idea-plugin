package ro.redeul.google.go;

import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 2:45:10 AM
 */
public class GoFileTypeLoader extends FileTypeFactory {

    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(
                GoFileType.GO_FILE_TYPE,
                new ExtensionFileNameMatcher(GoFileType.DEFAULT_EXTENSION));
    }

}
