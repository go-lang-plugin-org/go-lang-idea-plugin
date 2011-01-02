package ro.redeul.google.go.ide;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.Sdk;
import ro.redeul.google.go.config.sdk.GoSdkType;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: 1/2/11
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoModuleBuilder extends JavaModuleBuilder implements SourcePathsBuilder {

    @Override
    public ModuleType getModuleType() {
        return GoModuleType.getInstance();
    }

    @Override
    public boolean isSuitableSdk(Sdk sdk) {
        return sdk.getSdkType() == GoSdkType.getInstance();
    }
}
