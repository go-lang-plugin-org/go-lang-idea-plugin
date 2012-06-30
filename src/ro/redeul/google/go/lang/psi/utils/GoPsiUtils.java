package ro.redeul.google.go.lang.psi.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ReflectionCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.GoNamesUtil;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.sdk.GoSdkUtil;

public class GoPsiUtils {

    public static String getStringLiteralValue(String literalText) {
        if (literalText == null ){
            return "";
        }

        if (literalText.startsWith("`")) {
            return literalText.replaceAll("(?:^`)|(?:`$)", "").replaceAll("\\\\`","`");
        }

        // TODO: implemented proper rune translation to value here.
        return literalText.replaceAll("(?:^\")|(?:\"$)", "");
    }

    public static GoFile[] findFilesForPackage(String importPath,
                                               GoFile importingFile) {

        Project project = importingFile.getProject();

        String defaultPackageName = findDefaultPackageName(importPath);

        VirtualFile packageFilesLocation = null;
        if (importPath.startsWith("./")) { // local import path
            packageFilesLocation = VfsUtil.findRelativeFile(
                importPath.replaceAll("./", ""),
                importingFile.getVirtualFile());
        } else {
            Sdk sdk = GoSdkUtil.getGoogleGoSdkForFile(importingFile);

            VirtualFile sdkSourceRoots[] = sdk.getRootProvider()
                                              .getFiles(OrderRootType.SOURCES);

            for (VirtualFile sdkSourceRoot : sdkSourceRoots) {
                VirtualFile packageFile = VfsUtil.findRelativeFile(importPath,
                                                                   sdkSourceRoot);

                if (packageFile != null) {
                    packageFilesLocation = packageFile;
                    break;
                }
            }
        }

        if (packageFilesLocation == null) {
            return GoFile.EMPTY_ARRAY;
        }

        VirtualFile[] children = packageFilesLocation.getChildren();

        List<GoFile> files = new ArrayList<GoFile>();

        for (VirtualFile child : children) {
            if (child.getFileType() != GoFileType.INSTANCE ||
                child.getNameWithoutExtension().endsWith("_test")) {
                continue;
            }

            GoFile packageGoFile = (GoFile) PsiManager.getInstance(project)
                                                      .findFile(child);
            assert packageGoFile != null;

            if (packageGoFile.getPackage()
                             .getPackageName()
                             .equals(defaultPackageName)) {
                files.add(packageGoFile);
            }
        }

        return files.toArray(new GoFile[files.size()]);
    }

    public static String findDefaultPackageName(String importPath) {
        return importPath != null ? importPath.replaceAll("(?:[a-zA-Z\\.]+/)+",
                                                          "") : null;
    }

    public static boolean isNodeOfType(PsiElement node, TokenSet tokenSet) {
        if (node == null) {
            return false;
        }

        ASTNode astNode = node.getNode();
        return astNode != null && tokenSet.contains(astNode.getElementType());
    }

    public static boolean isNodeOfType(PsiElement node, IElementType type) {
        if (node == null) {
            return false;
        }

        ASTNode astNode = node.getNode();
        return astNode != null && astNode.getElementType() == type;
    }

    public static PsiElement findParentOfType(PsiElement node,
                                              IElementType type) {
        while (node != null) {
            node = node.getParent();
            if (isNodeOfType(node, type)) {
                return node;
            }
        }
        return null;
    }

    public static <T extends PsiElement> T findParentOfType(
        @Nullable PsiElement node,
        Class<? extends T> type) {
        while (node != null && !ReflectionCache.isInstance(node, type)) {
            node = node.getParent();
        }

        return type.cast(node);
    }

    public static boolean isWhiteSpaceOrComment(PsiElement node) {
        return isNodeOfType(node, GoTokenSets.WHITESPACE_OR_COMMENTS);
    }

    public static boolean isWhiteSpaceNode(PsiElement node) {
        return isNodeOfType(node, GoTokenSets.WHITESPACE);
    }

    public static PsiElement findChildOfType(PsiElement node,
                                             IElementType type) {
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

    public static <T extends PsiElement> List<T> findChildrenOfType(
        @Nullable PsiElement node,
        Class<? extends T> type) {
        if (node == null) {
            return Collections.emptyList();
        }

        List<T> children = new ArrayList<T>();
        for (PsiElement element : node.getChildren()) {
            if (ReflectionCache.isInstance(element, type)) {
                children.add(type.cast(element));
            }
        }

        return children;
    }

    public static List<PsiElement> findChildrenOfType(@Nullable PsiElement node,
                                                      IElementType type) {
        if (node == null) {
            return Collections.emptyList();
        }

        List<PsiElement> children = new ArrayList<PsiElement>();
        PsiElement child = node.getFirstChild();
        while (child != null) {
            if (isNodeOfType(child, type)) {
                children.add(child);
            }
            child = child.getNextSibling();
        }

        return children;
    }

    /**
     * Check whether element is the predeclared identifier "iota" in a const declaration
     *
     * @param element the element to check
     * @return true if the element is a valid use of "iota" in const declaration
     */
    public static boolean isIotaInConstantDeclaration(PsiElement element) {
        return element instanceof GoLiteralIdentifier && ((GoLiteralIdentifier) element)
            .isIota();
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

    public static PsiElement getPrevSiblingIfItsWhiteSpaceOrComment(
        PsiElement element) {
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

    public static PsiElement getNextSiblingIfItsWhiteSpaceOrComment(PsiElement element) {
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

            element = element.getNextSibling();
        }
        return null;
    }

    @NotNull
    public static GoFunctionParameter[] getParameters(PsiElement psiNode) {

        if (psiNode == null)
            return GoFunctionParameter.EMPTY_ARRAY;

        ASTNode list =
            psiNode.getNode()
                   .findChildByType(GoElementTypes.FUNCTION_PARAMETER_LIST);

        if (list == null)
            return GoFunctionParameter.EMPTY_ARRAY;

        GoFunctionParameter params[] =
            PsiTreeUtil.getChildrenOfType(list.getPsi(),
                                          GoFunctionParameter.class);

        return params != null ? params : GoFunctionParameter.EMPTY_ARRAY;
    }

    public static boolean isFunctionOrMethodCall(PsiElement element) {
        return element instanceof GoBuiltinCallExpression || element instanceof GoCallOrConvExpression;
    }

    @SuppressWarnings("unchecked")
    public static <Psi extends PsiElement> Psi findChildOfClass(PsiElement node,
                                                                Class<Psi> type) {

        for (PsiElement cur = node.getFirstChild(); cur != null;
             cur = cur.getNextSibling()) {
            if (ReflectionCache.isInstance(cur, type)) return (Psi) cur;
        }
        return null;
    }

    public static boolean psiIsA(PsiElement node,
                                 Class<? extends GoPsiElement> psiType) {
        return ReflectionCache.isInstance(node, psiType);
    }

    public static boolean hasParentAs(PsiElement node, TokenSet...tokens) {
        if (node == null)
            return false;

        node = node.getParent();
        int parent = 0;
        while ( node != null && parent < tokens.length ) {
            if (! tokens[parent].contains(node.getNode().getElementType()))
                return false;

            node = node.getParent();
            parent++;
        }

        return node != null && parent < tokens.length;
    }

    public static boolean hasPrevSiblingOfType(PsiElement node,
                                               IElementType type) {
        if (node == null)
            return false;

        do {
            node = node.getPrevSibling();
        } while (node != null && isWhiteSpaceOrComment(node));

        return node != null && node.getNode().getElementType() == type;
    }

    public static SearchScope getLocalElementSearchScope(GoPsiElement element) {
        GoStatement statement = findParentOfType(element, GoStatement.class);
        if (statement == null) {
            return new LocalSearchScope(element);
        }

        return new LocalSearchScope(statement.getParent());
    }

    public static SearchScope getGlobalElementSearchScope(GoPsiElement element, String name) {
        if (GoNamesUtil.isExportedName(name)) {
            return GlobalSearchScope.projectScope(element.getProject());
        }

        return getPackageSearchScope(element);
    }

    public static SearchScope getPackageSearchScope(GoPsiElement element) {
        GoNamesCache namesCache = GoNamesCache.getInstance(element.getProject());
        String packageName = ((GoFile) element.getContainingFile()).getPackageName();
        Collection<GoFile> files = namesCache.getFilesByPackageName(packageName);
        return new LocalSearchScope(files.toArray(new PsiElement[files.size()]));
    }
}
