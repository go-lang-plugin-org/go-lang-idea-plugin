package ro.redeul.google.go.lang.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 29, 2010
 * Time: 2:51:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoPackageName extends StringStubIndexExtension<GoFile> {

    public static final StubIndexKey<String, GoFile> KEY = StubIndexKey.createIndexKey("go.package.names");

    public StubIndexKey<String, GoFile> getKey() {
      return KEY;
    }
}
