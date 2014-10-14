package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.fix.ChangePackageNameFix;
import ro.redeul.google.go.inspection.fix.RepackageFileFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

public class InvalidPackageNameInspection
    extends AbstractWholeGoFileInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Invalid package name";
    }

    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull InspectionResult result) {

        GoPackageDeclaration packageDeclaration = file.getPackage();

        if (packageDeclaration.isMainPackage())
            return;

        String packageName = packageDeclaration.getPackageName();

        VirtualFile virtualFile = file.getVirtualFile();

        if (virtualFile == null)
            return;

        ProjectFileIndex projectFileIndex =
            ProjectRootManager.getInstance(file.getProject()).getFileIndex();

        VirtualFile srcRoot =
            projectFileIndex.getSourceRootForFile(virtualFile);

        if (srcRoot != null && srcRoot.equals(virtualFile.getParent())) {
            result.addProblem(packageDeclaration,
                              "File should be inside a folder named '" + packageName + "'",
                              ProblemHighlightType.GENERIC_ERROR,
                              new RepackageFileFix(srcRoot, packageName));
            return;
        }

        String targetPackageName = virtualFile.getParent().getName();
        // We are only interested in the package name without any "."
        targetPackageName = GoPsiUtils.findRealImportPathValue(targetPackageName);

        if (virtualFile.getNameWithoutExtension().endsWith("_test") ) {
            packageName = packageName.replaceAll("_test$", "");

            if (!targetPackageName.equals(packageName)) {
                result.addProblem(packageDeclaration,
                                  GoBundle.message("error.invalid.package.name.with.test", targetPackageName),
                                  ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                  new ChangePackageNameFix(packageDeclaration, targetPackageName),
                                  new ChangePackageNameFix(packageDeclaration, targetPackageName + "_test")
                );
            }
            return;
        }

        if (!targetPackageName.equals(packageName)) {
            result.addProblem(packageDeclaration,
                              GoBundle.message("error.invalid.package.name", targetPackageName),
                              ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                              new ChangePackageNameFix(packageDeclaration, targetPackageName)
                              );
        }
    }
}
