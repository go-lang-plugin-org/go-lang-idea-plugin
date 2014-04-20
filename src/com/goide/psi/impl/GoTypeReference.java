package com.goide.psi.impl;

import com.goide.GoSdkUtil;
import com.goide.psi.*;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class GoTypeReference extends PsiReferenceBase<PsiElement> {
  @NotNull private final PsiElement myIdentifier;
  @NotNull private final GoTypeReferenceExpression myRefExpression;

  public GoTypeReference(@NotNull GoTypeReferenceExpression element) {
    super(element, TextRange.from(element.getIdentifier().getStartOffsetInParent(), element.getIdentifier().getTextLength()));
    myIdentifier = element.getIdentifier();
    myRefExpression = element;
  }

  protected static void processImports(@NotNull List<LookupElement> result, @NotNull GoFile file, boolean localCompletion) {
    if (localCompletion) {
      for (String i : file.getImportMap().keySet()) {
        result.add(GoPsiImplUtil.createPackageLookupElement(i, true));
      }
    }
  }

  @Nullable
  private static PsiElement calcQualifierResolve(@NotNull PsiElement qualifier) {
    PsiReference reference = qualifier.getReference();
    return reference != null ? reference.resolve() : null;
  }

  protected static void processInType(@NotNull List<LookupElement> result, PsiElement resolve, GoType type) {
    if (type instanceof GoStructType) {
      for (GoFieldDeclaration declaration : ((GoStructType)type).getFieldDeclarationList()) {
        for (GoFieldDefinition d : declaration.getFieldDefinitionList()) {
          result.add(GoPsiImplUtil.createVariableLikeLookupElement(d));
        }
        GoAnonymousFieldDefinition anon = declaration.getAnonymousFieldDefinition();
        if (anon != null) result.add(GoPsiImplUtil.createVariableLikeLookupElement(anon));
      }

      final List<GoTypeReferenceExpression> refs = ContainerUtil.newArrayList();
      type.accept(new GoRecursiveVisitor() {
        @Override
        public void visitAnonymousFieldDefinition(@NotNull GoAnonymousFieldDefinition o) {
          refs.add(o.getTypeReferenceExpression());
        }
      });
      for (GoTypeReferenceExpression ref : refs) {
        PsiElement typeSpec = ref.getReference().resolve();
        if (typeSpec != null && !PsiTreeUtil.isAncestor(typeSpec, type, true)) {
          if (typeSpec instanceof GoTypeSpec) {
            processInType(result, typeSpec, ((GoTypeSpec)typeSpec).getType());
          }
        }
      }
    }

    if (resolve instanceof GoTypeSpec) {
      for (GoMethodDeclaration method : ((GoTypeSpec)resolve).getMethods()) {
        result.add(GoPsiImplUtil.createFunctionOrMethodLookupElement(method));
      }
    }
  }

  @Nullable
  protected PsiElement getQualifier() {
    return myRefExpression.getQualifier();
  }

  @Nullable
  protected PsiElement processUnqualified(@NotNull GoFile file, boolean localResolve) {
    String id = myIdentifier.getText();
    if (getQualifier() == null) {
      GoScopeProcessorBase processor = createProcessor(false);
      ResolveUtil.treeWalkUp(myRefExpression, processor);
      GoNamedElement result = processor.getResult();
      if (result != null) return result;
    }
    for (GoTypeSpec t : file.getTypes()) { // todo: copy from completion or create a separate inspection
      if ((t.isPublic() || localResolve) && id.equals(t.getName())) return t;
    }

    for (PsiElement o : file.getImportMap().values()) {
      if (o instanceof GoImportSpec && ((GoImportSpec)o).getDot() != null) {
        PsiDirectory resolve = ((GoImportSpec)o).getImportString().resolve();
        PsiDirectory parent = file.getOriginalFile().getParent();
        if (Comparing.equal(parent, resolve)) continue;
        PsiElement result = processDirectory(resolve, null, null, false);
        if (result != null) {
          GoReference.putIfAbsent(o, myElement);
          return result;
        }
      }
    }
    
    return resolveImportOrPackage(file, id);
  }

  protected void processFile(@NotNull List<LookupElement> result, @NotNull GoFile file, boolean localCompletion) {
    GoScopeProcessorBase processor = createProcessor(true);
    ResolveUtil.treeWalkUp(myRefExpression, processor);
    for (GoNamedElement element : processor.getVariants()) {
      if (element instanceof GoTypeSpec) {
        result.add(GoPsiImplUtil.createTypeLookupElement((GoTypeSpec)element));
      }
    }
    boolean insideInterfaceType = myElement.getParent() instanceof GoMethodSpec;
    for (GoTypeSpec t : file.getTypes()) {
      if (insideInterfaceType && !(t.getType() instanceof GoInterfaceType)) continue;
      if (t.isPublic() || localCompletion) {
        result.add(GoPsiImplUtil.createTypeLookupElement(t));
      }
    }
    processImports(result, file, localCompletion);
  }

  @NotNull
  public PsiElement getIdentifier() {
    return myIdentifier;
  }

  @NotNull
  protected GoScopeProcessorBase createProcessor(boolean completion) {
    return new GoTypeProcessor(myIdentifier.getText(), myRefExpression, completion);
  }

  @Nullable
  protected PsiDirectory getDirectory(@NotNull PsiElement qualifier) {
    PsiElement resolve = calcQualifierResolve(qualifier);

    PsiDirectory dir = null;
    if (resolve instanceof GoImportSpec) {
      return ((GoImportSpec)resolve).getImportString().resolve();
    }
    else if (resolve instanceof PsiDirectory) {
      dir = (PsiDirectory)resolve;
    }
    return dir;
  }

  protected void processDirectory(@NotNull List<LookupElement> result,
                                  @Nullable PsiDirectory dir,
                                  @Nullable GoFile file,
                                  boolean localCompletion) {
    String packageName = file != null ? file.getPackageName() : null;
    String name = file != null ? file.getName() : null;
    if (dir != null) {
      for (PsiFile psiFile : dir.getFiles()) {
        if (psiFile instanceof GoFile && !psiFile.getName().equals(name)) {
          if (packageName != null && !Comparing.equal(((GoFile)psiFile).getPackageName(), packageName)) continue;
          processFile(result, (GoFile)psiFile, localCompletion);
        }
      }
    }
  }

  @Nullable
  protected PsiElement processDirectory(@Nullable PsiDirectory dir,
                                        @Nullable String packageName,
                                        @Nullable String name,
                                        boolean localResolve) {
    // todo: improve this algorithm
    if (dir == null) return null;
    for (PsiFile child : dir.getFiles()) {
      if (!GoUtil.allowed(child)) continue;
      if (name != null && Comparing.equal(name, child.getName())) continue;
      if (child instanceof GoFile) {
        GoFile goFile = (GoFile)child;
        if (packageName != null && !Comparing.equal(goFile.getPackageName(), packageName)) continue;
        PsiElement element = processUnqualified(goFile, localResolve);
        if (element != null) return element;
      }
    }
    return null;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    PsiFile file = myElement.getContainingFile();
    if (file instanceof GoFile) {
       PsiElement qualifier = getQualifier();
      if (qualifier == null) {
        PsiElement unqualified = processUnqualified((GoFile)file, true);
        if (unqualified != null) return unqualified;

        PsiElement result = processDirectory(file.getParent(), ((GoFile)file).getPackageName(), file.getName(), true);
        if (result != null) return result;

        return processBuiltin(file);
      }
      else {
        PsiElement qualifierResolve = calcQualifierResolve(qualifier);
        if (qualifierResolve instanceof PsiDirectory) {
          PsiElement result = processDirectory((PsiDirectory)qualifierResolve, null, null, false);
          if (result != null) return result;
        }
        if (qualifierResolve instanceof GoTypeOwner) {
          GoType type = ((GoTypeOwner)qualifierResolve).getGoType();
          PsiElement fromType = processGoType(type);
          if (fromType != null) return fromType;
        }
      }
    }
    return null;
  }

  @Nullable
  private PsiElement processBuiltin(@NotNull PsiFile file) {
    if (!file.getName().equals("builtin.go")) {
      GoFile builtin = GoSdkUtil.findBuiltinFile(myElement);
      if (builtin != null) {
        PsiElement r = processUnqualified(builtin, true);
        if (r != null) return r;
      }
    }
    return null;
  }

  @Nullable
  protected PsiElement processGoType(@Nullable GoType type) {
    if (type == null) return null;

    PsiElement fromExistingType = processExistingType(type);
    if (fromExistingType != null) return fromExistingType;

    if (type instanceof GoPointerType) type = ((GoPointerType)type).getType();

    return processInTypeRef(GoPsiImplUtil.getTypeReference(type), type);
  }

  @Nullable
  private PsiElement processInTypeRef(@Nullable GoTypeReferenceExpression refExpr, @Nullable GoType recursiveStopper) {
    PsiReference reference = refExpr != null ? refExpr.getReference() : null;
    PsiElement resolve = reference != null ? reference.resolve() : null;
    if (resolve instanceof GoTypeSpec) {
      GoType resolveType = ((GoTypeSpec)resolve).getType();
      if (recursiveStopper != null && resolveType != null) {
        if (recursiveStopper.textMatches(resolveType.getText())) return null;
      }
      PsiElement element = processExistingType(resolveType);
      return element != null ? element : null;
    }
    return null;
  }

  @Nullable
  private PsiElement processExistingType(@Nullable GoType type) {
    if (type == null) return null;
    if (type instanceof GoStructType) {
      GoScopeProcessorBase processor = createProcessor(false);
      type.processDeclarations(processor, ResolveState.initial(), null, myElement);
      GoNamedElement result = processor.getResult();
      if (result != null) return result;

      final List<GoTypeReferenceExpression> refs = ContainerUtil.newArrayList();
      type.accept(new GoRecursiveVisitor() {
        @Override
        public void visitAnonymousFieldDefinition(@NotNull GoAnonymousFieldDefinition o) {
          refs.add(o.getTypeReferenceExpression());
        }
      });
      for (GoTypeReferenceExpression ref : refs) {
        PsiElement element = processInTypeRef(ref, type);
        if (element != null) return element;
      }
    }
    PsiElement parent = type.getParent();
    if (parent instanceof GoTypeSpec) {
      for (GoMethodDeclaration method : ((GoTypeSpec)parent).getMethods()) {
        if (Comparing.equal(getIdentifier().getText(), method.getName())) return method;
      }
    }
    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    List<LookupElement> result = ContainerUtil.newArrayList();
    PsiElement qualifier = getQualifier();
    PsiFile file = myElement.getContainingFile();
    if (file instanceof GoFile) {
      if (qualifier == null) {
        processFile(result, (GoFile)file, true);

        VirtualFile vfile = file.getOriginalFile().getVirtualFile();
        VirtualFile localDir = vfile == null ? null : vfile.getParent();
        PsiDirectory localPsiDir = localDir == null ? null : PsiManager.getInstance(myElement.getProject()).findDirectory(localDir);
        processDirectory(result, localPsiDir, (GoFile)file, true);

        for (PsiElement o : ((GoFile)file).getImportMap().values()) {
          if (o instanceof GoImportSpec && ((GoImportSpec)o).getDot() != null) {
            PsiDirectory resolve = ((GoImportSpec)o).getImportString().resolve();
            processDirectory(result, resolve, null, false);
          }
        }

        if (!file.getName().equals("builtin.go")) {
          GoFile builtinFile = GoSdkUtil.findBuiltinFile(myElement);
          if (builtinFile != null) processFile(result, builtinFile, true);
        }
      }
      else {
        PsiElement qualifierResolve = calcQualifierResolve(qualifier);
        if (qualifierResolve instanceof GoNamedElement) {
          GoType goType = ((GoNamedElement)qualifierResolve).getGoType();
          if (goType instanceof GoPointerType) goType = ((GoPointerType)goType).getType();

          if (goType != null) {
            processInType(result, goType.getParent(), goType);
          }

          GoTypeReferenceExpression expression = GoPsiImplUtil.getTypeReference(goType);
          PsiReference reference = expression != null ? expression.getReference() : null;
          PsiElement resolve = reference != null ? reference.resolve() : null;
          if (resolve instanceof GoTypeSpec) {
            GoType type = ((GoTypeSpec)resolve).getType();
            processInType(result, resolve, type);
          }
        }
        processDirectory(result, getDirectory(qualifier), null, false);
      }
    }
    return ArrayUtil.toObjectArray(result);
  }

  @Nullable
  protected PsiElement resolveImportOrPackage(@NotNull GoFile file, @NotNull String id) {
    Collection<PsiElement> collection = file.getImportMap().get(id);
    for (Object o : collection) {
      if (o instanceof GoImportSpec) return (PsiElement)o;
      if (o instanceof GoImportString) return ((GoImportString)o).resolve();
    }
    return null;
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    getIdentifier().replace(GoElementFactory.createIdentifierFromText(myElement.getProject(), newElementName));
    return getElement();
  }
}
