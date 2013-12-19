package ro.redeul.google.go.lang.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/23/11
 * Time: 5:19 PM
 */
public class GoQualifiedTypeName extends StringStubIndexExtension<GoTypeNameDeclaration> {

    public static final StubIndexKey<String, GoTypeNameDeclaration> KEY = StubIndexKey.createIndexKey("go.package.type.names.qualified");

    @NotNull
    @Override
    public StubIndexKey<String, GoTypeNameDeclaration> getKey() {
        return KEY;
    }
}