package ro.redeul.google.go.lang.psi.expressions.literals;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 4, 2010
 * Time: 10:42:42 PM
 */
public interface GoLiteralIdentifier extends GoLiteral<String>,
                                             PsiNameIdentifierOwner {
    GoLiteralIdentifier[] EMPTY_ARRAY = new GoLiteralIdentifier[0];

    boolean isBlank();

    boolean isIota();

    boolean isNil();

    BigInteger getIotaValue();

    public void setIotaValue(int value);

    @NotNull
    @Override
    String getName();
}
