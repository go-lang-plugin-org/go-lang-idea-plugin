package main
var ( a,
 b = 1,

 2
 )
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
    PsiElement(()('(')
    PsiWhiteSpace(' ')
    VarDeclarationImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('a')
      PsiElement(,)(',')
      PsiWhiteSpace('\n')
      PsiWhiteSpace(' ')
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('b')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_INT)('1')
      PsiElement(,)(',')
      PsiWhiteSpace('\n\n')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_INT)('2')
    PsiWhiteSpace('\n')
    PsiWhiteSpace(' ')
    PsiElement())(')')
