package ro.redeul.google.go.lang.psi.resolve;

import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.references.TypeNameReference;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 21, 2010
 * Time: 4:47:46 AM
 */
public class TypeNameResolver extends
                              GoPsiReferenceResolver<TypeNameReference> {

    public TypeNameResolver(TypeNameReference reference) {
        super(reference);
    }

    @Override
    public void visitTypeSpec(GoTypeSpec type) {

        if (checkReference(type.getTypeNameDeclaration())){
            addDeclaration(type);
        }
    }
}
