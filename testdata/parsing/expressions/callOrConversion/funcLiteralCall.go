package main

func main() {
    (func() int)()
}

/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n\n')
  FunctionDeclaration(main)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('main')
    PsiElement(()('(')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      ExpressionStmtImpl
        CallOrConversionExpressionImpl
          TypeParenthesizedImpl
            PsiElement(()('(')
            TypeFunctionImpl
              PsiElement(KEYWORD_FUNC)('func')
              PsiElement(()('(')
              PsiElement())(')')
              PsiWhiteSpace(' ')
              FunctionResult
                FunctionParameterListImpl
                  FunctionParameterImpl
                    TypeNameImpl
                      LiteralIdentifierImpl
                        PsiElement(IDENTIFIER)('int')
            PsiElement())(')')
          PsiElement(()('(')
          PsiElement())(')')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
  PsiWhiteSpace('\n')
