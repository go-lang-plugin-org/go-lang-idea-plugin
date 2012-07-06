package main
var e = x.(type)
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
      LiteralExpressionImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('x')
  PsiErrorElement:';' or newline expected
    PsiElement(.)('.')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:';' or newline expected
    PsiElement(KEYWORD_TYPE)('type')
  PsiErrorElement:unknown.token
    PsiElement())(')')
