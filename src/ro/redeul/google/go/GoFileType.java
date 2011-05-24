package ro.redeul.google.go;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.highlight.GoEditorHighlighter;

import javax.swing.*;
import java.nio.charset.Charset;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 2:37:45 AM
 */
public class GoFileType extends LanguageFileType {

    public static final GoFileType GO_FILE_TYPE = new GoFileType();

    public static final Language GO_LANGUAGE = GO_FILE_TYPE.getLanguage();
    @NonNls
    public static final String DEFAULT_EXTENSION = "go";

    private GoFileType() {
        super(new GoLanguage());
    }

    @NotNull
    @NonNls
    public String getName() {
        return "Google Go";
    }

    @NonNls
    @NotNull
    public String getDescription() {
        return "Google go Files";
    }

    @NotNull
    @NonNls
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    public boolean isJVMDebuggingSupported() {
        return false;
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, byte[] content) {
        return CharsetToolkit.UTF8;
    }

    public EditorHighlighter getEditorHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme colors) {
        return new GoEditorHighlighter(colors, project, virtualFile);
    }


}
