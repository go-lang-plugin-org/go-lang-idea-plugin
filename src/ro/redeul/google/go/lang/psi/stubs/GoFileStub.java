package ro.redeul.google.go.lang.psi.stubs;

import com.intellij.psi.stubs.PsiFileStubImpl;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 13, 2010
 * Time: 10:25:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoFileStub extends PsiFileStubImpl<GoFile> {
    
    public GoFileStub(GoFile file) {
        super(file);
    }

    public GoFileStub() {
        super(null);
    }
}
