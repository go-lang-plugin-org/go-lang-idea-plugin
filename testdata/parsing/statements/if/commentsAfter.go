package main
func main() {
       if 1 > 2 {
       }
       // test
       // test
}
-----
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
      PsiWhiteSpace('       ')
      IfStmtImpl
        PsiElement(KEYWORD_IF)('if')
        PsiWhiteSpace(' ')
        RelationalExpressionImpl
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('1')
          PsiWhiteSpace(' ')
          PsiElement(>)('>')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('2')
        PsiWhiteSpace(' ')
        BlockStmtImpl
          PsiElement({)('{')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('       ')
          PsiElement(})('}')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('       ')
      PsiComment(SL_COMMENT)('// test')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('       ')
      PsiComment(SL_COMMENT)('// test')
      PsiWhiteSpace('\n')
      PsiElement(})('}')