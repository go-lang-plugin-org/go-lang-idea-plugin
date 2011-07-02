package ro.redeul.google.go;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 2:45:10 AM
 */
public class GoFileTypeLoader extends FileTypeFactory {

    public static final List<FileType> GO_FILE_TYPES = new ArrayList<FileType>();

    public static FileType[] getGoEnabledFileTypes() {
        return GO_FILE_TYPES.toArray(new FileType[GO_FILE_TYPES.size()]);
    }

    public static List<String> getAllGoExtensions() {
        final ArrayList<String> strings = new ArrayList<String>();
        strings.add(GoFileType.DEFAULT_EXTENSION);
        return strings;
    }

    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(GoFileType.GO_FILE_TYPE, StringUtil.join(getAllGoExtensions(), ";"));
        GO_FILE_TYPES.add(GoFileType.GO_FILE_TYPE);
    }

}
