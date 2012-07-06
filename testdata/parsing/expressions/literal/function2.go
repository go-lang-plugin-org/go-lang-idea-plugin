package main
var f = func(x, y int) int { return 0 }(1, 2)

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
        PsiElement(IDENTIFIER)('f')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      CallOrConversionExpressionImpl
        LiteralExpressionImpl
          LiteralFunctionImpl
            PsiElement(KEYWORD_FUNC)('func')
            PsiElement(()('(')
            FunctionParameterListImpl
              FunctionParameterImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('x')
                PsiElement(,)(',')
                PsiWhiteSpace(' ')
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('y')
                PsiWhiteSpace(' ')
                TypeNameImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('int')
            PsiElement())(')')
            PsiWhiteSpace(' ')
            FunctionResult
              FunctionParameterListImpl
                FunctionParameterImpl
                  TypeNameImpl
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('int')
            PsiWhiteSpace(' ')
            BlockStmtImpl
              PsiElement({)('{')
              PsiWhiteSpace(' ')
              ReturnStmtImpl
                PsiElement(KEYWORD_RETURN)('return')
                PsiWhiteSpace(' ')
                LiteralExpressionImpl
                  LiteralIntegerImpl
                    PsiElement(LITERAL_INT)('0')
              PsiWhiteSpace(' ')
              PsiElement(})('}')
        PsiElement(()('(')
        ExpressionList
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('1')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('2')
        PsiElement())(')')
  PsiWhiteSpace('\n')
