package ro.redeul.google.go.lang.resolve;

import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 8, 2010
 * Time: 2:59:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoTypeResolvingTestCase extends GoResolveTestCase {

    @Test
    public void testLocalTypeResolving() throws Exception {
        PsiReference reference = configure();
    }
}
