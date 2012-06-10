package ro.redeul.google.go.resolve;

import java.io.File;

import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.PsiTestCase;
import ro.redeul.google.go.lang.parser.GoElementTypes;

public abstract class GoResolveTestCase extends PsiTestCase {

    public String REF_MARKER = "<ref>";
    public String DEF_MARKER = "<def>";

    protected abstract String getRelativeDataPath();

    @Override
    protected String getTestDataPath() {
        String pluginHome =
            FileUtil.toSystemIndependentName(
                PluginPathManager.getPluginHomePathRelative(
                    "google-go-language")) + "/testdata";

        String communityPath =
            PlatformTestUtil.getCommunityPath()
                            .replace(File.separatorChar, '/');

        String path =
            String.format("%s%s/%s/%s", communityPath, pluginHome,
                          getBasePath(), getRelativeDataPath());

        if (new File(path).exists())
            return path;

        return String.format("%s%s/../%s/%s", communityPath, pluginHome,
                             getBasePath(), getRelativeDataPath());
    }

    private String getBasePath() {
        return "resolve";
    }

    protected void doTestResolve() throws Exception {
        final String fullPath = getTestDataPath() + getTestName(false) + ".go";

        final VirtualFile vFile =
            LocalFileSystem.getInstance().findFileByPath(
                fullPath.replace(File.separatorChar, '/'));
        assertNotNull("file " + fullPath + " not found", vFile);

        String fileText = StringUtil.convertLineSeparators(
            VfsUtil.loadText(vFile));

        final String fileName = vFile.getName();

        int definitionMark = fileText.indexOf(DEF_MARKER);
        int referenceMark = fileText.indexOf(REF_MARKER);

        assertTrue(definitionMark >= 0);
        assertTrue(referenceMark >= 0);

        if ( definitionMark < referenceMark ) {
            fileText = fileText.substring(0, definitionMark) +
                fileText.substring(
                    definitionMark + DEF_MARKER.length());

            referenceMark = fileText.indexOf(REF_MARKER);
            fileText = fileText.substring(0, referenceMark) +
                fileText.substring(referenceMark + REF_MARKER.length());
        } else {
            fileText = fileText.substring(0, referenceMark) +
                fileText.substring(referenceMark + REF_MARKER.length());

            definitionMark = fileText.indexOf(DEF_MARKER);
            fileText = fileText.substring(0, definitionMark) +
                fileText.substring(
                    definitionMark + DEF_MARKER.length());
        }

        myFile = createFile(myModule, fileName, fileText);
        PsiReference ref = myFile.findReferenceAt(referenceMark);
        PsiElement definition = myFile.findElementAt(definitionMark);

        assertNotNull(ref);
        assertNotNull(definition);

        PsiElement resolvedDefinition = ref.resolve();
        assertNotNull(resolvedDefinition);

        if (definition.getNode().getElementType() == GoElementTypes.mIDENT)
            definition = definition.getParent();

        assertSame(definition, resolvedDefinition);
    }
}
