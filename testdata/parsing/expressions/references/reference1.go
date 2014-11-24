package main
var t = (*x).y

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
        PsiElement(IDENTIFIER)('t')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      SelectorExpressionImpl
        ParenthesisedExpressionImpl
          PsiElement(()('(')
          UnaryExpressionImpl
            PsiElement(*)('*')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('x')
          PsiElement())(')')
        PsiElement(.)('.')
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('y')
  PsiWhiteSpace('\n')
