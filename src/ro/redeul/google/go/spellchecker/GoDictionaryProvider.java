package ro.redeul.google.go.spellchecker;

import com.intellij.spellchecker.BundledDictionaryProvider;

public class GoDictionaryProvider implements BundledDictionaryProvider {
    @Override
    public String[] getBundledDictionaries() {
        // Data of this file is extracted from all exported names of official libraries in go 1.0.1.
        // Add this file as bundled dictionary, so that users will never see spell error report on standard libraries.
        return new String[] {"/goSpell.dic"};
    }
}
