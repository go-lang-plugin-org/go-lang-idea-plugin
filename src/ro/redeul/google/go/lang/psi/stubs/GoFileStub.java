package ro.redeul.google.go.lang.psi.stubs;

import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.util.io.StringRef;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 13, 2010
 * Time: 10:25:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoFileStub extends PsiFileStub<GoFile> {

    StringRef getPackageName();

    boolean isMain();

}
