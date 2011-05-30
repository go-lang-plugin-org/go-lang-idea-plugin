package ro.redeul.google.go.lang.psi.types;

import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/27/11
 * Time: 12:18 AM
 */
public interface GoTypeStruct extends GoType {

    GoTypeStructField[] getFields();

    GoTypeStructAnonymousField[] getAnonymousFields();
}
