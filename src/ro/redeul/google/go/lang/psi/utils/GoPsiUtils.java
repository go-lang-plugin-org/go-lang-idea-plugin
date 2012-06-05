package ro.redeul.google.go.lang.psi.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.sdk.GoSdkUtil;

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

        String defaultPackageName = findDefaultPackageName(importPath);

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

    public static String findDefaultPackageName(String importPath) {
        return importPath != null ? importPath.replaceAll("(?:[a-zA-Z\\.]+/)+", "") : null;
    }

    public static boolean isNodeOfType(PsiElement node, TokenSet tokenSet) {
        return tokenSet.contains(node.getNode().getElementType());
    }

    public static boolean isNodeOfType(PsiElement node, IElementType type) {
        return node.getNode().getElementType() == type;
    }

    public static PsiElement findParentOfType(PsiElement node, IElementType type) {
        while (node != null) {
            node = node.getParent();
            if (node != null && node.getNode() != null && node.getNode().getElementType() == type) {
                return node;
            }
        }
        return null;
    }

    /**
     * Check whether element is the predeclared identifier "iota" in a const declaration
     * @param element the element to check
     * @return true if the element is a valid use of "iota" in const declaration
     */
    public static boolean isIotaInConstantDeclaration(PsiElement element) {
        PsiElement expressionList = findParentOfType(element, GoElementTypes.EXPRESSION_LIST);
        PsiElement constDeclaration = findParentOfType(expressionList, GoElementTypes.CONST_DECLARATION);
        return constDeclaration != null;
    }
}
