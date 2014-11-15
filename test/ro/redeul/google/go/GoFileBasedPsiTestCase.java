package ro.redeul.google.go;

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
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Ignore
public abstract class GoFileBasedPsiTestCase extends GoPsiTestCase {


    protected void doTest() throws Exception {
        final String fullPath =
                new File((getTestDataPath() + getTestName(false))
                .replace(File.separatorChar, '/')).getCanonicalPath();

        VirtualFile vFile;

        vFile = LocalFileSystem.getInstance().findFileByPath(fullPath);
        if (vFile != null && vFile.isDirectory()) {
            doDirectoryTest(vFile);
            return;
        }

        vFile = LocalFileSystem.getInstance().findFileByPath(fullPath + ".go");
        if (vFile != null) {
            doSingleFileTest(vFile);
            return;
        }

        fail("no test files found in \"" + fullPath + "\"");
    }

    private void doSingleFileTest(VirtualFile vFile) throws Exception {
        files.clear();

        VirtualFile vModuleDir = PsiTestUtil.createTestProjectStructure(getProject(), getModule(), new ArrayList<File>());

        parseFile(vFile, vFile.getParent(), vModuleDir);

        addBuiltinPackage(vModuleDir);

        for (Map.Entry<PsiFile, String> fileEntry : files.entrySet()) {
            postProcessFilePsi(fileEntry.getKey(), fileEntry.getValue());
        }

        assertTest();
    }

    private void doDirectoryTest(@NotNull final VirtualFile file) throws Exception {
        files.clear();

        final VirtualFile contentRoot = PsiTestUtil.createTestProjectStructure(getProject(), getModule(), file.getPath(), new ArrayList<File>());

        VfsUtil.processFilesRecursively(
                file,
                new FilteringProcessor<VirtualFile>(
                        new Condition<VirtualFile>() {
                            @Override
                            public boolean value(VirtualFile virtualFile) {
                                return !virtualFile.isDirectory() && virtualFile.getName().endsWith(".go");
                            }
                        },
                        new Processor<VirtualFile>() {
                            @Override
                            public boolean process(VirtualFile virtualFile) {
                                parseFile(virtualFile, file, contentRoot);
                                return true;
                            }
                        }
                )
        );

        addBuiltinPackage(contentRoot);

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

    private Map<PsiFile, String> files = new HashMap<PsiFile, String>();

    protected void parseFile(VirtualFile file, VirtualFile root,
                             VirtualFile vModuleRoot) {

        String relativePath = VfsUtil.getRelativePath(file.getParent(), root, '/');

        if (relativePath == null) {
            fail("could not determine relative path for file " + file.getCanonicalPath());
        }

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
