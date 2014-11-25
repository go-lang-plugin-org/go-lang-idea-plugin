package ro.redeul.google.go.lang.psi;

import com.intellij.psi.PsiDirectoryContainer;
import org.jetbrains.annotations.NotNull;

public interface GoPackage extends GoPsiElement, PsiDirectoryContainer {

    public String getImportPath();

    @NotNull
    public String getName();

    GoFile[] getFiles();

    boolean isTestPackage();
}
