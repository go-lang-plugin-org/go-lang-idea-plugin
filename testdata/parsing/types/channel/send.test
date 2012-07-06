package main
type f chan<- int
-----
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
      TypeNameDeclaration(f)
        PsiElement(IDENTIFIER)('f')
      PsiWhiteSpace(' ')
      TypeChanSendImpl
        PsiElement(KEYWORD_CHAN)('chan')
        PsiElement(<-)('<-')
        PsiWhiteSpace(' ')
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('int')
