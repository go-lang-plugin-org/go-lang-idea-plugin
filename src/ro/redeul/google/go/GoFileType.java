package ro.redeul.google.go;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.highlight.GoEditorHighlighter;

import javax.swing.*;

public class GoFileType extends LanguageFileType {

    public static final GoFileType INSTANCE = new GoFileType();

    @NonNls
    public static final String DEFAULT_EXTENSION = "go";

    private GoFileType() {
        super(GoLanguage.INSTANCE);
    }

    @NotNull
    @NonNls
    public String getName() {
        return "Google Go";
    }

    @NonNls
    @NotNull
    public String getDescription() {
        return "Google Go files";
    }

    @NotNull
    @NonNls
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, byte[] content) {
        return CharsetToolkit.UTF8;
    }

    public EditorHighlighter getEditorHighlighter(@NotNull EditorColorsScheme colors) {
        return new GoEditorHighlighter(colors);
    }
}
