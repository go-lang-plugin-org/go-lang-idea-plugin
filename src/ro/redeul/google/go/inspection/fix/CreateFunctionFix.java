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
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.editor.TemplateUtil;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoRelationalExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeArray;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypePsiBacked;
import ro.redeul.google.go.lang.psi.utils.GoExpressionUtils;
import ro.redeul.google.go.util.GoUtil;

import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findChildOfClass;
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
     * @param stringList
     * @return the generated arugment list ex: arg0 int, arg1 string
     */
    public static String InspectionGenFuncArgs(PsiElement e, List<String> stringList) {
        StringBuilder stringBuilder = new StringBuilder();
        int arg = 0;
        final GoFile currentFile = (GoFile) e.getContainingFile();

        if (GoUtil.isFunctionNameIdentifier(e)) {

            for (GoExpr argument : ((GoCallOrConvExpression) e.getParent()).getArguments()) {
                if (arg != 0)
                    stringBuilder.append(',');

                stringBuilder.append(String.format("$v%d$ ", arg));
                stringList.add(String.format("arg%d", arg));

                PsiElement firstChildExp = argument.getFirstChild();
                GoType[] goTypes = argument.getType();


                // FIX TEST ##321
                // Check first Relational is alwais boolean
                if (argument instanceof GoRelationalExpression) {
                    stringBuilder.append("bool");
                } else if (goTypes.length > 0 && goTypes[0] != null) {
                    GoType goType = goTypes[0];

                    if (goType instanceof GoTypePointer) {
                        /*
                         * Detects when a reference is being passed
                         */
                        //Fix: Now go we are receiving the right typ
                        stringBuilder.append('*');
                        goType = ((GoTypePointer) goType).getTargetType();
                    }

                    if (goType instanceof GoTypePsiBacked) {
                         /*
                          * Using the psiType,
                          */
                        String type = GoUtil.getNameLocalOrGlobal(((GoTypePsiBacked) goType).getPsiType(), currentFile);
                        stringBuilder.append(type);
                    } else if (goType instanceof GoTypeArray) {
                         /*
                          * Using the psiType,
                          */
                        String type = GoUtil.getNameLocalOrGlobal(((GoTypeArray) goType).getPsiType(), currentFile);
                        stringBuilder.append(type);
                    } else if (firstChildExp instanceof GoLiteralFunction) {
                         /*
                          * Resolves the type of a function decl
                          * ex: the type of http.HandleFunc is func(string,func(http.ResponseWriter,*http.Request))
                          */
                        GoFunctionDeclaration functionDeclaration = (GoFunctionDeclaration) firstChildExp;
                        stringBuilder.append(GoUtil.getFuncDecAsParam(functionDeclaration.getParameters(), functionDeclaration.getResults(), currentFile));
                    } else {

                        /*
                         * This block try to resolve a closure variable
                         * ex: var myClosure=func()int{return 25*4}
                         *      unresolvedFn(myClosure)
                         * will generate:
                         * func unresolvedFn(arg0 func()int){}
                         */

                        final PsiReference[] references = firstChildExp.getReferences();
                        if (references.length > 0) {
                            PsiElement resolve = references[0].resolve();
                            if (resolve != null) {
                                GoPsiElement resl_element = (GoPsiElement) resolve.getParent().getLastChild();
                                GoLiteralFunction fn = (GoLiteralFunction) resl_element.getFirstChild();
                                stringBuilder.append(GoUtil.getFuncDecAsParam(fn.getParameters(), fn.getResults(), currentFile));
                            } else {
                                stringBuilder.append("interface{}");
                            }
                        } else {
                            stringBuilder.append("interface{}");
                        }

                    }
                } else if (firstChildExp instanceof GoLiteral) {
                    /*
                     * Resolves the type of a literal
                     */
                    //GoPsiElement resolveTo = null;
                    //if (firstChildExp instanceof GoLiteralIdentifier) {
                    //   resolveTo = ResolveTypeOfVarDecl(firstChildExp);
                    //}
                    //if (resolveTo instanceof GoPsiType) {
                    //    stringBuilder.append(getNameLocalOrGlobal((GoPsiType) resolveTo, currentFile));
                    //} else {
                    stringBuilder.append(((GoLiteral) firstChildExp).getType().name().toLowerCase());
                    //}
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

                        GoPsiType[] returnType = ((GoLiteralFunction) firstChild).getReturnType();
                        if (returnType.length > 0) {
                            stringBuilder.append(returnType[0].getName());
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
        } else {
            /*
             * Try to resolve the type declaration for the generated function based on called function
             * ex: when http.HandleFunc("/",myIndexHandle)
             * will generate:
             *          func myIndexHandle(arg0 http.ResponseWriter, arg1 *http.Request){}
             */
            //TODO: separate this block in another method, improve to be able to generate the function from an generated template, and generate the results
            GoCallOrConvExpression parentOfTypeCallOrConvExpression = findParentOfType(e, GoCallOrConvExpression.class);
            GoFunctionDeclaration goFunctionDeclaration = GoExpressionUtils.resolveToFunctionDeclaration(parentOfTypeCallOrConvExpression);

            if (goFunctionDeclaration != null) {

                int myIndex = 0;

                for (GoExpr argum : parentOfTypeCallOrConvExpression.getArguments()) {
                    if (argum.equals(e) || argum.getFirstChild().equals(e))
                        break;
                    myIndex++;
                }

                GoFunctionParameter[] parameter = goFunctionDeclaration.getParameters();

                if (myIndex < parameter.length) {
                    GoPsiType type = parameter[myIndex].getType();
                    if (type instanceof GoPsiTypeFunction) {
                        GoFunctionParameterList goFunctionParameterList = findChildOfClass(type, GoFunctionParameterList.class);
                        if (goFunctionParameterList != null) {
                            for (GoFunctionParameter parameter1 : goFunctionParameterList.getFunctionParameters()) {
                                if (arg > 0)
                                    stringBuilder.append(',');

                                stringBuilder.append(String.format("$v%d$ ", arg));
                                stringList.add(String.format("arg%d", arg));

                                final GoPsiType type1 = parameter1.getType();
                                if (type1 instanceof GoPsiTypePointer) {
                                    if (type1.getParent().getNode().getElementType().equals(GoElementTypes.FUNCTION_PARAMETER_VARIADIC))
                                        stringBuilder.append("...");
                                    stringBuilder.append('*').append(GoUtil.getNameLocalOrGlobal(((GoPsiTypePointer) type1).getTargetType(), currentFile));
                                } else {
                                    stringBuilder.append(GoUtil.getNameLocalOrGlobalAsParameter(type1, currentFile));
                                }
                                arg++;
                            }
                        }

                    }
                }

            } else {
                return "";
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

        TemplateImpl template = TemplateUtil.createTemplate(String.format("\n\nfunc %s(%s) { \n$v%d$$END$\n}", e.getText(), fnArguments, arguments.size()));
        arguments.add("//TODO: implements " + e.getText());
        TemplateUtil.runTemplate(editor, insertPoint, arguments, template);
    }
}
