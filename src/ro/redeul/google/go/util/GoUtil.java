package ro.redeul.google.go.util;

import com.intellij.ide.Bootstrap;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoRelationalExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoExpressionUtils;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ro.redeul.google.go.lang.psi.utils.GoIdentifierUtils.getFunctionDeclaration;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 4:18:54 AM
 */
public class GoUtil {

    /**
     * Gets the idea home directory.
     * Note: Copied over from IDEA Main class but the home directory doesn't seem to be properly made available to
     * plugins.
     *
     * @return The idea home directory
     */
    private static File getIdeaHomeDir() {
        URL url = Bootstrap.class.getResource("");
        if (url == null || !"jar".equals(url.getProtocol())) return null;

        String path = url.getPath();

        int start = path.indexOf("file:/");
        int end = path.indexOf("!/");
        if (start == -1 || end == -1) return null;

        String jarFileUrl = path.substring(start, end);

        try {
            File bootstrapJar = new File(new URI(jarFileUrl));
            return bootstrapJar.getParentFile().getParentFile();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static boolean testGoHomeFolder(String goRoot) {
        return goRoot != null
                && goRoot.trim().length() > 0 && new File(goRoot).isDirectory();
    }

    private final static Pattern RE_PACKAGE_TARGET =
            Pattern.compile("^TARG=([^\\s]+)\\s*$", Pattern.MULTILINE);

    /**
     * Returns a string if there is a TARG=xxx specified in the provided makefile and null if there is no such file.
     *
     * @param makefile the file we want to test (can be null)
     * @return the specified target or null
     */
    public static String getTargetFromMakefile(VirtualFile makefile) {
        if (makefile == null) {
            return null;
        }

        try {
            String content = new String(makefile.contentsToByteArray(), "UTF-8");

            Matcher matcher = RE_PACKAGE_TARGET.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (IOException e) {
            //
        }

        return null;
    }


    public static String getFuncDecAsParam(GoFunctionParameter[] parameters, GoFunctionParameter[] results1, GoFile currentPackge) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("func(");

        int counter = 0;

        for (GoFunctionParameter parameter : parameters) {
            if (counter != 0)
                stringBuilder.append(',');
            stringBuilder.append(getNameLocalOrGlobal(parameter.getType(), currentPackge));
            counter++;
        }
        stringBuilder.append(')');

        counter = 0;

        if (results1.length > 1)
            stringBuilder.append('(');

        for (GoFunctionParameter parameter : results1) {
            if (counter != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(getNameLocalOrGlobal(parameter.getType(), currentPackge));
            counter++;
        }

        if (counter > 1)
            stringBuilder.append(')');

        return stringBuilder.toString();
    }

    public static boolean isFunctionNameIdentifier(PsiElement e) {
        if (!psiIsA(e, GoLiteralExpression.class))
            return false;

        GoLiteral literal = ((GoLiteralExpression) e).getLiteral();
        if (!(literal instanceof GoLiteralIdentifier))
            return false;

        if (((GoLiteralIdentifier) literal).isQualified())
            return false;

        if (!psiIsA(e.getParent(), GoCallOrConvExpression.class))
            return false;

        // function name is the first element of its parent.
        return e.getStartOffsetInParent() == 0;
    }

    private static String recursiveNameOrGlobalTypeImp(GoPsiType type, GoFile currentFile) {
        if (type instanceof GoPsiTypePointer) {
            return String.format("*%s", recursiveNameOrGlobalTypeImp(((GoPsiTypePointer) type).getTargetType(), currentFile));
        } else if (type instanceof GoPsiTypeSlice) {
            return String.format("[]%s", recursiveNameOrGlobalTypeImp(((GoPsiTypeSlice) type).getElementType(), currentFile));
        } else if (type instanceof GoPsiTypeArray) {
            GoPsiTypeArray elementType = (GoPsiTypeArray) type;
            return String.format("[%s]%s", elementType.getArrayLength(), recursiveNameOrGlobalTypeImp(elementType.getElementType(), currentFile));
        } else if (type instanceof GoPsiTypeMap) {
            GoPsiTypeMap type1 = (GoPsiTypeMap) type;
            return String.format("map[%s]%s", recursiveNameOrGlobalTypeImp(type1.getKeyType(), currentFile), recursiveNameOrGlobalTypeImp(type1.getElementType(), currentFile));
        } else if (type instanceof GoPsiTypeChannel) {
            GoPsiTypeChannel type1 = (GoPsiTypeChannel) type;
            return String.format("%s %s", GoTypeChannel.ChannelType.getText(type1.getChannelType()), recursiveNameOrGlobalTypeImp(type1.getElementType(), currentFile));
        } else if (type instanceof GoPsiTypeFunction) {
            GoPsiTypeFunction type1 = (GoPsiTypeFunction) type;
            return getFuncDecAsParam(type1.getParameters(), type1.getResults(), currentFile);
        } else if (type instanceof GoPsiTypeInterface) {
            GoPsiTypeName[] typeNames = ((GoPsiTypeInterface) type).getTypeNames();
            if (typeNames.length == 0)
                return "interface{}";
            type = typeNames[0];
        }

        StringBuilder stringBuilder = new StringBuilder();
        PsiDirectory containingDirectory = type.getContainingFile().getContainingDirectory();
        boolean isInSameDir = currentFile.getContainingDirectory().equals(containingDirectory);
        if (((GoPsiTypeName) type).isPrimitive() || isInSameDir) {
            stringBuilder.append(type.getName());
        } else {
            FORLOOP:
            for (GoImportDeclarations declarations : currentFile.getImportDeclarations())
                for (GoImportDeclaration declaration : declarations.getDeclarations()) {
                    String canonicalPath = containingDirectory.getVirtualFile().getCanonicalPath();
                    if (canonicalPath != null && canonicalPath.endsWith(declaration.getImportPath().getValue())) {
                        String visiblePackageName = declaration.getVisiblePackageName();
                        if (visiblePackageName.equals(".")) {
                            stringBuilder.append(type.getName());
                        } else {
                            stringBuilder.append(visiblePackageName).append(".").append(type.getName());
                        }
                        break FORLOOP;
                    }
                }
        }
        return stringBuilder.toString();
    }

    public static String getNameLocalOrGlobal(GoPsiType type, GoFile currentFile) {

        if (type.getParent().getNode().getElementType().equals(GoElementTypes.FUNCTION_PARAMETER_VARIADIC))
            return String.format("...%s", recursiveNameOrGlobalTypeImp(type, currentFile));

        return recursiveNameOrGlobalTypeImp(type, currentFile);
    }

    private static Object tryPsiType(Object element) {
        if (element instanceof GoPsiType) {
            return element;
        }
        if (element instanceof GoTypePsiBacked) {
            return ((GoTypePsiBacked) element).getPsiType();
        }
        return element;
    }


    public static boolean CompairTypes(GoPsiType element, Object element2) {
        return CompairTypes(element, element2, null);
    }

    public static GoPsiElement ResolveReferece(GoPsiElement element) {
        for (PsiReference reference : element.getReferences()) {
            PsiElement resolve = reference.resolve();
            if (resolve != null)
                return ResolveReferece((GoPsiElement) resolve);
        }
        return element;
    }

    public static boolean CompairTypes(GoPsiType element, Object element2, GoPsiElement goExpr) {
        if (element instanceof GoPsiTypeFunction && !(element2 instanceof GoPsiTypeFunction)) {

            if (goExpr instanceof GoCallOrConvExpression) {
                element2 = findChildOfClass(goExpr, GoPsiTypeParenthesized.class);
                if (element2 != null) {
                    element2 = ((GoPsiTypeParenthesized) element2).getInnerType();
                    return CompairTypes(element, element2, null);
                }
            }
            if (goExpr instanceof GoParenthesisedExpression) {
                goExpr = ((GoParenthesisedExpression) goExpr).getInnerExpression();
            }
            if (goExpr instanceof GoLiteralExpression) {
                goExpr = ((GoLiteralExpression) goExpr).getLiteral();
            }
            if (goExpr instanceof GoLiteralIdentifier) {
                goExpr = ResolveReferece(goExpr);
                element2 = getFunctionDeclaration(goExpr);
                if (element2 == null) {
                    element2 = goExpr.getParent().getLastChild();
                    if (!(element2 instanceof GoFunctionDeclaration)) {
                        if (element2 instanceof GoLiteralExpression) {
                            GoType[] type = ((GoLiteralExpression) element2).getType();
                            if (type.length != 0 && type[0] instanceof GoTypePsiBacked)
                                return CompairTypes(element, type[0], (GoPsiElement) element2);
                            element2 = ((GoLiteralExpression) element2).getLiteral();
                            if (!(element2 instanceof GoFunctionDeclaration)) {
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }
                }
            }

            if (goExpr instanceof GoFunctionDeclaration)
                return CompairFnTypeToDecl((GoPsiTypeFunction) element, (GoFunctionDeclaration) goExpr);

            return element2 instanceof GoFunctionDeclaration && CompairFnTypeToDecl((GoPsiTypeFunction) element, (GoFunctionDeclaration) element2);
        }


        element2 = tryPsiType(element2);

        if (element2 instanceof GoPsiType) {
            GoTypeUtils.resolveToFinalType((GoPsiType) element2);
            return element.isIdentical((GoPsiType) element2);
        }

        return !(element2 instanceof GoType) || element.getUnderlyingType().isIdentical(((GoType) element2).getUnderlyingType());


    }


    private static boolean CompairFnTypeToDecl(GoPsiTypeFunction psiType, GoFunctionDeclaration functionDeclaration) {


        GoFunctionParameter[] funcTypeArguments = psiType.getParameters();

        GoFunctionParameter[] funcDeclArguments = functionDeclaration.getParameters();

        int idx = 0;
        if (funcDeclArguments.length != funcTypeArguments.length)
            return false;

        for (GoFunctionParameter parameter : funcDeclArguments) {
            if (!parameter.getType().isIdentical(funcTypeArguments[idx].getType()))
                return false;
            idx++;
        }
        funcTypeArguments = psiType.getResults();
        funcDeclArguments = functionDeclaration.getResults();

        if (funcDeclArguments.length != funcTypeArguments.length)
            return false;

        idx = 0;
        for (GoFunctionParameter parameter : funcDeclArguments) {
            if (!parameter.getType().isIdentical(funcTypeArguments[idx].getType()))
                return false;
            idx++;
        }
        return true;
    }

    /**
     * Helper method to generate function arguments type, based on param being passed to the function
     *
     * @param e -> parent -> GoCallOrConvExpression
     * @return the generated arugment list ex: arg0 int, arg1 string
     */
    public static String InspectionGenFuncArgs(PsiElement e) {
        StringBuilder stringBuilder = new StringBuilder();
        int arg = 0;
        final GoFile currentFile = (GoFile) e.getContainingFile();

        if (isFunctionNameIdentifier(e)) {

            for (GoExpr argument : ((GoCallOrConvExpression) e.getParent()).getArguments()) {
                if (arg != 0)
                    stringBuilder.append(',');

                stringBuilder.append("arg").append(arg).append(" ");
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
                        String type = getNameLocalOrGlobal(((GoTypePsiBacked) goType).getPsiType(), currentFile);
                        stringBuilder.append(type);
                    } else if (goType instanceof GoTypeArray) {
                         /*
                          * Using the psiType,
                          */
                        String type = getNameLocalOrGlobal(((GoTypeArray) goType).getPsiType(), currentFile);
                        stringBuilder.append(type);
                    } else if (firstChildExp instanceof GoLiteralFunction) {
                         /*
                          * Resolves the type of a function decl
                          * ex: the type of http.HandleFunc is func(string,func(http.ResponseWriter,*http.Request))
                          */
                        GoFunctionDeclaration functionDeclaration = (GoFunctionDeclaration) firstChildExp;
                        stringBuilder.append(getFuncDecAsParam(functionDeclaration.getParameters(), functionDeclaration.getResults(), currentFile));
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
                                stringBuilder.append(getFuncDecAsParam(fn.getParameters(), fn.getResults(), currentFile));
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

                                stringBuilder.append("arg").append(arg).append(' ');

                                final GoPsiType type1 = parameter1.getType();
                                if (type1 instanceof GoPsiTypePointer) {
                                    if (type1.getParent().getNode().getElementType().equals(GoElementTypes.FUNCTION_PARAMETER_VARIADIC))
                                        stringBuilder.append("...");
                                    stringBuilder.append('*').append(getNameLocalOrGlobal(((GoPsiTypePointer) type1).getTargetType(), currentFile));
                                } else {
                                    stringBuilder.append(getNameLocalOrGlobal(type1, currentFile));
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

    public static GoPsiElement ResolveTypeOfVarDecl(GoPsiElement element) {
        PsiElement parent = element.getParent();

        if (parent instanceof GoConstDeclaration) {
            GoPsiType identifiersType = ((GoConstDeclaration) parent).getIdentifiersType();
            if (identifiersType != null)
                return identifiersType;
            GoConstDeclarations parentDec = (GoConstDeclarations) parent.getParent();
            for (GoConstDeclaration declaration : parentDec.getDeclarations()) {
                identifiersType = declaration.getIdentifiersType();
                if (identifiersType != null)
                    return identifiersType;
            }
            return (GoPsiElement) parent.getLastChild();
        }
        if (parent instanceof GoVarDeclaration) {
            GoPsiType identifiersType = ((GoVarDeclaration) parent).getIdentifiersType();
            if (identifiersType != null)
                return identifiersType;
            return (GoPsiElement) parent.getLastChild();
        }
        if (element instanceof GoLiteralIdentifier) {
            for (PsiReference reference : element.getReferences()) {
                if (reference != null) {
                    GoPsiElement resolve = (GoPsiElement) reference.resolve();
                    if (resolve != null && !resolve.equals(element))
                        return ResolveTypeOfVarDecl(resolve);
                }
            }
        }
        return element;
    }
}

