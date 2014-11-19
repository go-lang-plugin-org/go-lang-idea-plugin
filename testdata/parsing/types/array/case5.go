package main
type t [2][2][2]float64
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
            PsiElement(LITERAL_INT)('2')
        PsiElement(])(']')
        TypeArrayImpl
          PsiElement([)('[')
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('2')
          PsiElement(])(']')
          TypeArrayImpl
            PsiElement([)('[')
            LiteralExpressionImpl
              LiteralIntegerImpl
                PsiElement(LITERAL_INT)('2')
            PsiElement(])(']')
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('float64')