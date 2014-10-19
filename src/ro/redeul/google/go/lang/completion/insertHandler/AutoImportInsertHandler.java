package ro.redeul.google.go.lang.completion.insertHandler;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import ro.redeul.google.go.inspection.fix.AddImportFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.utils.GoFileUtils;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import java.util.List;

import static ro.redeul.google.go.imports.AutoImportHighlightingPass.getPackagesByName;

public class AutoImportInsertHandler implements InsertHandler<LookupElement> {
    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        String packageName = item.getLookupString();
        PsiFile file = context.getFile();
        if (!(file instanceof GoFile) || StringUtil.isEmpty(packageName)) {
            return;
        }

        for (GoImportDeclaration id : GoFileUtils.getImportDeclarations((GoFile) file)) {
            if (packageName.equals(id.getPackageAlias())) {
                return;
            }
        }

        GoFile goFile = (GoFile) file;
        GoNamesCache namesCache = GoNamesCache.getInstance(context.getProject());
        List<String> sdkPackages = getPackagesByName(namesCache.getSdkPackages(), packageName);
        List<String> projectPackages = getPackagesByName(namesCache.getProjectPackages(), packageName);
        new AddImportFix(sdkPackages, projectPackages, goFile, context.getEditor()).execute();
    }
}
