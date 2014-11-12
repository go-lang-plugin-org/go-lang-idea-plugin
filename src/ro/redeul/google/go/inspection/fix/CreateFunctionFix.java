package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.editor.TemplateUtil;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.util.GoUtil;

import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class CreateFunctionFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    private PsiFile workingFile = null;

    public CreateFunctionFix(@Nullable PsiElement element) {
        super(element);
    }


    public CreateFunctionFix(PsiElement element, PsiElement resolve) {
        super(element);
    }

    public CreateFunctionFix(PsiElement element, PsiFile workingFile) {
        super(element);
        this.workingFile = workingFile;
    }

    /**
     * Helper method to generate function arguments type, based on param being passed to the function
     *
     * @param e          -> parent -> GoCallOrConvExpression
     * @param stringList List<String>
     * @return the generated arugment list ex: arg0 int, arg1 string
     */
    @Nullable
    public static String InspectionGenFuncArgs(PsiElement e, List<String> stringList) {
        StringBuilder stringBuilder = new StringBuilder();
        int arg = 0;
        final GoFile currentFile = (GoFile) e.getContainingFile();


        if (GoUtil.isFunctionNameIdentifier(e)) {
            GoCallOrConvExpression callOrConvExpression = findParentOfType(e, GoCallOrConvExpression.class);
            stringBuilder.append("(");
            for (GoExpr argument : callOrConvExpression.getArguments()) {
                if (arg != 0)
                    stringBuilder.append(',');

                stringBuilder.append(String.format("$v%d$ ", arg));
                stringList.add(String.format("arg%d", arg));

                PsiElement firstChildExp = argument.getFirstChild();
                GoType[] goTypes = argument.getType();

                if (goTypes.length > 0 && goTypes[0] != null) {
                    GoType goType = goTypes[0];
                    stringBuilder.append(GoTypes.getRepresentation(goType, currentFile));

                } else if (firstChildExp instanceof GoLiteral) {
                    /*
                     * Resolves the type of a literal
                     */
                    stringBuilder.append(((GoLiteral) firstChildExp).getType().name().toLowerCase());
                } else {

                    /*
                     * This block try to resolve the return type of a closure being called on the paramenter list
                     * ex: unresolvedFn(func()int{return 25*4}())
                     * will generate:
                     * func unresolvedFn(arg0 int){}
                     * @note i think any one will do that, but we never know :D
                     */
                    PsiElement firstChild = firstChildExp.getFirstChild();
                    if (firstChild instanceof GoLiteralFunction) {

                        GoType[] returnType = ((GoLiteralFunction) firstChild).getReturnTypes();
                        if (returnType.length > 0) {
                            stringBuilder.append("<berila>");
                        } else {
                            stringBuilder.append("interface{}");
                        }
                    } else if (firstChild instanceof GoLiteral) {
                        GoLiteral.Type type = ((GoLiteral) firstChild).getType();
                        //Fix TEST PR ##321 this only happens on test. i don't know why
                        if (type == GoLiteral.Type.Float || type == GoLiteral.Type.ImaginaryFloat) {
                            stringBuilder.append("float32");
                        } else {
                            stringBuilder.append(type.name().toLowerCase());
                        }
                    } else {
                        stringBuilder.append("interface{}");
                    }
                }
                arg++;
            }
            stringBuilder.append(")");
        } else {
            /*
             * Try to resolve the type declaration for the generated function based on called function
             * ex: when http.HandleFunc("/",myIndexHandle)
             * will generate:
             *          func myIndexHandle(arg0 http.ResponseWriter, arg1 *http.Request){}
             */

            GoCallOrConvExpression callExpression = findParentOfType(e, GoCallOrConvExpression.class);
            GoType[] type = callExpression.getBaseExpression().getType();

            if ( type.length != 1 || ! (type[0] instanceof GoTypeFunction) )
                return "";

            GoTypeFunction function = (GoTypeFunction) type[0];

            int pos = -1;
            for (GoExpr expr : callExpression.getArguments()) {
                pos++;
                if ( expr.getTextOffset() == e.getTextOffset() && expr.getTextLength() == e.getTextLength() )
                    break;
            }

            GoType desiredType = function.getParameterType(pos);

            if ( desiredType instanceof GoTypeFunction ) {
                GoTypeFunction typeFunction = (GoTypeFunction) desiredType;
                int i = 0;

                stringBuilder.append("(");
                GoType parameterType = null;
                while ( (parameterType = typeFunction.getParameterType(i)) != null) {
                    stringList.add(String.format("arg%d", i));

                    if ( i > 0 )
                        stringBuilder.append(",");

                    stringBuilder.append(String.format("$v%d$ ", i));
                    stringBuilder.append(GoTypes.getRepresentation(parameterType, currentFile));
                    i++;
                }
                stringBuilder.append(")");

                GoType[] results = typeFunction.getResultTypes();
                if ( results.length > 1 ) stringBuilder.append("(");
                for (int i1 = 0; i1 < results.length; i1++) {
                    GoType goType = results[i1];
                    if( i1 > 0)
                        stringBuilder.append(",");
                    stringBuilder.append(GoTypes.getRepresentation(goType, currentFile));
                }
                if ( results.length > 1 ) stringBuilder.append(")");
            }
        }

        return stringBuilder.toString();
    }

    @NotNull
    @Override
    public String getText() {
        return "Create function \"" + getStartElement().getText() + "\"";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Variable Declaration";
    }


    @Override
    public void invoke(@NotNull final Project project,
                       @NotNull PsiFile file,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull final PsiElement startElement, @NotNull PsiElement endElement) {
        final PsiElement e;
        int insertPoint;
        final PsiFile wFile;
        ArrayList<String> arguments = new ArrayList<String>();
        final String fnArguments = InspectionGenFuncArgs(startElement, arguments);
        if (workingFile == null) {
            wFile = file;
            e = startElement;
            GoFunctionDeclaration fd = findParentOfType(e, GoFunctionDeclaration.class);
            while (fd instanceof GoLiteralFunction) {
                fd = findParentOfType(fd.getParent(), GoFunctionDeclaration.class);
            }


            if (fd != null) {
                insertPoint = fd.getTextRange().getEndOffset();
            } else {
                insertPoint = file.getTextRange().getEndOffset();
            }

        } else {
            e = startElement.getLastChild();
            insertPoint = workingFile.getTextLength();
            FileEditorManager.getInstance(project).openFile(workingFile.getVirtualFile(), true, true);
            wFile = this.workingFile;
        }


        Document doc = PsiDocumentManager.getInstance(project).getDocument(wFile);
        if (doc == null) {
            return;
        }

        if (((GoFile) wFile).getPackageName().equals("")) {
            String packStr = String.format("\npackage %s", startElement.getFirstChild().getText());
            doc.insertString(insertPoint, packStr);
            insertPoint += packStr.length();
        }

        TemplateImpl template = TemplateUtil.createTemplate(String.format("\n\nfunc %s%s { \n$v%d$$END$\n}", e.getText(), fnArguments, arguments.size()));
        arguments.add("//TODO: implements " + e.getText());
        TemplateUtil.runTemplate(editor, insertPoint, arguments, template);
    }

    private static final String TEMPLATE = "" +
            "" +
            "func $functionName$()#if($returnCount > 1)()#end {" +
            "   $END$" +
            "#if($returnCount > 0)  return#{else}}#end";
}
