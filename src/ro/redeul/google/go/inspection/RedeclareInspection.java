package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.HashSet;
import java.util.Stack;

public class RedeclareInspection extends AbstractWholeGoFileInspection {
    private Stack<HashSet<String>> blockNameStack = new Stack<HashSet<String>>();
    private HashSet<String> methodNameSet = new HashSet<String>();
    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitFile(GoFile file) {
                blockNameStack.push(new HashSet<String>());
                methodNameSet = new HashSet<String>();
                super.visitFile(file);
                blockNameStack.pop();
            }

            @Override
            public void visitBlockStatement(GoBlockStatement statement) {
                blockNameStack.push(new HashSet<String>());
                super.visitBlockStatement(statement);
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
                nameCheck(declaration.getNameIdentifier(), declaration.getName(), result);
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
        }.visitFile(file);
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
}
