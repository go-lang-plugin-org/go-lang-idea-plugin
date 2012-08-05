package ro.redeul.google.go.ide;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoLanguage;

public class GoLanguageCodeStyleSettingsProvider
    extends LanguageCodeStyleSettingsProvider {

    @NotNull
    @Override
    public Language getLanguage() {
        return GoLanguage.INSTANCE;
    }

    @Override
    public CommonCodeStyleSettings getDefaultCommonSettings() {
        CommonCodeStyleSettings defaultSettings =
            new CommonCodeStyleSettings(GoLanguage.INSTANCE);

        CommonCodeStyleSettings.IndentOptions indentOptions =
            defaultSettings.initIndentOptions();

        indentOptions.USE_TAB_CHARACTER = true;
        indentOptions.INDENT_SIZE = 4;
        indentOptions.TAB_SIZE = 4;
        indentOptions.CONTINUATION_INDENT_SIZE = 8;

        return defaultSettings;
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return "// A concurrent prime sieve\n" +
            "\n" +
            "package main\n" +
            "\n" +
            "// Send the sequence 2, 3, 4, ... to channel 'ch'.\n" +
            "func Generate(ch chan<- int) {\n" +
            "\tfor i := 2; ; i++ {\n" +
            "\t\tch <- i // Send 'i' to channel 'ch'.\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "// Copy the values from channel 'in' to channel 'out',\n" +
            "// removing those divisible by 'prime'.\n" +
            "func Filter(in <-chan int, out chan<- int, prime int) {\n" +
            "\tfor {\n" +
            "\t\ti := <-in // Receive value from 'in'.\n" +
            "\t\tif i%prime != 0 {\n" +
            "\t\t\tout <- i // Send 'i' to 'out'.\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "// The prime sieve: Daisy-chain Filter processes.\n" +
            "func main() {\n" +
            "\tch := make(chan int) // Create a new channel.\n" +
            "\tgo Generate(ch)      // Launch Generate goroutine.\n" +
            "\tfor i := 0; i < 10; i++ {\n" +
            "\t\tprime := <-ch\n" +
            "\t\tprint(prime, \"\\n\")\n" +
            "\t\tch1 := make(chan int)\n" +
            "\t\tgo Filter(ch, ch1, prime)\n" +
            "\t\tch = ch1\n" +
            "\t}\n" +
            "}\n";
    }

    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new IndentOptionsEditor();
    }
}
