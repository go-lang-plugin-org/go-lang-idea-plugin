package ro.redeul.google.go.lang.psi.utils;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.*;
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
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.processors.GoNamesUtil;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class GoPsiUtils {

    public static <Psi extends PsiElement> Psi childAt(int i, Psi[] array) {
        if (array == null || array.length <= i)
            return null;
        return array[i];
    }

    public static <B extends PsiElement, D extends B> D castAs(Class<D> type,
                                                               int i, B[] array) {
        if (array == null || array.length <= i)
            return null;

        return !type.isInstance(array[i]) ? null : type.cast(array[i]);
    }

    public static <Psi extends PsiElement> Psi get(Psi node) {
        return node;
    }

    public static <
        B extends PsiElement,
        D extends B> D getAs(Class<D> type, B node) {
        if ( node == null || !type.isInstance(node) )
            return null;

        return type.cast(node);
    }


    public static <T extends PsiElement> T resolveSafely(PsiElement element, Class<T> expectedType) {
        if (element == null) {
            return null;
        }

        PsiReference []references = element.getReferences();

        for (PsiReference reference : references) {
            if (reference == null) {
                return null;
            }

            PsiElement resolved = reference.resolve();
            if (resolved != null && expectedType.isAssignableFrom(resolved.getClass()))
                return expectedType.cast(resolved);
        }

        return null;
    }

    public static <T extends PsiElement> T resolveSafely(PsiElement element,
                                                         ElementPattern pattern,
                                                         Class<T> expectedType)
    {
        PsiReference []references = element.getReferences();

        for (PsiReference reference : references) {
            PsiElement resolved = reference.resolve();
            if (resolved != null && pattern.accepts(resolved) &&
                expectedType.isAssignableFrom(resolved.getClass()))
                return expectedType.cast(resolved);
        }

        return null;
    }

    @Nullable
    public static GoTypeSpec resolveTypeSpec(@Nullable GoPsiTypeName type) {
        if (type == null) {
            return null;
        }

        GoTypeNameDeclaration nameDeclaration = resolveSafely(type, GoTypeNameDeclaration.class);
        return nameDeclaration != null ? nameDeclaration.getTypeSpec() : null;
    }

    public static boolean hasHardReferences(PsiElement element) {

        PsiReference []references = element.getReferences();

        for (PsiReference reference : references) {
            if ( ! reference.isSoft())
                return true;
        }

        return false;
    }


    public static String getStringLiteralValue(String literalText) {
        if (literalText == null ){
            return "";
        }

        if (literalText.startsWith("`")) {
            return literalText.replaceAll("(?:^`)|(?:`$)", "").replaceAll("\\\\`","`");
        }

        Integer runeValue = getRuneValue(literalText);
        if (runeValue != null)
            return Character.toString((char) runeValue.intValue());

        return literalText.replaceAll("(?:^\")|(?:\"$)", "");
    }

    public static Integer getRuneValue(String literalText) {
        if (!literalText.startsWith("'"))
            return null;
        String runeText = literalText.replaceAll("(?:^')|(?:'$)", "");
        if (runeText.isEmpty())
            return null;

        if (runeText.length() > 1){
            if (!runeText.startsWith("\\"))
                return null;

            String firstValue = runeText.substring(1, 2);
            if (firstValue.equals("a"))
                return 0x07;
            if (firstValue.equals("b"))
                return 0x08;
            if (firstValue.equals("f"))
                return 0x0c;
            if (firstValue.equals("n"))
                return 0x0a;
            if (firstValue.equals("r"))
                return 0x0d;
            if (firstValue.equals("t"))
                return 0x09;
            if (firstValue.equals("v"))
                return 0x0b;
            if (firstValue.equals("\\"))
                return 0x5c;
            if (firstValue.equals("'"))
                return 0x27;
            if (firstValue.equals("\""))
                return 0x22;            
            if (firstValue.equals("U") && runeText.length() == 10){
                String uValue = runeText.substring(2, 10);
                Integer value = Integer.parseInt(uValue, 16);
                if (value <= 0x10FFFF)
                    return value;
            }
            if (firstValue.equals("u") && runeText.length() == 6){
                String uValue = runeText.substring(2, 6);
                Integer value = Integer.parseInt(uValue, 16);
                if ((value < 0xD800) || (value > 0xDFFF)) // surrogate half
                    return value;
            }
            if (firstValue.equals("x") && runeText.length() == 4) {
                String uValue = runeText.substring(2, 4);
                return Integer.parseInt(uValue, 16);
            }
            if (runeText.length() == 4) { // octal
                String uValue = runeText.substring(1, 4);
                Integer value = Integer.parseInt(uValue, 8);
                if (value <= 255)
                    return value;
            }
            return null;
        } else {
            return runeText.codePointAt(0);
        }
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
        return importPath != null
            ? importPath.replaceAll("(?:[^/]+/)+", "")
            : null;
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

    public static boolean isNewLineNode(PsiElement node) {
        return isWhiteSpaceNode(node) && node.getText().contains("\n");
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

        PsiElement child = node.getFirstChild();
        while (child != null) {
            if (isNodeOfType(child, type)) {
                return child;
            }
            child = child.getNextSibling();
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

            if (!isWhiteSpaceOrComment(node.getPsi())) {
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

            if (!isWhiteSpaceOrComment(node.getPsi())) {
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
            GoFunctionDeclaration functionDeclaration = findParentOfType(element, GoFunctionDeclaration.class);
            if (functionDeclaration != null) {
                return new LocalSearchScope(functionDeclaration);
            }
            return new LocalSearchScope(element);
        }

        PsiElement scope = statement.getParent();
        if (scope instanceof GoVarDeclarations) {
            scope = scope.getParent();
        }

        if (scope == null) {
            scope = element;
        }

        return new LocalSearchScope(scope);
    }

    public static SearchScope getGlobalElementSearchScope(GoPsiElement element, String name) {
        if (GoNamesUtil.isExportedName(name)) {
            return GlobalSearchScope.projectScope(element.getProject());
        }

        return getPackageSearchScope(element);
    }

    private static SearchScope getPackageSearchScope(GoPsiElement element) {
        GoNamesCache namesCache = GoNamesCache.getInstance(element.getProject());
        String packageName = ((GoFile) element.getContainingFile()).getPackageName();
        Collection<GoFile> files = namesCache.getFilesByPackageName(packageName);
        return new LocalSearchScope(files.toArray(new PsiElement[files.size()]));
    }

    public static <T extends PsiElement> SmartPsiElementPointer<T> createSmartElementPointer(T element) {
        SmartPointerManager manager = SmartPointerManager.getInstance(element.getProject());
        return manager.createSmartPsiElementPointer(element);
    }

    @Nullable
    public static GoFile getContainingGoFile(GoPsiElementBase goPsiElement) {
        PsiFile file = goPsiElement.getContainingFile();

        return  (file != null && psiIsA(file, GoFile.class)) ? (GoFile) file : null;
    }

    private static final Pattern relativeImportPathRegex = Pattern.compile("^\\.\\.?/.*");

    public static String getAbsoluteImportPath(String value, GoFile goFile) {
        if ( value == null || goFile == null)
            return value;

        if ( ! relativeImportPathRegex.matcher(value).matches() ) {
            return value;
        }

        String fileImportPath = goFile.getPackageImportPath();

        value = value.replaceFirst("^(\\./)+", "");
        while ( value.startsWith("../") && !fileImportPath.isEmpty()) {
            value = value.substring(3);
            fileImportPath = fileImportPath.replaceFirst("(?:(?:^[^/]+)|(?:/[^/]+))$", "");
            value = value.replace("^(\\./)+", "");
        }

        return fileImportPath.isEmpty() ? value : fileImportPath + "/" + value;
    }
}
