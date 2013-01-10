package main
type T struct { x int; y int32 }
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
        PsiWhiteSpace(' ')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('x')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
        PsiElement(;)(';')
        PsiWhiteSpace(' ')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('y')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int32')
        PsiWhiteSpace(' ')
        PsiElement(})('}')