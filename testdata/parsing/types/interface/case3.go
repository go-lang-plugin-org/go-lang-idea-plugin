package main;type Lock interface {Lock(); Unlock() }

/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
    PsiElement(;)(';')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    TypeSpecImpl
      TypeNameDeclaration(Lock)
        PsiElement(IDENTIFIER)('Lock')
      PsiWhiteSpace(' ')
      TypeInterfaceImpl
        PsiElement(KEYWORD_INTERFACE)('interface')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        MethodDeclaration(Lock)
          PsiElement(IDENTIFIER)('Lock')
          PsiElement(()('(')
          PsiElement())(')')
        MethodDeclaration()
          TypeNameImpl
            PsiErrorElement:identifier.expected
              PsiElement(;)(';')
        PsiWhiteSpace(' ')
        MethodDeclaration(Unlock)
          PsiElement(IDENTIFIER)('Unlock')
          PsiElement(()('(')
          PsiElement())(')')
        PsiWhiteSpace(' ')
        PsiElement(})('}')
  PsiElement(WS_NEW_LINES)('\n')