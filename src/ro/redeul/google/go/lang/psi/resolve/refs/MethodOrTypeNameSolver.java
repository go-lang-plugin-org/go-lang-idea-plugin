package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

public class MethodOrTypeNameSolver extends VisitingReferenceSolver<MethodOrTypeNameReference, MethodOrTypeNameSolver> {

    @Override
    public MethodOrTypeNameSolver self() { return this; }

    public MethodOrTypeNameSolver(MethodOrTypeNameReference reference) {
        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {
            @Override
            public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
                System.out.println("Declaration: " + declaration);
                if (isReferenceTo(declaration))
                    addTarget(declaration);
            }

            private boolean isReferenceTo(GoFunctionDeclaration declaration) {
                return matchNames(referenceName(), declaration.getFunctionName());
            }

            @Override
            public void visitTypeSpec(GoTypeSpec type) {

//                if (ResolveStates.get(getState(), ResolveStates.Key.IsPackageBuiltin)) {
//                    String typeName = type.getName();
//                    GoPsiType typeDeclaration = type.getType();
//                    if (typeName != null && typeDeclaration != null) {
//                        if (!typeName.equals(typeDeclaration.getText()))
//                            return;
//                    }
//                }

                if (isReferenceTo(type.getTypeNameDeclaration()))
                    addTarget(type);
            }

            private boolean isReferenceTo(GoTypeNameDeclaration declaration) {
                String name = declaration.getName();
                return name != null && matchNames(referenceName(), name);
            }

//            @Override
//            public void visitVarDeclaration(GoVarDeclaration declaration) {
//                if (checkReference(declaration))
//                    addTarget(declaration);
//            }
//
            @Override
            public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
                GoLiteralIdentifier ids[] = declaration.getDeclarations();
                checkIdentifiers(referenceName(), ids);
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
