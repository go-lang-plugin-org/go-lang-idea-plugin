package ro.redeul.google.go.lang.resolve;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.ResolveTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 8, 2010
 * Time: 2:55:00 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GoResolveTestCase extends ResolveTestCase {

    protected Method currentRunningTestMethod;

    @BeforeMethod
    public void before(Method method) throws Throwable {
        this.currentRunningTestMethod = method;
        this.setUp();
    }

    @Override
    protected String getTestName(boolean lowercaseFirstLetter) {
        return getTestName();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        super.tearDown();
    }

    protected String getRelativeDataPath() {
        return "testdata/resolve/";
    }

    protected PsiReference configure() throws Exception {
        return configureByFile(getTestName() + ".go");
    }

    protected String getTestName() {
        String name = currentRunningTestMethod.getName();

        name = StringUtil.trimStart(name, "test");
        if (StringUtil.isEmpty(name)) {
            return getRelativeDataPath();
        }

        String processedName = getRelativeDataPath() + File.separator;
        boolean isFirst = true;

        for (String s : name.split("(?<=\\p{Lower})(?=\\p{Upper})")) {
            if (isFirst) {
                processedName += s.toLowerCase();
                isFirst = false;
            } else {
                processedName += File.separator + s.toLowerCase();
            }
        }

        return processedName;
    }

}
