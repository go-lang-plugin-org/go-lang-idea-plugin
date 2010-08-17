package ro.redeul.google.go.lang.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 7:56:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoFileImpl extends PsiFileBase implements GoFile {

    public GoFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, GoFileType.GO_LANGUAGE);
    }

    @NotNull
    public FileType getFileType() {
        return GoFileType.GO_FILE_TYPE;
    }

    @Override
    public String toString() {
        return "Go file";
    }
}
