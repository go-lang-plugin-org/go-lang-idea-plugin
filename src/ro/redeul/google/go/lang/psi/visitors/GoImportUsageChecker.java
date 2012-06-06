package ro.redeul.google.go.lang.psi.visitors;

import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.types.GoTypeName;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/15/11
 * Time: 8:20 AM
 */
public class GoImportUsageChecker extends GoRecursiveElementVisitor {

    private GoImportDeclaration importSpec;
    private boolean isUsed = false;
    String importPrefix = "";

    public GoImportUsageChecker(GoImportDeclaration importSpec) {
        this.importSpec = importSpec;

        String visiblePackageName = importSpec.getVisiblePackageName();
        if ( visiblePackageName.length() != 0 ) {
            visiblePackageName += ".";
        }

        importPrefix = visiblePackageName;
    }


    @Override
    public void visitLiteralExpr(GoLiteral literalExpr) {
        GoIdentifier goIdentifier = literalExpr.getIdentifier();
        if ( goIdentifier == null ) {
            return;
        }

        String identifierText = goIdentifier.getText();

        if ( identifierText.startsWith(this.importPrefix) ) {
            isUsed = true;
        }
    }

    @Override
    public void visitTypeName(GoTypeName typeName) {
        if ( typeName.getPackageReference() == null ) {
            return;
        }

        if ( typeName.getPackageReference().getString().equals(importSpec.getVisiblePackageName()) ) {
            isUsed = true;
        }
    }

    public boolean isUsed() {
        return isUsed ||
                importSpec.getPackageReference() != null &&
                        (importSpec.getPackageReference().isLocal() || importSpec.getPackageReference().isBlank());
    }
}
