package ro.redeul.google.go.lang.psi.stubs.impl;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import ro.redeul.google.go.lang.parser.GoParserDefinition;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.stubs.GoFileStub;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 13, 2010
 * Time: 10:25:59 PM
 */
public class GoFileStubImpl extends PsiFileStubImpl<GoFile> implements GoFileStub {

    private final StringRef myPackageName;
    private final boolean isMain;

    public GoFileStubImpl(GoFile file) {
        super(file);

        myPackageName = StringRef.fromString(file.getPackage().getPackageName());
        isMain = file.getMainFunction() != null;
    }

    public GoFileStubImpl(StringRef packageName, boolean isMain) {
        super(null);

        myPackageName = packageName;
        this.isMain = isMain;
    }

    public IStubFileElementType getType() {
      return GoParserDefinition.GO_FILE_TYPE;
    }

    public StringRef getPackageName() {
        return myPackageName;
    }

    public boolean isMain() {
        return isMain;
    }

}
