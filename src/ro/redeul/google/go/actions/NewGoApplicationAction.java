package ro.redeul.google.go.actions;

import com.intellij.ide.IdeView;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.CreateTemplateInPackageAction;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoIcons;

import static ro.redeul.google.go.actions.GoTemplatesFactory.Template;

public class NewGoApplicationAction extends CreateTemplateInPackageAction<PsiElement>
    implements DumbAware {

    public NewGoApplicationAction() {
        super(GoBundle.message("new.go.app"), GoBundle.message("new.go.app.description"), GoIcons.GO_ICON_16x16, JavaModuleSourceRootTypes.SOURCES);
    }

    @Override
    protected PsiElement getNavigationElement(@NotNull PsiElement file) {
        return file;
    }

    protected boolean checkPackageExists(PsiDirectory directory) {
        return true;
    }

    @NotNull
    protected CreateFileFromTemplateDialog.Builder buildDialog(Project project, PsiDirectory directory) {

        CreateFileFromTemplateDialog.Builder builder = CreateFileFromTemplateDialog.createDialog(project);

        buildDialog(project, directory, builder);

        return builder;
    }

    @Override
    protected String getErrorTitle() {
        return "Go application creation";
    }

    @Override
    protected boolean isAvailable(DataContext dataContext) {
        return
                super.isAvailable(dataContext)
                        && isNotIsSourceRoot(dataContext);
    }

    private boolean isNotIsSourceRoot(DataContext dataContext) {

        final Project project = PlatformDataKeys.PROJECT.getData(dataContext);
        final IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);

        if ( view == null )
            return false;

        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        for (PsiDirectory dir : view.getDirectories()) {

            VirtualFile dirVirtualFile = dir.getVirtualFile();

            if (projectFileIndex.isInSourceContent(dirVirtualFile)) {
                VirtualFile sourceRoot = projectFileIndex.getSourceRootForFile(dirVirtualFile);

                if (sourceRoot != null
                    && sourceRoot.getUrl().compareTo(dirVirtualFile.getUrl()) == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void doCheckCreate(PsiDirectory dir, String className) throws IncorrectOperationException {
        // check to see if a file with the same name already exists
        PsiFile files[] = dir.getFiles();
        for (PsiFile file : files) {
            if (file.getFileType() == GoFileType.INSTANCE &&
                file.getVirtualFile().getNameWithoutExtension().equals(className)) {
                throw new IncorrectOperationException();
            }
        }
    }

    @Override
    protected PsiElement doCreate(PsiDirectory dir, String fileName, String templateName) throws IncorrectOperationException {

        Template template = Template.GoFile;

        if (templateName.equalsIgnoreCase("main")) {
            template = Template.GoAppMain;
        }

        return GoTemplatesFactory.createFromTemplate(dir, "main", fileName + ".go", template);
    }

//    @Override
    protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
//        builder.setTitle("Go application creation");

//        boolean isApplicationFolder = isApplicationFolder(directory);

//        if (!isApplicationFolder) {
        builder.addKind("Go Application file ", GoIcons.GO_ICON_16x16, "main");
//        }

//        builder.addKind("Helper file", GoIcons.GO_ICON_16x16, "helper");
    }

    private boolean isLibraryFolder() {
        return false;
    }

    private boolean isApplicationFolder() {
        return false;
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return GoBundle.message("new.go.app.action.text");
    }
}
