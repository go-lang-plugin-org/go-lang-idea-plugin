package main
var e = f.p[i].x()

/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiElement(WS_NEW_LINES)('\n')
  VarDeclarationsImpl
    PsiElement(KEYWORD_VAR)('var')
    PsiWhiteSpace(' ')
    VarDeclarationImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('e')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      CallOrConversionExpressionImpl
        SelectorExpression
          IndexExpressionImpl
            SelectorExpression
              LiteralExpressionImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('f')
              PsiElement(.)('.')
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('p')
            PsiElement([)('[')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('i')
            PsiElement(])(']')
          PsiElement(.)('.')
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('x')
        PsiElement(()('(')
        PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')