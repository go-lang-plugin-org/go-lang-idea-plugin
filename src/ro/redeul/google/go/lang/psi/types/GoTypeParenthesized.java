package ro.redeul.google.go.lang.psi.types;

public interface GoTypeParenthesized extends GoType {
    GoType getInnerType();
}
