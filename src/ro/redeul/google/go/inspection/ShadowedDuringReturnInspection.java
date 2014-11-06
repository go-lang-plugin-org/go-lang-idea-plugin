package ro.redeul.google.go.inspection;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.*;

import static ro.redeul.google.go.lang.psi.utils.GoFunctionDeclarationUtils.*;


public class ShadowedDuringReturnInspection extends AbstractWholeGoFileInspection {
    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {

        new GoRecursiveElementVisitor() {
            @Override
            public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
                visitElement(declaration);
                checkFunction(result, declaration);
            }

            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
                visitElement(declaration);
                checkFunction(result, declaration);
            }

            @Override
            public void visitFunctionLiteral(GoLiteralFunction literal) {
                visitElement(literal);
                checkFunction(result, literal);
            }
        }.visitFile(file);
    }

    public static void checkFunction(InspectionResult result, GoFunctionDeclaration function) {
        checkShadowedDuringReturn(result, function);
    }

    private static void checkShadowedDuringReturn(InspectionResult result, GoFunctionDeclaration function) {
        // function have return parameters can cause shadowed during return
        if (!hasResult(function)){
            return;
        }

        // return parameters must have name can cause shadowed during return
        if (!IsResultNamed(function)){
            return;
        }

        new ReturnVisitor(result, function).visitFunctionDeclaration(function);
    }

    /**
     * Recursively look for VarDeclaration can cuase shadowed during return,
     * Recursively look for GoReturnStatement which can cuase shadowed during return,
     * 1.VarDeclaration must in direct parent blockStat childrens.
     * 2.VarDeclaration can not in topLevel block of function.
     * 3.VarDeclaration must appear before that GoReturnStatement.
     * 4.VarDeclaration Identifier must equal some function ResultParameterNames.
     */
    private static class ReturnVisitor extends GoRecursiveElementVisitor {
        private final InspectionResult result;
        private GoFunctionDeclaration functionDeclaration;
        private HashSet<GoVarDeclaration> currentShadowedVarDeclarationSet = new HashSet<GoVarDeclaration>();
        private Stack<HashSet<GoVarDeclaration>> currentBlockVarDeclarationStack = new Stack<HashSet<GoVarDeclaration>>();
        private boolean isInFunctionBlock = true;

        public ReturnVisitor(InspectionResult result, GoFunctionDeclaration declaration) {
            this.result = result;
            this.functionDeclaration = declaration;
        }

        @Override
        public void visitFunctionLiteral(GoLiteralFunction literal) {
            // stop the recursion here
        }

        @Override
        public void visitBlockStatement(GoBlockStatement statement) {
            boolean oldIsInFunctionBlock = isInFunctionBlock;
            isInFunctionBlock = functionDeclaration.getBlock()==statement;
            currentBlockVarDeclarationStack.push(new HashSet<GoVarDeclaration>());
            super.visitBlockStatement(statement);
            HashSet<GoVarDeclaration> currentblockStack = currentBlockVarDeclarationStack.pop();
            currentShadowedVarDeclarationSet.removeAll(currentblockStack);
            isInFunctionBlock = oldIsInFunctionBlock;
        }

        @Override
        public void visitVarDeclaration(GoVarDeclaration declaration) {
            if (isInFunctionBlock){
                return;
            }
            currentShadowedVarDeclarationSet.add(declaration);
            currentBlockVarDeclarationStack.peek().add(declaration);
        }

        public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
            visitVarDeclaration(declaration);
        }

        @Override
        public void visitReturnStatement(GoReturnStatement statement) {
            // return statement do not have any expressions can cause shadowed during return
            if (statement.getExpressions().length>0) {
                return;
            }
            List<String> varDeclarationNames = getShadowedVarDeclarationNames();
            ArrayList<String> shadowedResultNames = new ArrayList<String>();
            for(String resultName: getResultParameterNames(functionDeclaration)){
                if (varDeclarationNames.contains(resultName)){
                    shadowedResultNames.add(resultName);
                }
            }

            if (shadowedResultNames.size()>0){
                result.addProblem(statement, GoBundle.message("error.shadowed.during.return", stringJoin(shadowedResultNames) ));
            }
        }

        private List<String> getShadowedVarDeclarationNames(){
            ArrayList<String> output = new ArrayList<String>();
            for(GoVarDeclaration varDeclaration: currentShadowedVarDeclarationSet){
                for(GoLiteralIdentifier ids : varDeclaration.getIdentifiers()){
                    output.add(ids.getName());
                }
            }
            return output;
        }

        private String stringJoin(List<String> input){
            String output = Arrays.toString(input.toArray());
            return output.substring(1,output.length()-1);
        }

    }
}
