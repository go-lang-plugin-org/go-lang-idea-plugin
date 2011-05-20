package ro.redeul.google.go.lang.psi.expressions;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOptsException;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 11:07 PM
 */
public interface GoLiteralExpression extends GoPsiExpression {

    GoIdentifier getIdentifier();

}
