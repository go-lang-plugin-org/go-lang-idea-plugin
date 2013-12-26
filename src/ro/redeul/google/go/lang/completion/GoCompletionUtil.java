package ro.redeul.google.go.lang.completion;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.*;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.completion.insertHandler.AutoImportInsertHandler;
import ro.redeul.google.go.lang.completion.insertHandler.KeywordInsertionHandler;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.utils.GoFileUtils;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.util.*;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Aug 22, 2010
 */
public class GoCompletionUtil {

    public static LookupElement[] resolveSdkPackagesForPath(Project project, PsiFile containingFile, String path) {
        VirtualFile virtualFile = containingFile.getVirtualFile();
        if (virtualFile == null) {
            return LookupElement.EMPTY_ARRAY;
        }

        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        Module module = projectFileIndex.getModuleForFile(virtualFile);
        if (module == null) {
            return LookupElement.EMPTY_ARRAY;
        }

        Sdk sdk = GoSdkUtil.getGoogleGoSdkForModule(module);
        if (sdk == null) {
            return LookupElement.EMPTY_ARRAY;
        }

        Set<String> completions = new HashSet<String>();
        VirtualFile roots[] = sdk.getRootProvider().getFiles(OrderRootType.CLASSES);

        for (VirtualFile root : roots) {

            CommonProcessors.CollectUniquesProcessor<VirtualFile> processor = new CommonProcessors.CollectUniquesProcessor<VirtualFile>() {
                @Override
                public boolean process(VirtualFile file) {
                    if ((!file.isDirectory()) && file.getName().matches(".*\\.a$")) {
                        super.process(file);
                    }

                    return true;
                }
            };

            VfsUtil.processFilesRecursively(root, processor);

            for (VirtualFile child : processor.getResults()) {
                String relativePath = VfsUtil.getRelativePath(child, root, '/');
                if (relativePath != null) {
                    completions.add(relativePath.replaceAll(".a$", ""));
                }
            }
        }

        List<LookupElement> list = new ArrayList<LookupElement>();
        for (String completion : completions) {
            list.add(LookupElementBuilder.create(completion));
        }

        return list.toArray(new LookupElement[list.size()]);
    }

    private static String cleanupImportPath(String currentPath) {
        return currentPath.replaceAll("^\"", "").replaceAll("\"$", "");
    }

    public static Collection<LookupElementBuilder> resolveLocalPackagesForPath(final Project project, PsiFile containingFile, String currentPath) {

        String importPath = cleanupImportPath(currentPath);

        final VirtualFile targetFile = containingFile.getVirtualFile();
        if (targetFile == null) {
            return Collections.emptyList();
        }

        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        Module module = projectFileIndex.getModuleForFile(targetFile);
        if (module == null) {
            return Collections.emptyList();
        }

        final PsiManager psiManager = PsiManager.getInstance(project);

        CommonProcessors.CollectUniquesProcessor<String> localPackages = new CommonProcessors.CollectUniquesProcessor<String>();

        Function<VirtualFile, String> convertor = new Function<VirtualFile, String>() {
            public String fun(VirtualFile virtualFile) {

                GoFile goFile = (GoFile) psiManager.findFile(virtualFile);
                if (goFile == null) {
                    return "";
                }

                String packageName = goFile.getPackage().getPackageName();

                // in the same folder as the target file we just import the package
                if (targetFile.getParent().equals(virtualFile.getParent())) {
                    return packageName;
                }

                String importName = VfsUtil.getRelativePath(virtualFile.getParent(), targetFile.getParent(), '/');
                if (!virtualFile.getParent().getName().equals(packageName)) {
                    importName += "/" + packageName;
                }

                return importName;
            }
        };

        Processor<VirtualFile> processor = new AdapterProcessor<VirtualFile, String>(localPackages, convertor) {
            @Override
            public boolean process(VirtualFile file) {
                if (file.getFileType() == GoFileType.INSTANCE) {
                    GoFile goFile = (GoFile) psiManager.findFile(file);

                    if (goFile != null && !goFile.getPackage().isMainPackage()) {
                        super.process(file);
                    }

                    return true;
                }

                return true;
            }
        };


        VfsUtil.processFilesRecursively(targetFile.getParent(), processor);

        List<LookupElementBuilder> elements = new ArrayList<LookupElementBuilder>();
        for (String localPackage : localPackages.getResults()) {
            LookupElementBuilder elementBuilder;

            if (importPath.startsWith("./")) {
                elementBuilder = LookupElementBuilder.create(localPackage)
                        .bold()
                        .withTypeText(
                                "via project");
            } else if (importPath.startsWith(".")) {
                elementBuilder = LookupElementBuilder.create("/" + localPackage)
                        .bold()
                        .withTypeText(
                                "via project");
            } else {
                elementBuilder = LookupElementBuilder.create("./" + localPackage)
                        .bold()
                        .withTypeText(
                                "via project");
            }

            elements.add(elementBuilder);
        }

        return elements;
    }

    public static LookupElement keyword(String keyword) {
        return keyword(keyword, new KeywordInsertionHandler());
    }

    public static LookupElement keyword(String keyword, @Nullable InsertHandler<LookupElement> handler) {
        return LookupElementBuilder.create(keyword)
                .bold()
                .withTypeText("keyword")
                .withInsertHandler(handler);
    }

    public static LookupElement builtinFunc(String name, @Nullable InsertHandler<LookupElement> handler) {
        return LookupElementBuilder.create(name)
                .bold()
                .withTypeText("builtin")
                .withPresentableText(String.format("%s()", name))
                .withInsertHandler(handler);
    }

    public static LookupElement packageElement(String packageName) {
        return packageElement(packageName, packageName);
    }

    public static LookupElement packageElement(String packageName, String tailText) {
        return LookupElementBuilder.create(packageName)
                .withIcon(PlatformIcons.PACKAGE_ICON)
                .withTypeText(" (" + tailText + ")", true)
                .withInsertHandler(
                        new AutoImportInsertHandler())
                .withTypeText("package");
    }

    public static LookupElement[] getImportedPackagesNames(PsiFile file) {

        if (!(file instanceof GoFile)) {
            return LookupElement.EMPTY_ARRAY;
        }

        GoFile goFile = (GoFile) file;

        List<LookupElement> elements = new ArrayList<LookupElement>();
        for (GoImportDeclaration importDeclaration : GoFileUtils.getImportDeclarations(goFile)) {
            String visiblePackageName = importDeclaration.getVisiblePackageName();
            GoLiteralString importPath1 = importDeclaration.getImportPath();
            if (importPath1 != null) {
                String importPath = importPath1.getValue();
                elements.add(packageElement(visiblePackageName, importPath));
            }
        }

        return elements.toArray(new LookupElement[elements.size()]);
    }
}
