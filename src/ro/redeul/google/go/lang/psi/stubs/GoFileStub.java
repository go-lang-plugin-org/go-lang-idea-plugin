package ro.redeul.google.go.lang.psi.stubs;

import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.util.io.StringRef;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 13, 2010
 * Time: 10:25:59 PM
 */
public interface GoFileStub extends PsiFileStub<GoFile> {

    StringRef getPackageName();

    boolean isMain();

}
