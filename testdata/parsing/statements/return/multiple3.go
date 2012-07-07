package main
func main() {
return 1,2
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
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('main')
    PsiElement(()('(')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n')
      ReturnStmtImpl
        PsiElement(KEYWORD_RETURN)('return')
        PsiWhiteSpace(' ')
        ExpressionList
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('1')
          PsiElement(,)(',')
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('2')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
