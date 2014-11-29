package ro.redeul.google.go.library;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.LibraryType;
import com.intellij.openapi.roots.libraries.NewLibraryConfiguration;
import com.intellij.openapi.roots.libraries.PersistentLibraryKind;
import com.intellij.openapi.roots.libraries.ui.LibraryEditorComponent;
import com.intellij.openapi.roots.libraries.ui.LibraryPropertiesEditor;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.LibraryEditor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoIcons;

import javax.swing.*;
import java.util.List;

/**
 * @author Florin Patan
 */
public class GoLibraryType extends LibraryType<GoLibraryProperties> implements GoLibrary {
    private static final PersistentLibraryKind<GoLibraryProperties> LIBRARY_KIND =
            new PersistentLibraryKind<GoLibraryProperties>(GO_LIBRARY_KIND_ID) {
                @NotNull
                @Override
                public GoLibraryProperties createDefaultProperties() {
                    return new GoLibraryProperties();
                }
            };

    protected GoLibraryType() {
        super(LIBRARY_KIND);
    }


    @NotNull
    @Override
    public String getCreateActionName() {
        return "New Go package";
    }

    @Override
    public NewLibraryConfiguration createNewLibrary(@NotNull JComponent jComponent, @Nullable VirtualFile virtualFile,
                                                    @NotNull Project project) {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createAllButJarContentsDescriptor();
        descriptor.setTitle(GoBundle.message("go.new.package.file.chooser.title"));
        descriptor.setDescription(GoBundle.message("go.new.package.file.chooser.description"));
        final VirtualFile[] files = FileChooser.chooseFiles(descriptor, project, virtualFile);

        if (files.length == 0) {
            return null;
        }
        return new NewLibraryConfiguration("Go package", this, new GoLibraryProperties()) {
            @Override
            public void addRoots(@NotNull LibraryEditor editor) {
                for (VirtualFile file : files) {
                    editor.addRoot(file, OrderRootType.CLASSES);
                }
            }
        };
    }

    @Override
    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @Override
    public LibraryPropertiesEditor createPropertiesEditor(@NotNull LibraryEditorComponent<GoLibraryProperties>
                                                                  libraryPropertiesLibraryEditorComponent) {

        return null;
    }

    @Override
    public GoLibraryProperties detect(@NotNull List<VirtualFile> classesRoots) {
        for (VirtualFile vf : classesRoots) {
            if (!vf.isDirectory())
                return null;

            for(VirtualFile file : vf.getChildren()) {
                String fileExtension = file.getExtension();
                if (fileExtension != null)
                    if (fileExtension.equals(GoFileType.DEFAULT_EXTENSION))
                        return new GoLibraryProperties();
            }
        }

        return null;
    }

}
