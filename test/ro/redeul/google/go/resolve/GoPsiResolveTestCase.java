package ro.redeul.google.go.resolve;

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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.util.FilteringProcessor;
import com.intellij.util.Processor;
import ro.redeul.google.go.psi.GoPsiTestCase;

public abstract class GoPsiResolveTestCase extends GoPsiTestCase {

    public String REF_MARKER = "/*ref*/";
    public String DEF_MARKER = "/*def*/";

    PsiReference ref;
    PsiElement def;

    @Override
    protected String getTestDataRelativePath() {
        return "psi/resolve/";
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

        if (vFile != null) {
            doSingleFileTest(vFile, vModuleDir);
            removeContentRoots(vModuleDir);
            return;
        }

        vFile = LocalFileSystem.getInstance().findFileByPath(fullPath);
        if (vFile != null && vFile.isDirectory()) {
            doDirectoryTest(vFile, vModuleDir);
            removeContentRoots(vModuleDir);
            return;
        }

        fail("no test files found in \"" + vFile + "\"");
    }

    private void removeContentRoots(VirtualFile vModuleDir) {
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

    private void doSingleFileTest(VirtualFile vFile, VirtualFile vModuleDir)
        throws Exception {
        parseFile(vFile, vFile.getParent(), vModuleDir);

        assertResolve();
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

        assertResolve();
    }

    private void assertResolve() {
        assertNotNull("Source position is not at a reference", ref);

        PsiElement resolvedDefinition = ref.resolve();
        if ( def != null ) {
            assertNotNull("The resolving should have been been a success",
                          resolvedDefinition);
            while (resolvedDefinition.getStartOffsetInParent() == 0) {
                resolvedDefinition = resolvedDefinition.getParent();
            }

            assertSame(def, resolvedDefinition);
        } else {
            assertNull("The resolving should have failed", resolvedDefinition);
        }
    }

    private void parseFile(VirtualFile file, VirtualFile root,
                           VirtualFile vModuleRoot) {
        String name = VfsUtil.getRelativePath(file, root, '/');
        try {
            String fileContent =
                StringUtil.convertLineSeparators(VfsUtil.loadText(file));

            PsiFile psiFile = createFile(myModule, vModuleRoot, name,
                                         fileContent);

            getDefinition(psiFile, fileContent);
            getReference(psiFile, fileContent);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private void getDefinition(PsiFile psiFile, String fileContent) {
        if (def != null) {
            return;
        }

        int position = fileContent.indexOf(DEF_MARKER);
        if (position > 0) {
            def = psiFile.findElementAt(position + DEF_MARKER.length());
            while (def != null && def.getStartOffsetInParent() == 0) {
                def = def.getParent();
            }
        }
    }

    private void getReference(PsiFile psiFile, String fileContent) {
        if (ref != null) {
            return;
        }

        int position = fileContent.indexOf(REF_MARKER);
        if (position > 0) {
            ref = psiFile.findReferenceAt(position + REF_MARKER.length());
        }
    }
}
