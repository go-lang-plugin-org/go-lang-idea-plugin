package ro.redeul.google.go.lang.psi;

import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

public class GoPsiFunctionTestCase extends AbstractGoPsiTestCase {

    public void testNoParams() {
        GoFile file = get(parse("package main; func a() { }"));
        GoFunctionDeclaration func = get(file.getFunctions(), 0);

        assertEquals(func.getParameters().length, 0);
    }

    public void testOneParam() {
        GoFile file = get(parse("package main; func a(a int) { }"));
        GoFunctionDeclaration func = get(file.getFunctions(), 0);

        assertEquals(func.getParameters().length, 1);
    }

    public void testOneParamVariadic() {
        GoFile file = get(parse("package main; func a(a ...int) { }"));
        GoFunctionDeclaration func = get(file.getFunctions(), 0);

        assertEquals(func.getParameters().length, 1);
    }
}
