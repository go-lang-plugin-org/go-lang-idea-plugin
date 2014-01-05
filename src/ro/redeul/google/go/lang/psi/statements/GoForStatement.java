package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;

public interface GoForStatement extends GoStatement, GoDocumentedPsiElement {
    GoBlockStatement getBlock();
}
