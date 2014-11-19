package main
type t [1000]*float64
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
      TypeNameDeclaration(t)
        PsiElement(IDENTIFIER)('t')
      PsiWhiteSpace(' ')
      TypeArrayImpl
        PsiElement([)('[')
        LiteralExpressionImpl
          LiteralIntegerImpl
            PsiElement(LITERAL_INT)('1000')
        PsiElement(])(']')
        TypePointerImpl
          PsiElement(*)('*')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('float64')