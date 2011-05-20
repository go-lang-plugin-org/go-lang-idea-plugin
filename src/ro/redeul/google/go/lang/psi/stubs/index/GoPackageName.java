package ro.redeul.google.go.lang.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 29, 2010
 * Time: 2:51:52 PM
 */
public class GoPackageName extends StringStubIndexExtension<GoFile> {

    public static final StubIndexKey<String, GoFile> KEY = StubIndexKey.createIndexKey("go.package.names");

    public StubIndexKey<String, GoFile> getKey() {
        return KEY;
    }
}
