package ro.redeul.google.go.lang.psi.stubs;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import ro.redeul.google.go.lang.parser.GoParserDefinition;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 13, 2010
 * Time: 10:25:59 PM
 */
public class GoFileStub extends PsiFileStubImpl<GoFile> {

    private final StringRef packageName;
    private final boolean isMain;

    public GoFileStub(GoFile file) {
        super(file);

        packageName = StringRef.fromString(file.getPackage().getPackageName());
        isMain = file.getMainFunction() != null;
    }

    public GoFileStub(StringRef packageName, boolean isMain) {
        super(null);

        this.packageName = packageName;
        this.isMain = isMain;
    }

    public IStubFileElementType getType() {
        return GoParserDefinition.GO_FILE_TYPE;
    }

    public StringRef getPackageName() {
        return packageName;
    }

    public boolean isMain() {
        return isMain;
    }

}
