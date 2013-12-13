package ro.redeul.google.go.lang.psi.types;

import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;

public interface GoPsiTypeFunction extends GoPsiType {
    GoFunctionParameter[] getParameters();

    GoFunctionParameter[] getResults();
}
