package ro.redeul.google.go.lang.psi.types;

import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/27/11
 * Time: 12:18 AM
 */
public interface GoTypeStruct extends GoType {

    GoTypeStructField[] getFields();
}
