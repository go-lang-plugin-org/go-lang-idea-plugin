package ro.redeul.google.go.lang.documentation;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import ro.redeul.google.go.ide.structureview.GoStructureViewElement;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

class DocumentUtil {

    public static String getTailingDocumentOfElement(PsiElement element) {
        boolean foundNewLine = false;
        List<String> comments = new ArrayList<String>();
        while ((element = element.getNextSibling()) != null) {
            if (isNodeOfType(element, GoElementTypes.COMMENTS)) {
                foundNewLine = false;
                comments.add(getCommentText(element));
            } else if (isNodeOfType(element, GoElementTypes.wsNLS)) {
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

    public static String getHeaderDocumentOfElement(PsiElement element) {
        boolean foundNewLine = false;
        List<String> comments = new ArrayList<String>();
        while ((element = element.getPrevSibling()) != null) {
            if (isNodeOfType(element, GoElementTypes.COMMENTS)) {
                foundNewLine = false;
                comments.add(getCommentText(element));
            } else if (isNodeOfType(element, GoElementTypes.wsNLS)) {
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

    public static String getCommentText(PsiElement comment) {
        String text = comment.getText().trim();
        if (text.startsWith("//")) {
            return text.substring(2);
        }
        return text.substring(2, text.length() - 2);
    }

    public static String getTypeDocument(GoTypeNameDeclaration type) {
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

        String doc = getTailingDocumentOfElement(parent);
        if (doc.isEmpty()) {
            doc = getHeaderDocumentOfElement(parent);
        }

        if (doc.isEmpty() && ((GoVarDeclarations) grandpa).getDeclarations().length == 1) {
            doc = getTailingDocumentOfElement(grandpa);
        }

        if (doc.isEmpty()) {
            doc = getHeaderDocumentOfElement(grandpa);
        }
        return doc;
    }

    public static String getConstDocument(GoLiteralIdentifier id) {
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

        String doc = getTailingDocumentOfElement(parent);
        if (doc.isEmpty()) {
            doc = getHeaderDocumentOfElement(parent);
        }

        if (doc.isEmpty() && ((GoConstDeclarations) grandpa).getDeclarations().length == 1) {
            doc = getTailingDocumentOfElement(grandpa);
        }

        if (doc.isEmpty()) {
            doc = getHeaderDocumentOfElement(grandpa);
        }
        return doc;
    }

    public static String getElementPackageInfo(PsiElement element) {
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
        String functionInfo = GoStructureViewElement.getFunctionPresentationText(fd);
        return packageInfo + "\n" + functionInfo;
    }
}
