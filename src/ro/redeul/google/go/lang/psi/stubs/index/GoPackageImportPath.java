package ro.redeul.google.go.lang.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 8/9/11
 * Time: 3:27 PM
 */
public class GoPackageImportPath extends StringStubIndexExtension<GoFile> {

    public static final StubIndexKey<String, GoFile> KEY = StubIndexKey.createIndexKey("go.package.import.paths");

    @NotNull
    public StubIndexKey<String, GoFile> getKey() {
        return KEY;
    }

}
