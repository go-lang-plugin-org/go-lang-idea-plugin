package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 21, 2010
 * Time: 4:47:46 AM
 */
public class TypeNameSolver extends VisitingReferenceSolver<TypeNameReference, TypeNameSolver> {

    @Override
    public TypeNameSolver self() { return this; }

    public TypeNameSolver(final TypeNameReference reference, boolean methodReceiver) {
        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {
                 @Override
                 public void visitTypeSpec(GoTypeSpec type) {
                     if (checkReference(type.getTypeNameDeclaration())) {
                         addTarget(type.getTypeNameDeclaration());
                     }
                 }

                 boolean checkReference(GoTypeNameDeclaration declaration) {
                     if (declaration == null || declaration.getName() == null)
                         return false;

                     return matchNames(reference.name(), declaration.getName());
                 }
             }
        );
    }
}
