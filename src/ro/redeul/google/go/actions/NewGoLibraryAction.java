package ro.redeul.google.go.actions;

import com.intellij.facet.FacetManager;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.CreateTemplateInPackageAction;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModel;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.facet.GoFacetType;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 20, 2010
 * Time: 11:41:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewGoLibraryAction extends CreateTemplateInPackageAction<GoFile> implements DumbAware {

    public NewGoLibraryAction() {
        super(GoBundle.message("new.go.lib"), GoBundle.message("new.go.lib.description"), GoIcons.GO_ICON_16x16, true);
    }

    @Override
    protected PsiElement getNavigationElement(@NotNull GoFile file) {
        return file;
    }

    @Override
    protected boolean checkPackageExists(PsiDirectory directory) {
        return true;
    }

    @Override
    protected boolean isAvailable(DataContext dataContext) {
        return super.isAvailable(dataContext)
                && hasGoFacet(DataKeys.MODULE.getData(dataContext));
    }

    private boolean hasGoFacet(Module module) {        
        return FacetManager.getInstance(module).getFacetByType(GoFacetType.GO_FACET_TYPE_ID) != null;
    }

    @Override
    protected void doCheckCreate(PsiDirectory dir, String className, String templateName) throws IncorrectOperationException {
        // check to see if a file with the same name already exists
        PsiFile files[] = dir.getFiles();
        for (PsiFile file : files) {
            if ( file.getFileType() == GoFileType.GO_FILE_TYPE && file.getVirtualFile().getNameWithoutExtension().equals(className) ) {
                throw new IncorrectOperationException();
            }
        }
    }

    @Override
    protected GoFile doCreate(PsiDirectory dir, String fileName, String templateName) throws IncorrectOperationException {
        GoTemplatesFactory.Template template = GoTemplatesFactory.Template.GoFile;

        if ( templateName.equals("multiple") ) {
            dir = dir.createSubdirectory(fileName);
        }
        
        return GoTemplatesFactory.createFromTemplate(dir, fileName, fileName + ".go", template);

    }

    @Override
    protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle("New Go file creation");

        boolean isLibraryFolder = isLibraryFolder(directory);
        
//        if ( && ! isLibraryFolder ) {
//            builder.addKind("Go library file", GoIcons.GO_ICON_16x16, "GoMainApplicationFile");
//            builder.addKind("Go library", GoIcons.GO_ICON_16x16, "GoMainApplicationFile");
//
//            builder.addKind("Go Application main file ", GoIcons.GO_ICON_16x16, "GoMainApplicationFile");
//            builder.addKind("Go Application file ", GoIcons.GO_ICON_16x16, "GoMainApplicationFile");
//        }

        builder.addKind("Multiple file library", GoIcons.GO_ICON_16x16, "multiple");
        builder.addKind("Single file library", GoIcons.GO_ICON_16x16, "single");
//        builder
//                .setTitle(IdeBundle.message("action.create.new.class"))
//                .addKind("Class", Icons.CLASS_ICON, JavaTemplateUtil.INTERNAL_CLASS_TEMPLATE_NAME)
//                .addKind("Interface", Icons.INTERFACE_ICON, JavaTemplateUtil.INTERNAL_INTERFACE_TEMPLATE_NAME);
//        if (LanguageLevelProjectExtension.getInstance(project).getLanguageLevel().compareTo(LanguageLevel.JDK_1_5) >= 0) {
//            builder.addKind("Enum", Icons.ENUM_ICON, JavaTemplateUtil.INTERNAL_ENUM_TEMPLATE_NAME);
//            builder.addKind("Annotation", Icons.ANNOTATION_TYPE_ICON, JavaTemplateUtil.INTERNAL_ANNOTATION_TYPE_TEMPLATE_NAME);
//        }
    }

    private boolean isLibraryFolder(PsiDirectory directory) {
        return false;
    }

    private boolean isApplicationFolder(PsiDirectory directory) {
        return false;
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return GoBundle.message("new.go.lib.action.text");
    }
}
