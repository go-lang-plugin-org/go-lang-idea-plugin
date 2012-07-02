package ro.redeul.google.go;

import java.io.File;
import java.io.IOException;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.FilteringProcessor;
import com.intellij.util.Processor;

public abstract class GoFileBasedPsiTestCase extends GoPsiTestCase {
    protected void removeContentRoots() {
        new WriteCommandAction.Simple(myModule.getProject()) {
            @Override
            protected void run() throws Throwable {
                ModuleRootManager instance =
                    ModuleRootManager.getInstance(myModule);

                ModifiableRootModel modifiableModel = instance.getModifiableModel();

                ContentEntry[] entries = instance.getContentEntries();
                for (ContentEntry entry : entries) {
                    modifiableModel.removeContentEntry(entry);
                }
                modifiableModel.commit();
            }
        }.execute().throwException();
    }

    protected void doTest() throws Exception {
        final String fullPath =
            (getTestDataPath() + getTestName(false))
                .replace(File.separatorChar, '/');

        VirtualFile vFile;

        vFile = LocalFileSystem.getInstance().findFileByPath(fullPath + ".go");

        File dir = createTempDirectory();
        VirtualFile vModuleDir =
            LocalFileSystem.getInstance()
                           .refreshAndFindFileByPath(
                               dir.getCanonicalPath()
                                  .replace(File.separatorChar, '/'));

        VirtualFile builtin =
            LocalFileSystem.getInstance()
                           .findFileByPath(
                               getTestDataPath() + "/builtin.go");

        if (builtin != null) {
            parseFile(builtin,
                      LocalFileSystem.getInstance()
                                     .findFileByPath(
                                         getTestDataPath()),
                      vModuleDir);
        }

        if (vFile != null) {
            doSingleFileTest(vFile, vModuleDir);
            removeContentRoots();
            return;
        }

        vFile = LocalFileSystem.getInstance().findFileByPath(fullPath);
        if (vFile != null && vFile.isDirectory()) {
            doDirectoryTest(vFile, vModuleDir);
            removeContentRoots();
            return;
        }

        fail("no test files found in \"" + vFile + "\"");
    }

    private void doSingleFileTest(VirtualFile vFile, VirtualFile vModuleDir)
        throws Exception {
        parseFile(vFile, vFile.getParent(), vModuleDir);

        assertTest();
    }

    private void doDirectoryTest(final VirtualFile vFile,
                                 final VirtualFile vModuleDir)
        throws IOException {
        VfsUtil.processFilesRecursively(
            vFile,
            new FilteringProcessor<VirtualFile>(
                new Condition<VirtualFile>() {
                    @Override
                    public boolean value(VirtualFile virtualFile) {
                        return !virtualFile.isDirectory() &&
                            virtualFile.getName().endsWith(".go");
                    }
                },
                new Processor<VirtualFile>() {
                    @Override
                    public boolean process(VirtualFile virtualFile) {
                        parseFile(virtualFile, vFile, vModuleDir);
                        return true;
                    }
                }
            )
        );

        assertTest();
    }

    protected void parseFile(VirtualFile file, VirtualFile root,
                             VirtualFile vModuleRoot) {

        String folder = VfsUtil.getRelativePath(file.getParent(), root, '/');

        VirtualFile folderFile = root.findFileByRelativePath(folder);
        try {
            String fileContent =
                StringUtil.convertLineSeparators(VfsUtil.loadText(file));

            PsiFile psiFile = createFile(myModule, folderFile, file.getName(),
                                         fileContent);

            postProcessFilePsi(psiFile, fileContent);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    protected abstract void postProcessFilePsi(PsiFile psiFile,
                                               String fileContent);

    protected abstract void assertTest();
}
