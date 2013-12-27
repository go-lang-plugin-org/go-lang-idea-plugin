package main
var e = a[b{c,d}]

/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  VarDeclarationsImpl
    PsiElement(KEYWORD_VAR)('var')
    PsiWhiteSpace(' ')
    VarDeclarationImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('e')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      IndexExpressionImpl
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('a')
        PsiElement([)('[')
        LiteralExpressionImpl
          LiteralCompositeImpl
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('b')
            LiteralCompositeValueImpl
              PsiElement({)('{')
              LiteralCompositeElementImpl
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('c')
              PsiElement(,)(',')
              LiteralCompositeElementImpl
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('d')
              PsiElement(})('}')
        PsiElement(])(']')
  PsiWhiteSpace('\n')
