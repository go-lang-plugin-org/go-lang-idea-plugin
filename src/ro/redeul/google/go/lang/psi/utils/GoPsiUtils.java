package ro.redeul.google.go.lang.psi.utils;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ReflectionCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoBuiltinCallExpr;
import ro.redeul.google.go.lang.psi.expressions.GoCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.util.ArrayList;
import java.util.List;

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
            if ( child.getFileType() != GoFileType.INSTANCE ||
                child.getNameWithoutExtension().endsWith("_test") ) {
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
        if (node == null) {
            return false;
        }

        ASTNode astNode = node.getNode();
        return astNode != null && astNode.getElementType() == type;
    }

    public static PsiElement findParentOfType(PsiElement node, IElementType type) {
        while (node != null) {
            node = node.getParent();
            if (isNodeOfType(node, type)) {
                return node;
            }
        }
        return null;
    }

    public static <T extends PsiElement> T findParentOfType(PsiElement node, Class<? extends T> type) {
        while (node != null && !ReflectionCache.isInstance(node, type)) {
            node = node.getParent();
        }

        return (T) node;
    }

    public static boolean isWhiteSpaceNode(PsiElement node) {
        return isNodeOfType(node, GoElementTypes.wsWS) || isNodeOfType(node, GoElementTypes.wsNLS);
    }

    public static PsiElement findChildOfType(PsiElement node, IElementType type) {
        if (node == null) {
            return null;
        }

        for (PsiElement element : node.getChildren()) {
            if (isNodeOfType(element, type)) {
                return element;
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
        return element instanceof GoLiteralIdentifier && ((GoLiteralIdentifier)element).isIota();
    }

    public static boolean isEnclosedByParenthesis(PsiElement element) {
        if (element == null) {
            return false;
        }

        PsiElement parent = element.getParent();
        if (parent == null) {
            return false;
        }

        PsiElement lastChild = parent.getLastChild();
        return lastChild != null && ")".equals(lastChild.getText());
    }

    public static PsiElement getPrevSiblingIfItsWhiteSpaceOrComment(PsiElement element) {
        while (element != null) {
            ASTNode node = element.getNode();
            if (node == null) {
                return null;
            }

            IElementType type = node.getElementType();
            if (type != GoElementTypes.wsNLS && type != GoElementTypes.wsWS && !(element instanceof PsiWhiteSpace) &&
                !(element instanceof PsiComment)) {
                return element;
            }

            element = element.getPrevSibling();
        }
        return null;
    }

    @NotNull
    public static GoFunctionParameter[] getParameters(PsiElement psiNode) {

        if (psiNode == null)
            return GoFunctionParameter.EMPTY_ARRAY;

        ASTNode list =
            psiNode.getNode().findChildByType(GoElementTypes.FUNCTION_PARAMETER_LIST);

        if (list == null)
            return GoFunctionParameter.EMPTY_ARRAY;

        GoFunctionParameter params[] =
            PsiTreeUtil.getChildrenOfType(list.getPsi(), GoFunctionParameter.class);

        return params != null ? params : GoFunctionParameter.EMPTY_ARRAY;
    }

    public static boolean isFunctionOrMethodCall(PsiElement element) {
        return element instanceof GoBuiltinCallExpr || element instanceof GoCallOrConversionExpression;
    }
}
