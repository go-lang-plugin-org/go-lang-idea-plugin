package ro.redeul.google.go.lang.psi.statements.switches;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public interface GoSwitchTypeClause extends GoPsiElement {

    boolean isDefault();

    @NotNull
    GoPsiType[] getTypes();

    @NotNull
    GoStatement[] getStatements();
}
