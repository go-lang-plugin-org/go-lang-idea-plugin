package main
type T struct {}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    TypeSpecImpl
      TypeNameDeclaration(T)
        PsiElement(IDENTIFIER)('T')
      PsiWhiteSpace(' ')
      TypeStructImpl
        PsiElement(KEYWORD_STRUCT)('struct')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiElement(})('}')