package ro.redeul.google.go;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.util.FilteringProcessor;
import com.intellij.util.Processor;
import org.junit.Ignore;

@Ignore
public abstract class GoFileBasedPsiTestCase extends GoPsiTestCase {
    protected void doTest() throws Exception {
        final String fullPath =
                new File((getTestDataPath() + getTestName(false))
                .replace(File.separatorChar, '/')).getCanonicalPath();

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
                           .findFileByPath(getTestDataPath() + "/builtin.go");

        if (builtin != null) {
            parseFile(builtin,
                      LocalFileSystem.getInstance()
                                     .findFileByPath(getTestDataPath()),
                      vModuleDir);
        }

        if (vFile != null) {
            doSingleFileTest(vFile, vModuleDir);
            return;
        }

        vFile = LocalFileSystem.getInstance().findFileByPath(fullPath);
        if (vFile != null && vFile.isDirectory()) {
            doDirectoryTest(vFile, vModuleDir);
            return;
        }

        fail("no test files found in \"" + vFile + "\"");
    }

    private void doSingleFileTest(VirtualFile vFile, VirtualFile vModuleDir)
        throws Exception {
        files.clear();

        parseFile(vFile, vFile.getParent(), vModuleDir);

        for (Map.Entry<PsiFile, String> fileEntry : files.entrySet()) {
            postProcessFilePsi(fileEntry.getKey(), fileEntry.getValue());
        }

        assertTest();
    }


    protected PsiFile createFile(final Module module, final VirtualFile vDir, final String fileName, final String text)
        throws IOException {
        return new WriteAction<PsiFile>() {
            @Override
            protected void run(Result<PsiFile> result) throws Throwable {
                if (!ModuleRootManager.getInstance(module)
                                      .getFileIndex()
                                      .isInSourceContent(vDir)) {
                    addSourceContentToRoots(module, vDir);
                }

                final VirtualFile vFile = vDir.createChildData(vDir, fileName);
                VfsUtil.saveText(vFile, text);
                assertNotNull(vFile);
                final PsiFile file = myPsiManager.findFile(vFile);
                assertNotNull(file);
                result.setResult(file);
            }
        }.execute().getResultObject();
    }

    protected void addSourceContentToRoots(final Module module, final VirtualFile vDir) {
        new WriteAction<Void>() {
            @Override
            protected void run(Result<Void> result) throws Throwable {
                PsiTestUtil.addSourceContentToRoots(module, vDir);
            }
        }.execute();
    }

    private void doDirectoryTest(final VirtualFile file,
                                 VirtualFile moduleDir)
        throws IOException {
        files.clear();

        VfsUtil.createDirectoryIfMissing(moduleDir, file.getName());
        final VirtualFile vModuleDir = moduleDir.findOrCreateChildData(getProject(), file.getName());
        addSourceContentToRoots(myModule, vModuleDir);

        VfsUtil.processFilesRecursively(
            file,
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
                        parseFile(virtualFile, file, vModuleDir);
                        return true;
                    }
                }
            )
        );

        for (Map.Entry<PsiFile, String> fileEntry : files.entrySet()) {
            postProcessFilePsi(fileEntry.getKey(), fileEntry.getValue());
        }

        assertTest();
    }

    private Map<PsiFile, String> files = new HashMap<PsiFile, String>();

    protected void parseFile(VirtualFile file, VirtualFile root,
                             VirtualFile vModuleRoot) {

        String relativePath = VfsUtil.getRelativePath(file.getParent(), root,
                                                      '/');

        try {
            String fileContent =
                StringUtil.convertLineSeparators(VfsUtil.loadText(file));

            PsiFile psiFile =
                createFile(myModule,
                           VfsUtil.createDirectoryIfMissing(vModuleRoot,
                                                            relativePath),
                           file.getName(), fileContent);

            files.put(psiFile, fileContent);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    protected abstract void postProcessFilePsi(PsiFile psiFile,
                                               String fileContent);

    protected abstract void assertTest();
}
