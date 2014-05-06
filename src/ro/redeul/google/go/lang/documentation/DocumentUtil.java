package ro.redeul.google.go.lang.documentation;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNewLineNode;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class DocumentUtil {

    private static String getTailingDocumentOfElement(PsiElement element) {
        boolean foundNewLine = false;
        List<String> comments = new ArrayList<String>();
        while ((element = element.getNextSibling()) != null) {
            if (isNodeOfType(element, GoElementTypes.COMMENTS)) {
                foundNewLine = false;
                comments.add(getCommentText(element));
            } else if (isNewLineNode(element)) {
                if (foundNewLine || StringUtil.countChars(element.getText(), '\n') > 1) {
                    break;
                }

                foundNewLine = true;
            } else if (!(element instanceof PsiWhiteSpace)) {
                break;
            }
        }

        return StringUtil.join(comments, "\n");
    }

    private static String getHeaderDocumentOfElement(PsiElement element) {
        boolean foundNewLine = false;
        List<String> comments = new ArrayList<String>();
        while ((element = element.getPrevSibling()) != null) {
            if (isNodeOfType(element, GoElementTypes.COMMENTS)) {
                foundNewLine = false;
                comments.add(getCommentText(element));
            } else if (isNewLineNode(element)) {
                if (foundNewLine || StringUtil.countChars(element.getText(), '\n') > 1) {
                    break;
                }
                foundNewLine = true;
            } else if (!(element instanceof PsiWhiteSpace)) {
                break;
            }
        }

        Collections.reverse(comments);
        return StringUtil.join(comments, "\n");
    }

    private static String getCommentText(PsiElement comment) {
        String text = comment.getText().trim();
        if (text.startsWith("//")) {
            return text.substring(2);
        }
        return text.substring(2, text.length() - 2);
    }

    public static String getTailingOrHeaderDocument(PsiElement element) {
        if (element == null) {
            return "";
        }

        String text = getTailingDocumentOfElement(element);
        if (text.isEmpty()) {
            text = getHeaderDocumentOfElement(element);
        }
        return text;
    }

    public static String getTypeDocument(GoTypeNameDeclaration type) {
        if (type == null) {
            return "";
        }

        PsiFile file = type.getContainingFile();
        if (!(file instanceof GoFile)) {
            return "";
        }

        for (GoTypeDeclaration td : ((GoFile) file).getTypeDeclarations()) {
            for (GoTypeSpec spec : td.getTypeSpecs()) {
                if (spec != null && type.isEquivalentTo(spec.getTypeNameDeclaration())) {
                    String text = getHeaderDocumentOfElement(spec);
                    return text.isEmpty() ? getHeaderDocumentOfElement(td) : text;
                }
            }
        }

        return "";
    }

    public static String getFunctionDocument(GoFunctionDeclaration function) {
        String text = getFunctionQuickNavigationInfo(function).replace("\n", "<br/>") + "<br/><br/>\n";
        return text + getHeaderDocumentOfElement(function.getOriginalElement());
    }

    public static String getMethodDocument(GoMethodDeclaration method) {
        String text = getFunctionQuickNavigationInfo(method).replace("\n", "<br/>") + "<br/><br/>\n";
        return text + getHeaderDocumentOfElement(method.getOriginalElement());
    }

    public static String getVarDocument(GoLiteralIdentifier id) {
        if (id == null) {
            return "";
        }

        PsiElement original = id.getOriginalElement();
        if (!(original instanceof GoLiteralIdentifier)) {
            return "";
        }

        PsiElement parent = original.getParent();
        if (!(parent instanceof GoVarDeclaration)) {
            return "";
        }

        PsiElement grandpa = parent.getParent();
        if (!(grandpa instanceof GoVarDeclarations)) {
            return "";
        }

        String doc = getTailingOrHeaderDocument(parent);

        if (doc.isEmpty() && ((GoVarDeclarations) grandpa).getDeclarations().length == 1) {
            doc = getTailingDocumentOfElement(grandpa);
        }

        if (doc.isEmpty()) {
            doc = getHeaderDocumentOfElement(grandpa);
        }
        return doc;
    }

    public static String getConstDocument(GoLiteralIdentifier id) {
        if (id == null) {
            return "";
        }

        PsiElement original = id.getOriginalElement();
        if (!(original instanceof GoLiteralIdentifier)) {
            return "";
        }

        PsiElement parent = original.getParent();
        if (!(parent instanceof GoConstDeclaration)) {
            return "";
        }

        PsiElement grandpa = parent.getParent();
        if (!(grandpa instanceof GoConstDeclarations)) {
            return "";
        }

        String doc = getTailingOrHeaderDocument(parent);

        if (doc.isEmpty() && ((GoConstDeclarations) grandpa).getDeclarations().length == 1) {
            doc = getTailingDocumentOfElement(grandpa);
        }

        if (doc.isEmpty()) {
            doc = getHeaderDocumentOfElement(grandpa);
        }
        return doc;
    }

    private static String getElementPackageInfo(PsiElement element) {
        PsiFile file = element.getContainingFile();

        if (!(file instanceof GoFile)) {
            return "";
        }

        VirtualFile virtualFile = file.getVirtualFile();
        String packageName = ((GoFile) file).getPackage().getPackageName();
        return virtualFile != null ? packageName + "/" + virtualFile.getName() : packageName;
    }

    public static String getFunctionQuickNavigationInfo(GoFunctionDeclaration fd) {
        String packageInfo = getElementPackageInfo(fd);
        String functionInfo = getFunctionPresentationText(fd);
        return packageInfo + "\n" + functionInfo;
    }

    public static String getFunctionPresentationText(GoFunctionDeclaration fd) {
        StringBuilder sb = new StringBuilder(fd.getFunctionName());
        String params = parametersToString(fd.getParameters());
        String results = parametersToString(fd.getResults());
        sb.append("(").append(params).append(")");
        if (!results.isEmpty()) {
            sb.append(": (").append(results).append(")");
        }
        return sb.toString();
    }

    /**
     * In the result of {@link #getFunctionPresentationText}, find text range of specified parameter
     * @param fd        The function definition
     * @param index     Parameter position.
     * @return Text range of the parameter
     */
    public static TextRange getFunctionParameterRangeInText(GoFunctionDeclaration fd, int index) {
        return parameterRangeInText(fd.getParameters(), index).shiftRight(fd.getFunctionName().length() + 1);
    }

    private static TextRange parameterRangeInText(GoFunctionParameter[] parameters, int expectedIndex) {
        if (parameters == null || parameters.length == 0) {
            return TextRange.EMPTY_RANGE;
        }

        int start = 0;
        int currentIndex = 0;
        StringBuilder sb = new StringBuilder();
        for (GoFunctionParameter fp : parameters) {
            GoLiteralIdentifier[] ids = fp.getIdentifiers();
            GoPsiType type = fp.getType();
            String variadic = fp.isVariadic() ? "..." : "";
            String typeName = variadic + String.valueOf(type != null ? type.getText() : null);
            start = sb.length();
            if (ids.length == 0) {
                sb.append(typeName).append(", ");
                if (currentIndex++ == expectedIndex) {
                    return new TextRange(start, sb.length() - 2);
                }
                continue;
            } else if (ids.length == 1) {
                sb.append(ids[0].getName()).append(" ").append(typeName).append(", ");
                if (currentIndex++ == expectedIndex) {
                    return new TextRange(start, sb.length() - 2);
                }
                continue;
            }

            for (int i = 0; i < ids.length; i++) {
                start = sb.length();
                sb.append(ids[i].getName()).append(", ");
                if (i != ids.length - 1 && currentIndex++ == expectedIndex) {
                    return new TextRange(start, sb.length() - 2);
                }
            }
            sb.insert(sb.length() - 2, " " + typeName);
            if (currentIndex++ == expectedIndex) {
                return new TextRange(start, sb.length() - 2);
            }
        }

        return new TextRange(start, sb.length() - 2);
    }

    private static String parametersToString(GoFunctionParameter[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (GoFunctionParameter fp : parameters) {
            GoLiteralIdentifier[] ids = fp.getIdentifiers();
            GoPsiType type = fp.getType();
            String variadic = fp.isVariadic() ? "..." : "";
            String typeName = variadic + String.valueOf(type != null ? type.getText() : null);
            if (ids.length == 0) {
                sb.append(typeName).append(", ");
                continue;
            } else if (ids.length == 1) {
                sb.append(ids[0].getName()).append(" ").append(typeName).append(", ");
                continue;
            }

            for (GoLiteralIdentifier id : ids) {
                sb.append(id.getName()).append(", ");
            }
            sb.insert(sb.length() - 2, " " + typeName);
        }

        return sb.delete(sb.length() - 2, sb.length()).toString();
    }

    public static void replaceElementWithText(final Document document, PsiElement element, final String text) {
        final int start = element.getTextOffset();
        final int end = start + element.getTextLength();

        WriteCommandAction writeCommandAction = new WriteCommandAction(element.getContainingFile().getProject()) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                document.replaceString(start, end, text);
            }
        };
        writeCommandAction.execute();
    }
}
