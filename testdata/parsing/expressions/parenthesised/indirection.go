package main
var e = (*v)['a']
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
        ParenthesisedExpressionImpl
          PsiElement(()('(')
          UnaryExpressionImpl
            PsiElement(*)('*')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('v')
          PsiElement())(')')
        PsiElement([)('[')
        LiteralExpressionImpl
          LiteralCharImpl
            PsiElement(LITERAL_CHAR)(''a'')
        PsiElement(])(']')
