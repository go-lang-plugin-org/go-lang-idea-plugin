package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeArray;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypePsiBacked;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.util.GoUtil;

import static ro.redeul.google.go.util.EditorUtil.reformatLines;

public class ChangeReturnsParametersFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    @Nullable
    private GoReturnStatement element;
    private GoExpr[] exprs;

    public ChangeReturnsParametersFix(@Nullable GoReturnStatement element) {
        super(element);
        this.element = element;
    }


    @NotNull
    @Override
    public String getText() {
        return "Change the function return parameters";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Function Returning";
    }


    @Override
    public void invoke(@NotNull final Project project,
                       @NotNull PsiFile file,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull final PsiElement startElement, @NotNull PsiElement endElement) {


        Document doc = PsiDocumentManager.getInstance(project).getDocument(file);

        if (doc == null || element == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;


        GoExpr[] expressions = element.getExpressions();

        for (GoExpr expr : expressions) {

            if (expr instanceof GoLiteralExpression) {
                GoLiteral literal = ((GoLiteralExpression) expr).getLiteral();
                if (literal instanceof GoLiteralFunction) {
                    if (i != 0) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append(GoUtil.getFuncDecAsParam(((GoLiteralFunction) literal).getParameters(),
                            ((GoLiteralFunction) literal).getResults(),
                            (GoFile) element.getContainingFile()));
                    i++;
                    continue;
                }
            }

            GoType[] types = expr.getType();
            for (GoType type : types) {
                if (i != 0) {
                    stringBuilder.append(",");
                }
                if (type != null) {

                    while (!(type instanceof GoTypePsiBacked)) {
                        if (type instanceof GoTypePointer) {
                            type = ((GoTypePointer) type).getTargetType();
                            stringBuilder.append("*");
                            continue;
                        }
                        break;
                    }


                    if (type instanceof GoTypePsiBacked) {
                        GoPsiType goPsiType = ((GoTypePsiBacked) type).getPsiType();
                        stringBuilder.append(GoUtil.getNameLocalOrGlobal(goPsiType, (GoFile) element.getContainingFile()));
                    } else if (type instanceof GoTypeArray) {
                        GoPsiType goPsiType = ((GoTypeArray) type).getPsiType();
                        stringBuilder.append(GoUtil.getNameLocalOrGlobal(goPsiType, (GoFile) element.getContainingFile()));
                    }
                } else {
                    //As some Expression may not return a valid type, in this case will not modify anithing and exit
                    return;
                }
                i++;
            }
        }

        GoFunctionDeclaration functionDeclaration = GoPsiUtils.findParentOfType(element, GoFunctionDeclaration.class);
        int startOffset;
        PsiElement result = GoPsiUtils.findChildOfType(functionDeclaration, GoElementTypes.FUNCTION_RESULT);
        if (result != null) {
            startOffset = result.getTextOffset();
            doc.replaceString(startOffset, result.getTextRange().getEndOffset(),
                    i > 1 ?
                            "(" + stringBuilder.toString() + ")" : stringBuilder.toString()
            );
        } else {
            startOffset = functionDeclaration.getBlock().getTextOffset();
            doc.insertString(startOffset,
                    i > 1 ?
                            "(" + stringBuilder.toString() + ")" : stringBuilder.toString()
            );
        }

        if (editor != null) {
            int line = doc.getLineNumber(startOffset);
            reformatLines(file, editor, line, line);
        }

    }
}
