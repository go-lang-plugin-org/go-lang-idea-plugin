package main
func createSolver() {
    go func() {

    }()
}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  FunctionDeclaration(createSolver)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('createSolver')
    PsiElement(()('(')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      GoStmtImpl
        PsiElement(KEYWORD_GO)('go')
        PsiWhiteSpace(' ')
        CallOrConversionExpressionImpl
          LiteralExpressionImpl
            LiteralFunctionImpl
              PsiElement(KEYWORD_FUNC)('func')
              PsiElement(()('(')
              PsiElement())(')')
              PsiWhiteSpace(' ')
              BlockStmtImpl
                PsiElement({)('{')
                PsiWhiteSpace('\n\n')
                PsiWhiteSpace('    ')
                PsiElement(})('}')
          PsiElement(()('(')
          PsiElement())(')')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
