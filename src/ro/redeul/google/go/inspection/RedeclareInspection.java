package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
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
                visitElement(file);
                blockNameStack.pop();
            }
            @Override
            public void visitIfStatement(GoIfStatement statement){
                blockNameStack.push(new HashSet<String>());
                visitElement(statement);
                blockNameStack.pop();
            }
            @Override
            public void visitForWithRange(GoForWithRangeStatement statement) {
                blockNameStack.push(new HashSet<String>());
                visitElement(statement);
                blockNameStack.pop();
            }
            @Override
            public void visitForWithRangeAndVars(GoForWithRangeAndVarsStatement statement) {
                blockNameStack.push(new HashSet<String>());
                visitElement(statement);
                blockNameStack.pop();
            }
            @Override
            public void visitForWithClauses(GoForWithClausesStatement statement) {
                blockNameStack.push(new HashSet<String>());
                visitElement(statement);
                blockNameStack.pop();
            }
            @Override
            public void visitInterfaceType(GoPsiTypeInterface type) {
                blockNameStack.push(new HashSet<String>());
                visitElement(type);
                blockNameStack.pop();
            }

            @Override
            public void visitBlockStatement(GoBlockStatement statement) {
                blockNameStack.push(new HashSet<String>());
                visitElement(statement);
                blockNameStack.pop();
            }

            @Override
            public void visitSwitchExpressionClause(GoSwitchExpressionClause statement) {
                blockNameStack.push(new HashSet<String>());
                visitElement(statement);
                blockNameStack.pop();
            }

            @Override
            public void visitSwitchTypeClause(GoSwitchTypeClause statement) {
                blockNameStack.push(new HashSet<String>());
                visitElement(statement);
                blockNameStack.pop();
            }

            @Override
            public void visitSwitchExpressionStatement(GoSwitchExpressionStatement statement) {
                blockNameStack.push(new HashSet<String>());
                visitElement(statement);
                blockNameStack.pop();
            }

            @Override
            public void visitSwitchTypeGuard(GoSwitchTypeGuard typeGuard) {
                blockNameStack.push(new HashSet<String>());
                visitElement(typeGuard);
                blockNameStack.pop();
            }

            @Override
            public void visitSwitchTypeStatement(GoSwitchTypeStatement statement) {
                blockNameStack.push(new HashSet<String>());
                visitElement(statement);
                blockNameStack.pop();
            }

            @Override
            public void visitSelectStatement(GoSelectStatement statement) {
                blockNameStack.push(new HashSet<String>());
                visitElement(statement);
                blockNameStack.pop();
            }

            @Override
            public void visitSelectCommClauseDefault(GoSelectCommClauseDefault commClause) {
                blockNameStack.push(new HashSet<String>());
                visitElement(commClause);
                blockNameStack.pop();
            }

            @Override
            public void visitSelectCommClauseRecv(GoSelectCommClauseRecv commClause) {
                blockNameStack.push(new HashSet<String>());
                visitElement(commClause);
                blockNameStack.pop();
            }
            @Override
            public void visitSelectCommClauseSend(GoSelectCommClauseSend commClause) {
                blockNameStack.push(new HashSet<String>());
                visitElement(commClause);
                blockNameStack.pop();
            }

            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
                super.visitMethodDeclaration(declaration);
                if (declaration.getMethodReceiver()==null ||
                        declaration.getMethodReceiver().getType()==null){
                    return;
                }
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
                super.visitFunctionDeclaration(declaration);
                String name = declaration.getName();
                if (name==null || name.equals("init")){
                    return;
                }
                nameCheck(declaration.getNameIdentifier(), name, result);
            }

            @Override
            public void visitTypeNameDeclaration(GoTypeNameDeclaration declaration) {
                super.visitTypeNameDeclaration(declaration);
                nameCheck(declaration, declaration.getName(), result);
            }

            @Override
            public void visitConstDeclaration(GoConstDeclaration declaration) {
                GoLiteralIdentifier[] ids = declaration.getIdentifiers();
                for(GoLiteralIdentifier id: ids){
                    nameCheck(id,id.getUnqualifiedName(),result);
                }
            }

            @Override
            public void visitVarDeclaration(GoVarDeclaration declaration) {
                GoLiteralIdentifier[] ids = declaration.getIdentifiers();
                for(GoLiteralIdentifier id: ids){
                    nameCheck(id,id.getUnqualifiedName(),result);
                }
            }

            @Override
            public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
                GoLiteralIdentifier[] ids = declaration.getIdentifiers();
                boolean isAllRepeat = true;
                for(GoLiteralIdentifier id: ids){
                    if (!blockNameStack.peek().contains(id.getUnqualifiedName())) {
                        isAllRepeat = false;
                        blockNameStack.peek().add(id.getUnqualifiedName());
                    }
                }
                if (isAllRepeat){
                    result.addProblem(ids[0],ids[ids.length-1], GoBundle.message("error.no.new.variables.on.left.side"),
                            ProblemHighlightType.GENERIC_ERROR);
                }
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
