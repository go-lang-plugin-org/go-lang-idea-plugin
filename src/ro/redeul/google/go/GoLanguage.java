package ro.redeul.google.go;

import com.intellij.lang.Language;

public class GoLanguage extends Language {
    public static final Language INSTANCE = new GoLanguage();

    private GoLanguage() {
        super("Google Go", "text/go", "text/x-go", "application/x-go");
    }

    @Override
    public String getDisplayName() {
        return "Google Go Lang";
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }
}
