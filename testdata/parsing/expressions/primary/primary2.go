package main
var e = f(3.1415, true)
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
      CallOrConversionExpressionImpl
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('f')
        PsiElement(()('(')
        ExpressionList
          LiteralExpressionImpl
            LiteralFloatImpl
              PsiElement(LITERAL_FLOAT)('3.1415')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralBoolImpl
              PsiElement(IDENTIFIER)('true')
        PsiElement())(')')
