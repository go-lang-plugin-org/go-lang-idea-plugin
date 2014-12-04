package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.typing.GoTypeInterface;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.typing.TypeVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 21, 2010
 * Time: 4:47:46 AM
 */
public class TypeNameSolver extends VisitingReferenceSolver<TypeNameReference, TypeNameSolver> {

    @Override
    public TypeNameSolver self() {
        return this;
    }

    public TypeNameSolver(final TypeNameReference reference, final boolean methodReceiver) {

        final TypeVisitor<Boolean> visitor = new TypeVisitor<Boolean>(true) {
            @Override
            public Boolean visitInterface(GoTypeInterface type) {
                return false;
            }

            @Override
            public Boolean visitPointer(GoTypePointer type) {
                return false;
            }
        };

        solveWithVisitor(
                new ReferenceSolvingVisitor(this, reference) {
                    @Override
                    public void visitTypeSpec(GoTypeSpec type) {
                        if (checkReference(type.getTypeNameDeclaration())) {
                            addTarget(type.getTypeNameDeclaration());
                        }
                    }

                    @Override
                    public void visitImportDeclaration(GoImportDeclaration declaration) {
                        if ( matchNames(reference.name(), declaration.getPackage().getName()) )
                            addTarget(declaration);
                    }

                    boolean checkReference(GoTypeNameDeclaration declaration) {
                        if (declaration == null || declaration.getName() == null)
                            return false;

                        if (!methodReceiver)
                            return matchNames(reference.name(), declaration.getName());


                        return GoTypes.fromPsi(declaration).underlyingType().accept(visitor) && matchNames(reference.name(), declaration.getName());
                    }
                }
        );
    }
}
