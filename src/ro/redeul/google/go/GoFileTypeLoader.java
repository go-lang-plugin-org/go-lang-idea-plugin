package ro.redeul.google.go;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 2:45:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoFileTypeLoader extends FileTypeFactory {

    public static final List<FileType> GO_FILE_TYPES = new ArrayList<FileType>();

    public static FileType[] getGroovyEnabledFileTypes() {
        return GO_FILE_TYPES.toArray(new FileType[GO_FILE_TYPES.size()]);
    }

    public static Set<String> getCustomGroovyScriptExtensions() {
        final LinkedHashSet<String> strings = new LinkedHashSet<String>();
//        strings.add("gdsl");
//        for (GroovyScriptTypeEP ep : GroovyScriptType.EP_NAME.getExtensions()) {
//            strings.addAll(Arrays.asList(ep.extensions.split(";")));
//        }
        return strings;
    }

    public static List<String> getAllGroovyExtensions() {
        final ArrayList<String> strings = new ArrayList<String>();
        strings.add(GoFileType.DEFAULT_EXTENSION);
        strings.addAll(getCustomGroovyScriptExtensions());
        return strings;
    }

    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(GoFileType.GO_FILE_TYPE, StringUtil.join(getAllGroovyExtensions(), ";"));
        GO_FILE_TYPES.add(GoFileType.GO_FILE_TYPE);
    }
}
