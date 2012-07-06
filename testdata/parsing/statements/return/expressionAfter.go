package main
func main() {
    return
    method();
}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  FunctionDeclaration(main)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
    PsiElement(()('(')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      ReturnStmtImpl
        PsiElement(KEYWORD_RETURN)('return')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      ExpressionStmtImpl
        CallOrConversionExpressionImpl
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('method')
          PsiElement(()('(')
          PsiElement())(')')
      PsiElement(;)(';')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
