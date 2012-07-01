package main
var e = a.b.c

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
      SelectorExpression
        SelectorExpression
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('a')
          PsiElement(.)('.')
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('b')
        PsiElement(.)('.')
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('c')
  PsiElement(WS_NEW_LINES)('\n')