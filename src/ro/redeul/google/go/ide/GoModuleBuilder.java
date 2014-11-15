package ro.redeul.google.go.ide;

import com.intellij.execution.CantRunException;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
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
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.actions.GoTemplatesFactory;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.ide.ui.GoToolWindow;
import ro.redeul.google.go.runner.GoApplicationConfiguration;
import ro.redeul.google.go.runner.GoApplicationConfigurationType;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.Map;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 1/2/11
 * Time: 10:34 AM
 */
public class GoModuleBuilder extends JavaModuleBuilder implements SourcePathsBuilder, ModuleBuilderListener {

    private static final Logger LOG = Logger.getInstance(GoModuleBuilder.class);
    private static final String TITLE = "go get package";
    public String packageURL;
    public String packageName;
    public boolean isNew;
    public GoProjectSettings.GoProjectSettingsBean settings;


    public GoModuleBuilder() {
        addListener(this);
        settings = new GoProjectSettings.GoProjectSettingsBean();
    }

    @Override
    public void moduleCreated(@NotNull final Module module) {

        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        final VirtualFile sourceRoots[] = moduleRootManager.getSourceRoots();
        final String projectName = module.getProject().getName();
        GoProjectSettings.getInstance(module.getProject()).loadState(settings);

        try {
            Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(module.getProject());
            if ( sdk == null ) {
                throw new CantRunException("No Go Sdk defined for this project");
            }
            final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
            if ( sdkData == null ) {
                throw new CantRunException("No Go Sdk defined for this project");
            }
            final String goExecName = GoSdkUtil.getGoExecName(sdk);
            if (goExecName == null) {
                return;
            }
            final String projectDir = module.getProject().getBasePath();
            if (projectDir == null) {
                throw new CantRunException("Could not retrieve the project directory");
            }
            final GoToolWindow toolWindow = GoToolWindow.getInstance(module.getProject());
            toolWindow.setTitle(TITLE);
            toolWindow.showAndCreate(module.getProject());

            final String packageDir;

            if (isNew) {
                //Create folders bin and pkg and add main.go to src folder:
                PsiDirectory srcDir = PsiManager.getInstance(module.getProject()).findDirectory(sourceRoots[0]);
                if (srcDir == null) {
                    return;
                }

                PsiDirectory baseDir = srcDir.getParentDirectory();
                if (baseDir == null) {
                    return;
                }

                baseDir.createSubdirectory("bin");
                baseDir.createSubdirectory("pkg");

                String packageNameLeafTemp = packageName;
                PsiDirectory currentDir = srcDir;
                if (packageName.contains("/") || packageName.contains("\\")) {
                    String[] parts = packageName.split("[\\\\/]");
                    for (int i = 0; i < parts.length-1; ++i) {
                        currentDir = currentDir.createSubdirectory(parts[i]);
                    }
                    packageNameLeafTemp = parts[parts.length-1];
                }

                final String packageNameLeaf = packageNameLeafTemp;
                final PsiDirectory mainPackage = currentDir.createSubdirectory(packageNameLeaf);
                packageDir = mainPackage.getVirtualFile().getPath();

                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        String mainFileName = projectName.concat(".go");
                        mainPackage.checkCreateFile(mainFileName);
                        GoTemplatesFactory.createFromTemplate(mainPackage, "main", "main", mainFileName, GoTemplatesFactory.Template.GoAppMain);
                        toolWindow.printNormalMessage(String.format("%nFinished creating package %s from template.%n", packageName));
                        sourceRoots[0].refresh(true, true);
                    }
                });

            } else {
                packageDir = sourceRoots[0].getCanonicalPath() + File.separatorChar + packageURL;

                Runnable r = new Runnable() {
                    public void run() {

                        try {
                            //Download a go package and the dependencies from URL:
                            Map<String, String> projectEnv = GoSdkUtil.getExtendedSysEnv(sdkData, projectDir, "", false, false);
                            String[] goEnv = GoSdkUtil.convertEnvMapToArray(projectEnv);
                            String[] command = GoSdkUtil.computeGoGetCommand(goExecName, "-u -v", packageURL);
                            Runtime rt = Runtime.getRuntime();
                            Process proc = rt.exec(command, goEnv, new File(projectDir));
                            OSProcessHandler handler = new OSProcessHandler(proc, null);
                            toolWindow.attachConsoleViewToProcess(handler);
                            toolWindow.printNormalMessage(String.format("%s%n", StringUtil.join(command, " ")));
                            toolWindow.show();
                            handler.startNotify();
                            if (proc.waitFor() == 0) {
                                toolWindow.printNormalMessage(String.format("%nFinished go get package %s%n", packageURL));
                            } else {
                                toolWindow.printErrorMessage(String.format("%nCould't go get package %s%n", packageURL));
                            }
                            sourceRoots[0].refresh(true, true);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Messages.showErrorDialog(String.format("Error while processing go get command: %s.", e.getMessage()), "Error on Google Go Plugin");
                            LOG.error(e.getMessage());
                        }
                    }
                };
                new Thread(r).start();

            }

            final String configName = (new File(packageDir)).getName();

            // Now add a new run configuration:
            RunManager runManager = RunManager.getInstance(module.getProject());
            GoApplicationConfigurationType goAppConfigType = new GoApplicationConfigurationType();
            GoApplicationConfigurationType.GoFactory goConfigFactory = new GoApplicationConfigurationType.GoFactory(new GoApplicationConfigurationType());
            GoApplicationConfiguration configuration = new GoApplicationConfiguration("name", module.getProject(), goAppConfigType);
            configuration.runPackage = true;
            configuration.packageDir = packageDir;
            configuration.workingDir = module.getProject().getBasePath();
            configuration.goOutputDir = module.getProject().getBasePath() + File.separatorChar + "bin";
            configuration.goBuildBeforeRun = true;
            configuration.runBuilderArguments = "";
            configuration.runExecutableName = configName;
            configuration.scriptName = "";
            configuration.scriptArguments = "";
            configuration.autoStartGdb = true;
            configuration.GDB_PATH = "gdb";
            configuration.debugBuilderArguments = "-gcflags \"-N -l\"";
            configuration.setModule(module);
            configuration.setName(configName);
            RunnerAndConfigurationSettings runAndConfig = runManager.createConfiguration(configuration, goConfigFactory);
            runManager.addConfiguration(runAndConfig, false);
            runManager.setSelectedConfiguration(runAndConfig);

            toolWindow.showAndCreate(module.getProject());


        } catch (CantRunException e) {
            e.printStackTrace();
        }


    }

    @Override
    public ModuleType getModuleType() {
        return GoModuleType.getInstance();
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return sdkType instanceof GoSdkType;
    }

}
