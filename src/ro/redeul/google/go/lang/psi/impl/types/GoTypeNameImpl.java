package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.codeInsight.completion.DefaultInsertHandler;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.completion.GroovyInsertHandlerAdapter;
import org.omg.CORBA.OBJ_ADAPTER;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.processors.LibraryContentsProcessor;
import ro.redeul.google.go.lang.psi.processors.TypesReferencesScopeProcessor;
import ro.redeul.google.go.lang.psi.resolve.GoResolveUtil;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 7:12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoTypeNameImpl extends GoPsiElementImpl implements GoTypeName {

    public GoTypeNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "TypeName";
    }

    @Override
    public String getName() {

        GoIdentifier identifier = findChildByClass(GoIdentifier.class);

        return identifier != null ? identifier.getText() : getText();
    }

    public PsiElement setName(@NonNls String name) throws IncorrectOperationException {
        return null;
    }

    public PsiElement getElement() {
        return this;
    }

    public TextRange getRangeInElement() {
        return new TextRange(0, getTextLength());
    }

    public GoPackageReference getPackageReference() {
        return findChildByClass(GoPackageReference.class);
    }

    public PsiElement resolve() {

        TypesReferencesScopeProcessor namedTypesProcessor = new TypesReferencesScopeProcessor(this);

        if (!PsiScopesUtil.treeWalkUp(namedTypesProcessor, this, this.getContainingFile())) {
            return namedTypesProcessor.getFoundType();
        }

        return null;
    }

    @NotNull
    public String getCanonicalText() {
        return getText();
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return this;
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if (isReferenceTo(element))
            return this;

        throw new IncorrectOperationException("Cannot bind to:" + element + " of class " + element.getClass());
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    public boolean isReferenceTo(PsiElement element) {
        return true;
    }

    @NotNull
    public Object[] getVariants() {
        if (getPackageReference() != null) {
            LibraryContentsProcessor processor = new LibraryContentsProcessor(this);

            GoFile goFile = (GoFile) getContainingFile();

            for (GoImportDeclaration goImportDeclaration : goFile.getImportDeclarations()) {
                PsiScopesUtil.treeWalkUp(processor, goImportDeclaration, this.getContainingFile());
            }

            return processor.getPackageContents();
        } else {
            List<Object> builtInTypes = new ArrayList<Object>();

            builtInTypes.add("uint8");
            builtInTypes.add("uint16");
            builtInTypes.add("uint32");
            builtInTypes.add("uint64");
            builtInTypes.add("int8");
            builtInTypes.add("int16");
            builtInTypes.add("int32");
            builtInTypes.add("int64");
            builtInTypes.add("float32");
            builtInTypes.add("float64");
            builtInTypes.add("complex64");
            builtInTypes.add("complex128");
            builtInTypes.add("byte");

            builtInTypes.add("unit");
            builtInTypes.add("int");
            builtInTypes.add("float");
            builtInTypes.add("complex");
            builtInTypes.add("uintptr");
            builtInTypes.add("bool");
            builtInTypes.add("string");


            NamedTypesScopeProcessor2 namedTypesProcessor = new NamedTypesScopeProcessor2(builtInTypes);

            PsiScopesUtil.treeWalkUp(namedTypesProcessor, this, this.getContainingFile());

            return namedTypesProcessor.references();
        }
    }

    public boolean isSoft() {
        return false;
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeName(this);
    }

    private class NamedTypesScopeProcessor2 extends BaseScopeProcessor {
        List<Object> references = new ArrayList<Object>();

        private NamedTypesScopeProcessor2(List<Object> references) {
            this.references.addAll(references);
        }

        public boolean execute(PsiElement element, ResolveState state) {

            collectTypeDeclarations(element, state);

            collectImports(element, state);

            return true;
        }

        private void collectImports(PsiElement element, ResolveState state) {
            if (!(element instanceof GoImportSpec)) {
                return;
            }

            GoImportSpec importSpec = (GoImportSpec) element;

            GoPackageReference packageRef = importSpec.getPackageReference();

            String packageName = null;
            if (packageRef == null) {
                packageName = GoResolveUtil.defaultPackageNameFromImport(importSpec.getImportPath());
            } else {
                if (!(packageRef.isBlank()) && !(packageRef.isLocal())) {
                    packageName = packageRef.getName();
                }
            }

            if (packageName != null) {

                LookupElement lookupElement =
                        LookupElementBuilder.create(packageName)
                                .setBold(true)
                                .setIcon(GoIcons.GO_ICON_16x16)
                                .setTypeText(importSpec.getImportPath())
                                .setInsertHandler(new InsertHandler<LookupElement>() {
                                    public void handleInsert(InsertionContext context, LookupElement item) {
                                        Editor editor = context.getEditor();
                                        Document document = editor.getDocument();

                                        document.insertString(context.getTailOffset(), ".");
                                        editor.getCaretModel().moveToOffset(context.getTailOffset());
                                    }
                                });

                references.add(lookupElement);
            }
        }

        private void collectTypeDeclarations(PsiElement element, ResolveState state) {
            if (!(element instanceof GoTypeDeclaration)) {
                return;
            }

            GoTypeDeclaration typeDeclaration = (GoTypeDeclaration) element;

            for (GoTypeSpec typeSpec : typeDeclaration.getTypeSpecs()) {

                GoTypeNameDeclaration typeNameDeclaration = typeSpec.getTypeNameDeclaration();

                if (typeNameDeclaration != null) {
                    references.add(typeNameDeclaration);
                }
            }
        }

        public Object[] references() {
            return references.toArray(new Object[references.size()]);
        }
    }

}
