package ro.redeul.google.go.intentions.statements;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.editor.TemplateUtil;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypePsiBacked;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;
import ro.redeul.google.go.util.GoUtil;

import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class CheckErrorIntention extends Intention {

    protected GoExpressionStatement statement;
    protected GoExpr expr;

    @Override
    protected boolean satisfiedBy(PsiElement element) {
        statement = element instanceof GoExpressionStatement ? (GoExpressionStatement) element : findParentOfType(element, GoExpressionStatement.class);
        if (statement == null && element instanceof PsiWhiteSpace && element.getPrevSibling() instanceof GoExpressionStatement) {
            statement = (GoExpressionStatement) element.getPrevSibling();
        }
        if (statement != null) {
            expr = statement.getExpression();
            if (expr != null) {
                for (GoType goType : expr.getType()) {
                    if (goType != null) {
                        if (goType instanceof GoTypePsiBacked) {
                            GoPsiType psiType = GoTypeUtils.resolveToFinalType(((GoTypePsiBacked) goType).getPsiType());
                            if (psiType instanceof GoPsiTypeName) {
                                if (psiType.getText().equals("error") && ((GoPsiTypeName) psiType).isPrimitive())
                                    return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor) throws IntentionExecutionException {
        TextRange textRange = statement.getTextRange();

        StringBuilder ifString = new StringBuilder();

        String k = "err";
        ifString.append("if ");
        GoType[] types = expr.getType();
        List<String> stringList = new ArrayList<String>();

        int j = 0;
        if (types.length > 1) {
            StringBuilder checkString = new StringBuilder();
            j = 0;
            int c = 0;
            int i = 0;

            for (GoType goType : types) {

                if (j != 0) {
                    ifString.append(",");
                }

                String currentVar = String.format("$v%d$", j);
                if (goType != null) {
                    if (goType instanceof GoTypePsiBacked) {
                        GoPsiType psiType = GoTypeUtils.resolveToFinalType(((GoTypePsiBacked) goType).getPsiType());
                        if (psiType instanceof GoPsiTypeName && psiType.getText().equals("error") && ((GoPsiTypeName) psiType).isPrimitive()) {
                            if (k.equals("err") && i != 0)
                                k = "err0";
                            while (GoUtil.TestDeclVar(expr, k)) {
                                k = String.format("err%d", i);
                                i++;
                            }
                            if (c != 0) {
                                checkString.append(" || ");
                            }
                            stringList.add(k);

                            ifString.append(currentVar);
                            checkString.append(currentVar)
                                    .append(" != nil");
                            j++;
                            i++;
                            c++;
                            continue;
                        }
                    }
                }
                ifString.append(currentVar);
                stringList.add("_");
                j++;
            }

            ifString
                    .append(":=")
                    .append(expr.getText())
                    .append(";")
                    .append(checkString)
                    .append("{");


        } else {
            ifString
                    .append(expr.getText())
                    .append(" != nil {");

        }

        ifString.append(String.format("\n$v%d$$END$\n}", j));
        stringList.add("//TODO: Handle error(s)");
        //stringList.add("panic(\"Unhandled error!\")");
        TemplateUtil.runTemplate(editor, textRange, stringList, TemplateUtil.createTemplate(ifString.toString()));
    }

}
