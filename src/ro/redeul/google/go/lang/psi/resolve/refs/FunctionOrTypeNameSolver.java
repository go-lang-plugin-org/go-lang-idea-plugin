package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;

public class FunctionOrTypeNameSolver extends VisitingReferenceSolver<FunctionOrTypeNameReference, FunctionOrTypeNameSolver> {

    @Override
    public FunctionOrTypeNameSolver self() { return this; }

    public FunctionOrTypeNameSolver(final FunctionOrTypeNameReference reference) {
        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {

            @Override
            public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
                if (isReferenceTo(declaration))
                    addTarget(declaration);
            }

            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
                //
            }

            private boolean isReferenceTo(GoFunctionDeclaration declaration) {
                return matchNames(reference.name(), declaration.getFunctionName());
            }

            @Override
            public void visitTypeSpec(GoTypeSpec type) {
                if (isReferenceTo(type.getTypeNameDeclaration()))
                    addTarget(type);
            }

            private boolean isReferenceTo(GoTypeNameDeclaration declaration) {
                String name = declaration.getName();
                return name != null && matchNames(reference.name(), name);
            }

            @Override
            public void visitVarDeclaration(GoVarDeclaration declaration) {
                checkIdentifiers(reference.name(), declaration.getIdentifiers());
            }

            @Override
            public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
                GoLiteralIdentifier ids[] = declaration.getDeclarations();
                checkIdentifiers(reference.name(), ids);
            }

            @Override
            public void visitFunctionParameter(GoFunctionParameter parameter) {
                if ( parameter.getType() instanceof GoPsiTypeFunction )
                    checkIdentifiers(reference.name(), parameter.getIdentifiers());
            }

            //            private boolean checkVarDeclaration(GoShortVarDeclaration declaration) {
//                declaration.getIdentifiersType();
//                return false;
//            }

//            @Override
//            public void visitFunctionParameter(GoFunctionParameter parameter) {
//                if (!(parameter.getType() instanceof GoPsiTypeFunction)) {
//                    return;
//                }
//
//                for (GoLiteralIdentifier identifier : parameter.getIdentifiers()) {
//                    if (!checkReference(identifier)) {
//                        continue;
//                    }
//
//                    if (!addTarget(identifier)) {
//                        return;
//                    }
//                }
//            }

        });
    }
}
