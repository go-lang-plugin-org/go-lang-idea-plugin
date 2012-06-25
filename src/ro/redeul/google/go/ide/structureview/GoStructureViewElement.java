package ro.redeul.google.go.ide.structureview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.ui.LayeredIcon;
import com.intellij.util.PlatformIcons;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.documentation.DocumentUtil;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import static ro.redeul.google.go.lang.psi.processors.GoNamesUtil.isExportedName;

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
        if (isExportedName(info.getName())) {
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
        } else if (element instanceof GoLiteralIdentifier) {
            return new LiteralIdentifierInfo(element);
        } else if (element instanceof GoTypeSpec) {
            GoType type = ((GoTypeSpec) element).getType();
            if (type instanceof GoTypeInterface) {
                return new InterfaceInfo(element);
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
            return children.toArray(new TreeElement[children.size()]);
        }

        private List<GoLiteralIdentifier> getConstDeclarations(GoFile psiFile) {
            GoConstDeclarations[] constDeclarations = psiFile.getConsts();
            if (constDeclarations == null) {
                return new ArrayList<GoLiteralIdentifier>();
            }

            List<GoLiteralIdentifier> consts = new ArrayList<GoLiteralIdentifier>();
            for (GoConstDeclarations cds : constDeclarations) {
                for (GoConstDeclaration cd : cds.getDeclarations()) {
                    Collections.addAll(consts, cd.getIdentifiers());
                }
            }

            Collections.sort(consts, NAMED_ELEMENT_COMPARATOR);
            return consts;
        }

        private List<GoLiteralIdentifier> getVariableDeclarations(GoFile psiFile) {
            GoVarDeclarations[] varDeclarations = psiFile.getGlobalVariables();
            if (varDeclarations == null) {
                return new ArrayList<GoLiteralIdentifier>();
            }

            List<GoLiteralIdentifier> vars = new ArrayList<GoLiteralIdentifier>();
            for (GoVarDeclarations vds : varDeclarations) {
                for (GoVarDeclaration cd : vds.getDeclarations()) {
                    Collections.addAll(vars, cd.getIdentifiers());
                }
            }

            Collections.sort(vars, NAMED_ELEMENT_COMPARATOR);
            return vars;
        }

        private List<GoFunctionDeclaration> getFunctionDeclarations(GoFile psiFile) {
            GoFunctionDeclaration[] functionDeclarations = psiFile.getFunctions();
            if (functionDeclarations == null) {
                return new ArrayList<GoFunctionDeclaration>();
            }

            List<GoFunctionDeclaration> functions =
                    new ArrayList<GoFunctionDeclaration>(Arrays.asList(functionDeclarations));
            Collections.sort(functions, NAMED_ELEMENT_COMPARATOR);
            return functions;
        }

        private List<GoTypeSpec> getTypeDeclarations(GoFile psiFile) {
            GoTypeDeclaration[] typeDeclarations = psiFile.getTypeDeclarations();
            if (typeDeclarations == null) {
                return new ArrayList<GoTypeSpec>();
            }

            List<GoTypeSpec> specs = new ArrayList<GoTypeSpec>();
            for (GoTypeDeclaration typeDec : typeDeclarations) {
                Collections.addAll(specs, typeDec.getTypeSpecs());
            }

            Collections.sort(specs, new Comparator<GoTypeSpec>() {
                @Override
                public int compare(GoTypeSpec lhs, GoTypeSpec rhs) {
                    boolean lhsInterface = lhs instanceof GoTypeInterface;
                    boolean rhsInterface = rhs instanceof GoTypeInterface;
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
            PsiElement parent = element.getParent();
            GoType type = null;
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
            List<PsiNamedElement> children = new ArrayList<PsiNamedElement>();
            if (typeSpec.getType() != null ) {
//            TODO: make sure we are only looking inside the types that actually
//              have members
//                for (GoPsiElement psi : typeSpec.getType().getMembers()) {
//                    if (psi instanceof PsiNamedElement) {
//                        children.add((PsiNamedElement) psi);
//                    }
//                }

                PsiFile file = typeSpec.getContainingFile();
                String name = typeSpec.getName();
                if (name != null && file instanceof GoFile) {
                    for (GoMethodDeclaration md : ((GoFile) file).getMethods()) {
                        GoMethodReceiver mr = md.getMethodReceiver();
                        if (mr == null) {
                            continue;
                        }

                        GoTypeName typeName = mr.getTypeName();
                        if (typeName != null && name.equals(typeName.getName())) {
                            children.add(md);
                        }
                    }
                }
            }

            Collections.sort(children, NAMED_ELEMENT_COMPARATOR);
            return children;
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
            return "Unkown element - " + element.getName();
        }
    }
}
