package main
type f <-chan int
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
      TypeChanRecvImpl
        PsiElement(<-)('<-')
        PsiElement(KEYWORD_CHAN)('chan')
        PsiWhiteSpace(' ')
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('int')
