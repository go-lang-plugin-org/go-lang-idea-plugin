package ro.redeul.google.go.ide.structureview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.ui.LayeredIcon;
import com.intellij.util.PlatformIcons;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.documentation.DocumentUtil;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.utils.GoFileUtils;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import static ro.redeul.google.go.lang.psi.processors.GoNamesUtil.isExportedName;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

/**
 * User: jhonny
 * Date: 06/07/11
 */
public class GoStructureViewElement implements StructureViewTreeElement, ItemPresentation {

    private final ElementInfo info;
    private final TreeElement[] children;

    public GoStructureViewElement(PsiNamedElement element) {
        this(makeElementInfo(element));
    }

    GoStructureViewElement(ElementInfo info) {
        this.info = info;
        children = info.getChildren();
    }

    @Override
    public PsiNamedElement getValue() {
        return info.element;
    }

    @Override
    public void navigate(boolean b) {
        if (getValue() instanceof NavigationItem) {
            ((NavigationItem) getValue()).navigate(b);
        }
    }

    @Override
    public boolean canNavigate() {
        return getValue() instanceof NavigationItem;
    }

    @Override
    public boolean canNavigateToSource() {
        return getValue() instanceof NavigationItem;
    }

    @Override
    public ItemPresentation getPresentation() {
        return this;
    }

    @Override
    public TreeElement[] getChildren() {
        return children;
    }

    @Override
    public String getPresentableText() {
        return info.getPresentationText();
    }

    @Override
    public String getLocationString() {
        return null;
    }

    @Override
    public Icon getIcon(boolean open) {
        if (info.getName() == null || isExportedName(info.getName())) {
            return info.getBaseIcon();
        }

        return new LayeredIcon(info.getBaseIcon(), PlatformIcons.LOCKED_ICON);
    }

    private static ElementInfo makeElementInfo(PsiNamedElement element) {
        if (element instanceof GoFile) {
            return new FileInfo(element);
        } else if (element instanceof GoMethodDeclaration) {
            return new MethodInfo(element);
        } else if (element instanceof GoFunctionDeclaration) {
            return new FunctionInfo(element);
        } else if (element instanceof GoLiteralIdentifier || element instanceof GoPsiTypeName) {
            return new LiteralIdentifierInfo(element);
        } else if (element instanceof GoTypeSpec) {
            GoPsiType type = ((GoTypeSpec) element).getType();
            if (type instanceof GoPsiTypeInterface) {
                return new InterfaceInfo(element);
            } else if (type instanceof GoPsiTypeStruct) {
                return new StructInfo(element);
            }
            return new TypeInfo(element);
        }
        return new Unknown(element);
    }


    private static abstract class ElementInfo {
        final PsiNamedElement element;

        protected ElementInfo(PsiNamedElement element) {
            this.element = element;
        }

        abstract Icon getBaseIcon();

        String getName() {
            return element.getName();
        }

        String getPresentationText() {
            return getName();
        }

        TreeElement[] getChildren() {
            return new TreeElement[0];
        }
    }

    private static class FileInfo extends ElementInfo {
        private FileInfo(PsiNamedElement element) {
            super(element);
        }

        @Override
        Icon getBaseIcon() {
            return GoIcons.GO_ICON_24x24;
        }

        @Override
        TreeElement[] getChildren() {
            GoFile psiFile = (GoFile) element;

            ArrayList<StructureViewTreeElement> children = new ArrayList<StructureViewTreeElement>();

            for (GoLiteralIdentifier id : getConstDeclarations(psiFile)) {
                children.add(new GoStructureViewElement(new ConstLiteralIdentifierInfo(id)));
            }

            for (GoLiteralIdentifier id : getVariableDeclarations(psiFile)) {
                children.add(new GoStructureViewElement(new VariableLiteralIdentifierInfo(id)));
            }

            for (GoTypeSpec ts : getTypeDeclarations(psiFile)) {
                children.add(new GoStructureViewElement(ts));
            }

            for (GoFunctionDeclaration fd : getFunctionDeclarations(psiFile)) {
                children.add(new GoStructureViewElement(fd));
            }

            for (GoMethodDeclaration fd : getMethodDeclarations(psiFile)) {
                GoPsiType type = fd.getMethodReceiver().getType();

                if (type == null)
                    continue;

                if ( type instanceof GoPsiTypePointer ) {
                    type = ((GoPsiTypePointer)type).getTargetType();
                }

                GoTypeNameDeclaration myTypeDeclaration = resolveSafely(type, GoTypeNameDeclaration.class);

                if ( myTypeDeclaration != null && myTypeDeclaration.getContainingFile().equals(psiFile) )
                    continue;

                children.add(new GoStructureViewElement(fd));
            }

            return children.toArray(new TreeElement[children.size()]);
        }

        private List<GoLiteralIdentifier> getConstDeclarations(GoFile psiFile) {
            List<GoLiteralIdentifier> consts = GoFileUtils.getConstIdentifiers(psiFile);
            Collections.sort(consts, NAMED_ELEMENT_COMPARATOR);
            return consts;
        }

        private List<GoLiteralIdentifier> getVariableDeclarations(GoFile psiFile) {
            List<GoLiteralIdentifier> vars = GoFileUtils.getVariableIdentifiers(psiFile);
            Collections.sort(vars, NAMED_ELEMENT_COMPARATOR);
            return vars;
        }

        private List<GoFunctionDeclaration> getFunctionDeclarations(GoFile psiFile) {
            List<GoFunctionDeclaration> functions = GoFileUtils.getFunctionDeclarations(psiFile);
            Collections.sort(functions, NAMED_ELEMENT_COMPARATOR);
            return functions;
        }

        private List<GoMethodDeclaration> getMethodDeclarations(GoFile psiFile) {
            List<GoMethodDeclaration> functions = GoFileUtils.getMethodDeclarations(psiFile);
            Collections.sort(functions, NAMED_ELEMENT_COMPARATOR);
            return functions;
        }

        private List<GoTypeSpec> getTypeDeclarations(GoFile psiFile) {
            List<GoTypeSpec> specs = GoFileUtils.getTypeSpecs(psiFile);

            Collections.sort(specs, new Comparator<GoTypeSpec>() {
                @Override
                public int compare(GoTypeSpec lhs, GoTypeSpec rhs) {
                    boolean lhsInterface = lhs instanceof GoPsiTypeInterface;
                    boolean rhsInterface = rhs instanceof GoPsiTypeInterface;
                    if (lhsInterface != rhsInterface) {
                        return lhsInterface ? -1 : 1;
                    }
                    return compareElementName(lhs, rhs);
                }
            });
            return specs;
        }

    }

    private static final Comparator<PsiNamedElement> NAMED_ELEMENT_COMPARATOR = new Comparator<PsiNamedElement>() {
        @Override
        public int compare(PsiNamedElement lhs, PsiNamedElement rhs) {
            return compareElementName(lhs, rhs);
        }
    };

    private static int compareElementName(PsiNamedElement lhs, PsiNamedElement rhs) {
        String lhsName = String.valueOf(lhs != null ? lhs.getName() : "");
        String rhsName = String.valueOf(rhs != null ? rhs.getName() : "");
        boolean lhsExported = isExportedName(lhsName);
        boolean rhsExported = isExportedName(rhsName);
        if (lhsExported != rhsExported) {
            return lhsExported ? -1 : 1;
        }

        return lhsName.compareTo(rhsName);
    }

    private static class FunctionInfo extends ElementInfo {
        private FunctionInfo(PsiNamedElement element) {
            super(element);
        }

        @Override
        public Icon getBaseIcon() {
            return PlatformIcons.FUNCTION_ICON;
        }

        @Override
        public String getPresentationText() {
            return DocumentUtil.getFunctionPresentationText((GoFunctionDeclaration) element);
        }
    }

    private static class MethodInfo extends FunctionInfo {
        public MethodInfo(PsiNamedElement element) {
            super(element);
        }

        @Override
        public Icon getBaseIcon() {
            return PlatformIcons.METHOD_ICON;
        }
    }

    private static class LiteralIdentifierInfo extends ElementInfo {
        private LiteralIdentifierInfo(PsiNamedElement element) {
            super(element);
        }

        @Override
        public Icon getBaseIcon() {
            return PlatformIcons.FIELD_ICON;
        }

        private String getTypeName() {
            if (element instanceof GoPsiTypeName) {
                GoPsiTypeName typeName = (GoPsiTypeName) element;
                return typeName.getQualifiedName();
            }

            PsiElement parent = element.getParent();
            GoPsiType type = null;
            GoLiteralIdentifier[] identifiers = GoLiteralIdentifier.EMPTY_ARRAY;
            GoExpr[] expressions = GoExpr.EMPTY_ARRAY;
            if (parent instanceof GoVarDeclaration) {
                GoVarDeclaration vd = (GoVarDeclaration) parent;
                type = vd.getIdentifiersType();
                identifiers = vd.getIdentifiers();
                expressions = vd.getExpressions();
            } else if (parent instanceof GoConstDeclaration) {
                GoConstDeclaration cd = (GoConstDeclaration) parent;
                type = cd.getIdentifiersType();
                identifiers = cd.getIdentifiers();
                expressions = cd.getExpressions();
            } else if (parent instanceof GoTypeStructField) {
                type = ((GoTypeStructField) parent).getType();
            }

            // return type name if type is explicitly defined in declaration.
            if (type != null) {
                String name = type.getName();
                return name == null ? type.getText() : name;
            }

            // otherwise show it's definition directly
            if (identifiers.length == expressions.length) {
                int start = element.getTextOffset();
                for (int i = 0; i < expressions.length; i++) {
                    if (identifiers[i].getTextOffset() == start) {
                        return "= " + expressions[i].getText();
                    }
                }
            }

            return "";
        }

        @Override
        public String getPresentationText() {
            return element.getName() + " " + getTypeName();
        }
    }

    private static class ConstLiteralIdentifierInfo extends LiteralIdentifierInfo {
        private ConstLiteralIdentifierInfo(PsiNamedElement element) {
            super(element);
        }

        @Override
        public Icon getBaseIcon() {
            return GoIcons.CONST_ICON;
        }
    }

    private static class VariableLiteralIdentifierInfo extends LiteralIdentifierInfo {
        private VariableLiteralIdentifierInfo(PsiNamedElement element) {
            super(element);
        }

        @Override
        public Icon getBaseIcon() {
            return PlatformIcons.VARIABLE_ICON;
        }
    }

    private static class TypeInfo extends ElementInfo {
        private TypeInfo(PsiNamedElement element) {
            super(element);
        }

        @Override
        public Icon getBaseIcon() {
            return PlatformIcons.CLASS_ICON;
        }

        @Override
        TreeElement[] getChildren() {
            GoTypeSpec typeSpec = (GoTypeSpec) element;
            ArrayList<TreeElement> children = new ArrayList<TreeElement>();
            for (PsiNamedElement psi : getMembers(typeSpec)) {
                children.add(new GoStructureViewElement(psi));
            }
            return children.toArray(new TreeElement[children.size()]);
        }

        List<PsiNamedElement> getMembers(GoTypeSpec typeSpec) {
            return Collections.emptyList();
        }

        @Override
        String getPresentationText() {
            GoPsiType type = ((GoTypeSpec) element).getType();
            if (type != null) {
                return getName() + " " + type.getText();
            }
            return getName();
        }
    }

    private static class StructInfo extends TypeInfo {
        private StructInfo(PsiNamedElement element) {
            super(element);
        }

        @Override
        List<PsiNamedElement> getMembers(GoTypeSpec typeSpec) {
            GoPsiType type = typeSpec.getType();
            if (!(type instanceof GoPsiTypeStruct)) {
                return Collections.emptyList();
            }

            List<PsiNamedElement> members = new ArrayList<PsiNamedElement>();
            members.addAll(getMethods(typeSpec));
            members.addAll(getFields((GoPsiTypeStruct) type));
            return members;
        }

        private List<PsiNamedElement> getFields(GoPsiTypeStruct struct) {
            List<PsiNamedElement> fields = getNamedFields(struct);
            fields.addAll(getAnonymousFields(struct));
            Collections.sort(fields, NAMED_ELEMENT_COMPARATOR);
            return fields;
        }

        private List<PsiNamedElement> getMethods(GoTypeSpec typeSpec) {
            PsiFile file = typeSpec.getContainingFile();
            String name = typeSpec.getName();
            if (name == null || !(file instanceof GoFile)) {
                return Collections.emptyList();
            }

            List<PsiNamedElement> children = new ArrayList<PsiNamedElement>();
            for (GoFile f : getAllSamePackageFiles((GoFile) file)) {
                getMethodsInFile(children, name, f);
            }
            Collections.sort(children, NAMED_ELEMENT_COMPARATOR);
            return children;
        }

        private Collection<GoFile> getAllSamePackageFiles(GoFile goFile) {
            String path = getFilePath(goFile);
            if (path.isEmpty()) {
                return Collections.singleton(goFile);
            }

            List<GoFile> files = new ArrayList<GoFile>();
            String packageName = goFile.getPackageName();
            GoNamesCache namesCache = GoNamesCache.getInstance(goFile.getProject());
            for (GoFile file : namesCache.getFilesByPackageName(packageName)) {
                if (path.equals(getFilePath(file))) {
                    files.add(file);
                }
            }

            if (files.isEmpty()) {
                files.add(goFile);
            }
            return files;
        }

        private String getFilePath(GoFile file) {
            VirtualFile vf = file.getVirtualFile();
            if (vf == null) {
                return "";
            }

            VirtualFile parent = vf.getParent();
            if (parent == null) {
                return "";
            }

            return parent.getPath();
        }

        private void getMethodsInFile(List<PsiNamedElement> children, String name, GoFile file) {
            for (GoMethodDeclaration md : file.getMethods()) {
                GoMethodReceiver mr = md.getMethodReceiver();
                if (mr == null) {
                    continue;
                }

                GoPsiType type = mr.getType();
                if (type instanceof GoPsiTypePointer) {
                    type = ((GoPsiTypePointer) type).getTargetType();
                }

                if (type != null && name.equals(type.getName())) {
                    children.add(md);
                }
            }
        }

        private List<PsiNamedElement> getAnonymousFields(GoPsiTypeStruct struct) {
            List<PsiNamedElement> children = new ArrayList<PsiNamedElement>();
            for (GoTypeStructAnonymousField field : struct.getAnonymousFields()) {
                children.add(field.getType());
            }
            return children;
        }

        private List<PsiNamedElement> getNamedFields(GoPsiTypeStruct struct) {
            List<PsiNamedElement> children = new ArrayList<PsiNamedElement>();
            for (GoTypeStructField field : struct.getFields()) {
                for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                    if (!identifier.isBlank()) {
                        children.add(identifier);
                    }
                }
            }
            return children;
        }

        @Override
        String getPresentationText() {
            return getName();
        }
    }

    private static class InterfaceInfo extends TypeInfo {
        private InterfaceInfo(PsiNamedElement element) {
            super(element);
        }

        @Override
        public Icon getBaseIcon() {
            return PlatformIcons.INTERFACE_ICON;
        }

        @Override
        List<PsiNamedElement> getMembers(GoTypeSpec typeSpec) {
            List<PsiNamedElement> children = new ArrayList<PsiNamedElement>();
            GoPsiType type = typeSpec.getType();
            if (!(type instanceof GoPsiTypeInterface)) {
                return Collections.emptyList();
            }
            GoPsiTypeInterface ti = (GoPsiTypeInterface) type;
            Collections.addAll(children, ti.getFunctionDeclarations());
            Collections.sort(children, NAMED_ELEMENT_COMPARATOR);
            return children;
        }

        @Override
        String getPresentationText() {
            return getName();
        }
    }

    private static class Unknown extends ElementInfo {
        private Unknown(PsiNamedElement element) {
            super(element);
        }

        @Override
        Icon getBaseIcon() {
            return PlatformIcons.EXCLUDED_FROM_COMPILE_ICON;
        }

        @Override
        String getPresentationText() {
            return "Unknown element - " + element.getName();
        }
    }
}
