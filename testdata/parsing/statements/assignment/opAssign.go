package main
func main() {
    x += 1
    x >>= 1
    x <<= 1
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
      PsiWhiteSpace('    ')
      AssignStmtImpl
        ExpressionListImpl
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('x')
        PsiWhiteSpace(' ')
        PsiElement(+=)('+=')
        PsiWhiteSpace(' ')
        ExpressionListImpl
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('1')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      AssignStmtImpl
        ExpressionListImpl
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('x')
        PsiWhiteSpace(' ')
        PsiElement(>>=)('>>=')
        PsiWhiteSpace(' ')
        ExpressionListImpl
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('1')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      AssignStmtImpl
        ExpressionListImpl
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('x')
        PsiWhiteSpace(' ')
        PsiElement(<<=)('<<=')
        PsiWhiteSpace(' ')
        ExpressionListImpl
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('1')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
