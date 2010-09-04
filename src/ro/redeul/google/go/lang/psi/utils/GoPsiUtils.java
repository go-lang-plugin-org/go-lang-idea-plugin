package ro.redeul.google.go.lang.psi.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.ResolveState;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.util.GoSdkUtil;

import javax.swing.text.html.ListView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 4, 2010
 * Time: 10:14:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoPsiUtils {

    public static String cleanupImportPath(String path) {
        return path.replaceAll("^\"", "").replaceAll("\"$", "");
    }

    public static GoFile[] findFilesForPackage(String importPath, GoFile importingFile) {

        Project project = importingFile.getProject();

        String defaultPackageName = importPath.replaceAll("(?:[a-zA-Z\\.]+/)+", "");

        VirtualFile packageFilesLocation = null;
        if ( importPath.startsWith("./") ) { // local import path
            packageFilesLocation = VfsUtil.findRelativeFile(importPath.replaceAll("./", ""), importingFile.getVirtualFile());
        } else {
            Sdk sdk = GoSdkUtil.getGoogleGoSdkForFile(importingFile);

            VirtualFile sdkSourceRoots[] = sdk.getRootProvider().getFiles(OrderRootType.SOURCES);

            for (VirtualFile sdkSourceRoot : sdkSourceRoots) {
                VirtualFile packageFile = VfsUtil.findRelativeFile(importPath, sdkSourceRoot);

                if ( packageFile != null ) {
                    packageFilesLocation = packageFile;
                    break;
                }
            }
        }

        if ( packageFilesLocation == null ) {
            return GoFile.EMPTY_ARRAY;
        }

        VirtualFile []children = packageFilesLocation.getChildren();

        List<GoFile> files = new ArrayList<GoFile>();

        for (VirtualFile child : children) {
            if ( child.getFileType() != GoFileType.GO_FILE_TYPE || child.getNameWithoutExtension().endsWith("_test") ) {
                continue;
            }

            GoFile packageGoFile = (GoFile) PsiManager.getInstance(project).findFile(child);
            assert packageGoFile != null;

            if ( packageGoFile.getPackage().getPackageName().equals(defaultPackageName)) {
                files.add(packageGoFile);
            }
        }

        return files.toArray(new GoFile[files.size()]);
    }
}
