package main
var e = a[b.c]

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
      IndexExpressionImpl
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('a')
        PsiElement([)('[')
        SelectorExpression
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('b')
          PsiElement(.)('.')
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('c')
        PsiElement(])(']')
  PsiElement(WS_NEW_LINES)('\n')