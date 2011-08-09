package ro.redeul.google.go.lang.psi.stubs;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import ro.redeul.google.go.lang.parser.GoParserDefinition;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 13, 2010
 * Time: 10:25:59 PM
 */
public class GoFileStub extends PsiFileStubImpl<GoFile> {

    private final StringRef packageName;
    private final boolean isMain;
    private final StringRef packageImportPath;

    public GoFileStub(GoFile file) {
        super(file);

        packageImportPath = StringRef.fromString(file.getPackageImportPath());
        packageName = StringRef.fromString(file.getPackage().getPackageName());
        isMain = file.getMainFunction() != null;
    }

    public GoFileStub(StringRef packageImportPath, StringRef packageName, boolean isMain) {
        super(null);

        this.packageImportPath = packageImportPath;
        this.packageName = packageName;
        this.isMain = isMain;
    }

    public IStubFileElementType getType() {
        return GoParserDefinition.GO_FILE_TYPE;
    }

    public StringRef getPackageImportPath() {
        return packageImportPath;
    }

    public StringRef getPackageName() {
        return packageName;
    }

    public boolean isMain() {
        return isMain;
    }

}
