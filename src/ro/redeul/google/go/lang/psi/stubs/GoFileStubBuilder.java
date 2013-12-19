package ro.redeul.google.go.lang.psi.stubs;

import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 13, 2010
 * Time: 11:27:40 PM
 */
public class GoFileStubBuilder extends DefaultStubBuilder {

    @Override
    protected StubElement createStubForFile(@NotNull final PsiFile file) {

        if (file instanceof GoFile) {
            return new GoFileStub((GoFile) file);
        }

        return super.createStubForFile(file);
    }
}
