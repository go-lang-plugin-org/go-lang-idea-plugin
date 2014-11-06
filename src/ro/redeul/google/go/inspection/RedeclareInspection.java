package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.*;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseDefault;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseRecv;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseSend;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectStatement;
import ro.redeul.google.go.lang.psi.statements.switches.*;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

/**
 * find redeclare compiler error.
 */
public class RedeclareInspection extends AbstractWholeGoFileInspection {
    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {
        new GoRecursiveElementVisitor() {
            private Stack<HashSet<String>> blockNameStack = new Stack<HashSet<String>>();
            private HashSet<String> methodNameSet = new HashSet<String>();
            @Override
            public void visitFile(GoFile file) {
                blockNameStack.push(new HashSet<String>());
                methodNameSet = new HashSet<String>();
                //add names from other files in this package.
                Collection<GoFile> packageFiles = GoNamesCache.getInstance(file.getProject()).getFilesByPackageImportPath(file.getFullPackageName());
                for(GoFile file1:packageFiles){
                    if (file1==file){
                        continue;
                    }
                    for(GoFunctionDeclaration declaration:file1.getFunctions()){
                        blockNameStack.peek().add(declaration.getName());
                    }
                    for(GoVarDeclarations declarations:file1.getGlobalVariables()){
                        for(GoVarDeclaration declaration: declarations.getDeclarations()) {
                            for (GoLiteralIdentifier id : declaration.getIdentifiers()) {
                                blockNameStack.peek().add(id.getName());
                            }
                        }
                    }
                    for(GoConstDeclarations declarations:file1.getConsts()){
                        for(GoConstDeclaration declaration: declarations.getDeclarations()) {
                            for (GoLiteralIdentifier id : declaration.getIdentifiers()) {
                                blockNameStack.peek().add(id.getName());
                            }
                        }
                    }
                    for(GoTypeDeclaration declaration:file1.getTypeDeclarations()){
                        for(GoTypeSpec spec: declaration.getTypeSpecs()) {
                            GoTypeNameDeclaration names = spec.getTypeNameDeclaration();
                            if (names==null){
                                continue;
                            }
                            blockNameStack.peek().add(names.getName());
                        }
                    }
                    for(GoMethodDeclaration declaration:file1.getMethods()){
                        if (declaration.getMethodReceiver()==null ||
                                declaration.getMethodReceiver().getType()==null){
                            return;
                        }
                        String name = declaration.getMethodReceiver().getType().getText()+"."+declaration.getName();
                        if (name.startsWith("*")){
                            name = name.substring(1);
                        }
                        methodNameSet.add(name);
                    }
                }
                visitElement(file);
                blockNameStack.pop();
            }
            @Override
            public void visitIfStatement(GoIfStatement element){ visitSampleBlock(element); }

            @Override
            public void visitForWithRange(GoForWithRangeStatement element) { visitSampleBlock(element); }

            @Override
            public void visitForWithRangeAndVars(GoForWithRangeAndVarsStatement element) { visitSampleBlock(element); }

            @Override
            public void visitForWithClauses(GoForWithClausesStatement element) { visitSampleBlock(element); }

            @Override
            public void visitInterfaceType(GoPsiTypeInterface element) { visitSampleBlock(element); }

            @Override
            public void visitSwitchExpressionClause(GoSwitchExpressionClause element) { visitSampleBlock(element); }

            @Override
            public void visitSwitchTypeClause(GoSwitchTypeClause element) { visitSampleBlock(element); }

            @Override
            public void visitSwitchExpressionStatement(GoSwitchExpressionStatement element) { visitSampleBlock(element); }

            @Override
            public void visitSwitchTypeGuard(GoSwitchTypeGuard element) { visitSampleBlock(element); }

            @Override
            public void visitSwitchTypeStatement(GoSwitchTypeStatement element) { visitSampleBlock(element); }

            @Override
            public void visitSelectStatement(GoSelectStatement element) { visitSampleBlock(element); }

            @Override
            public void visitSelectCommClauseDefault(GoSelectCommClauseDefault element) { visitSampleBlock(element); }

            @Override
            public void visitSelectCommClauseRecv(GoSelectCommClauseRecv element) { visitSampleBlock(element); }
            @Override
            public void visitSelectCommClauseSend(GoSelectCommClauseSend element) { visitSampleBlock(element); }

            @Override
            public void visitBlockStatement(GoBlockStatement statement) {
                if (statement.getParent() instanceof GoFunctionDeclaration){
                    visitElement(statement);
                }else {
                    visitSampleBlock(statement);
                }
            }
            private void visitSampleBlock(GoPsiElement element){
                blockNameStack.push(new HashSet<String>());
                visitElement(element);
                blockNameStack.pop();
            }

            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
                if (declaration.getMethodReceiver()==null ||
                        declaration.getMethodReceiver().getType()==null){
                    visitElement(declaration);
                    return;
                }
                blockNameStack.push(new HashSet<String>());
                GoLiteralIdentifier id = declaration.getMethodReceiver().getIdentifier();
                nameCheck(id,id.getName(),result);
                for(GoFunctionParameter parameter: declaration.getParameters()){
                    for(GoLiteralIdentifier paramterId: parameter.getIdentifiers()){
                        nameCheck(paramterId,paramterId.getName(),result);
                    }
                }
                for(GoFunctionParameter parameter: declaration.getResults()){
                    for(GoLiteralIdentifier paramterId: parameter.getIdentifiers()){
                        nameCheck(paramterId,paramterId.getName(),result);
                    }
                }

                visitElement(declaration);
                blockNameStack.pop();
                String name = declaration.getMethodReceiver().getType().getText()+"."+declaration.getName();
                if (name.startsWith("*")){
                    name = name.substring(1);
                }
                if (methodNameSet.contains(name)){
                    result.addProblem(declaration.getNameIdentifier(), GoBundle.message("error.redeclare"),
                            ProblemHighlightType.GENERIC_ERROR);
                    return;
                }
                methodNameSet.add(name);
            }

            @Override
            public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
                blockNameStack.push(new HashSet<String>());
                for(GoFunctionParameter parameter: declaration.getParameters()){
                    for(GoLiteralIdentifier paramterId: parameter.getIdentifiers()){
                        nameCheck(paramterId,paramterId.getName(),result);
                    }
                }
                for(GoFunctionParameter parameter: declaration.getResults()){
                    for(GoLiteralIdentifier paramterId: parameter.getIdentifiers()){
                        nameCheck(paramterId,paramterId.getName(),result);
                    }
                }

                visitElement(declaration);
                blockNameStack.pop();
                String name = declaration.getName();
                if (name==null || name.equals("init")){
                    return;
                }
                nameCheck(declaration.getNameIdentifier(), name, result);
            }

            @Override
            public void visitFunctionLiteral(GoLiteralFunction declaration) {
                blockNameStack.push(new HashSet<String>());
                for(GoFunctionParameter parameter: declaration.getParameters()){
                    for(GoLiteralIdentifier paramterId: parameter.getIdentifiers()){
                        nameCheck(paramterId, paramterId.getName(),result);
                    }
                }
                for(GoFunctionParameter parameter: declaration.getResults()){
                    for(GoLiteralIdentifier paramterId: parameter.getIdentifiers()){
                        nameCheck(paramterId,paramterId.getName(),result);
                    }
                }
                visitElement(declaration);
                blockNameStack.pop();

            }


            @Override
            public void visitTypeNameDeclaration(GoTypeNameDeclaration declaration) {
                nameCheck(declaration, declaration.getName(), result);
                visitElement(declaration);
            }

            @Override
            public void visitConstDeclaration(GoConstDeclaration declaration) {
                GoLiteralIdentifier[] ids = declaration.getIdentifiers();
                for(GoLiteralIdentifier id: ids){
                    nameCheck(id,id.getName(),result);
                }
                visitElement(declaration);
            }

            @Override
            public void visitVarDeclaration(GoVarDeclaration declaration) {
                GoLiteralIdentifier[] ids = declaration.getIdentifiers();
                for(GoLiteralIdentifier id: ids){
                    nameCheck(id,id.getName(),result);
                }
                visitElement(declaration);
            }

            @Override
            public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
                GoLiteralIdentifier[] ids = declaration.getIdentifiers();
                boolean isAllRepeat = true;
                for(GoLiteralIdentifier id: ids){
                    if (!blockNameStack.peek().contains(id.getName())) {
                        isAllRepeat = false;
                        blockNameStack.peek().add(id.getName());
                    }
                }
                if (isAllRepeat){
                    result.addProblem(ids[0],ids[ids.length-1], GoBundle.message("error.no.new.variables.on.left.side"),
                            ProblemHighlightType.GENERIC_ERROR);
                }
                visitElement(declaration);
            }
            private void nameCheck(PsiElement errorElement,String name,InspectionResult result){
                if (name.equals("_")){
                    return;
                }
                if (blockNameStack.peek().contains(name)){
                    result.addProblem(errorElement, GoBundle.message("error.redeclare"),
                            ProblemHighlightType.GENERIC_ERROR);
                }
                blockNameStack.peek().add(name);
            }

        }.visitFile(file);
    }
}
