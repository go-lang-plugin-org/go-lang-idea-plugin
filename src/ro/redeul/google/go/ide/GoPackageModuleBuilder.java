package ro.redeul.google.go.ide;

import com.intellij.execution.CantRunException;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.actions.GoTemplatesFactory;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.ide.ui.GoToolWindow;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 1/2/11
 * Time: 10:34 AM
 */
public class GoPackageModuleBuilder extends JavaModuleBuilder implements SourcePathsBuilder, ModuleBuilderListener {

    private static final Logger LOG = Logger.getInstance(GoPackageModuleBuilder.class);
    private static final String TITLE = "go get package";
    private String packageURL;

    public GoPackageModuleBuilder() {
        addListener(this);
    }

    @Override
    public void moduleCreated(@NotNull final Module module) {

        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        final VirtualFile sourceRoots[] = moduleRootManager.getSourceRoots();
        final String projectName = module.getProject().getName();


        GoProjectSettings.GoProjectSettingsBean settings = GoProjectSettings.getInstance(module.getProject()).getState();
        settings.prependSysGoPath = false;
        settings.appendSysGoPath = false;

        try {
            Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(module.getProject());
            if ( sdk == null ) {
                throw new CantRunException("No Go Sdk defined for this project");
            }

            final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
            if ( sdkData == null ) {
                throw new CantRunException("No Go Sdk defined for this project");
            }

            final String goExecName = sdkData.GO_EXEC;

            final String projectDir = module.getProject().getBasePath();

            if (projectDir == null) {
                throw new CantRunException("Could not retrieve the project directory");
            }

            Map<String, String> projectEnv = new HashMap<String, String>(System.getenv());
            String goRoot = GoSdkUtil.getSdkRootPath(sdkData);
            String goPath = projectDir;
            projectEnv.put("GOROOT", goRoot);
            projectEnv.put("GOPATH", goPath);
            projectEnv.put("PATH", GoSdkUtil.appendGoPathToPath(goRoot + File.pathSeparator + goPath));

            final GoToolWindow toolWindow = GoToolWindow.getInstance(module.getProject());
            toolWindow.setTitle(TITLE);

            final String packageName = this.packageURL;

            final String[] goEnv = GoSdkUtil.convertEnvMapToArray(projectEnv);

            final String[] command = GoSdkUtil.computeGoGetCommand(goExecName, "-u -v", packageName);

            Runnable r = new Runnable() {
                public void run() {
                    try {
                        Runtime rt = Runtime.getRuntime();
                        Process proc = rt.exec(command, goEnv, new File(projectDir));
                        OSProcessHandler handler = new OSProcessHandler(proc, null);
                        toolWindow.attachConsoleViewToProcess(handler);
                        toolWindow.printNormalMessage(String.format("%s%n", StringUtil.join(command, " ")));
                        toolWindow.show();

                        handler.startNotify();

                        if (proc.waitFor() == 0) {
                            //VirtualFileManager.getInstance().syncRefresh();
                            toolWindow.printNormalMessage(String.format("%nFinished go get package %s%n", packageName));

                        } else {
                            toolWindow.printErrorMessage(String.format("%nCould't go get package %s%n", packageName));
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Messages.showErrorDialog(String.format("Error while processing %s get command: %s.", goExecName, e.getMessage()), "Error on Google Go Plugin");
                    }
                }
            };

            new Thread(r).start();


        } catch (CantRunException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ModuleType getModuleType() {
        return GoPackageModuleType.getInstance();
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return sdkType instanceof GoSdkType;
    }

    public void setPackageURL(String packageURL) {
        this.packageURL = packageURL;
    }
}
