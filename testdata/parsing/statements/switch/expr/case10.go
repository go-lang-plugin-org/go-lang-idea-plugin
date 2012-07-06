package main
func main() {
    switch e {
        case a:
            x := y
            x()
            return 1
    }
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
      SwitchExprStmt
        PsiElement(KEYWORD_SWITCH)('switch')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('e')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('        ')
        SwitchExprCase
          PsiElement(KEYWORD_CASE)('case')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('a')
          PsiElement(:)(':')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('            ')
          ShortVarStmtImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('x')
            PsiWhiteSpace(' ')
            PsiElement(:=)(':=')
            PsiWhiteSpace(' ')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('y')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('            ')
          ExpressionStmtImpl
            CallOrConversionExpressionImpl
              LiteralExpressionImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('x')
              PsiElement(()('(')
              PsiElement())(')')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('            ')
          ReturnStmtImpl
            PsiElement(KEYWORD_RETURN)('return')
            PsiWhiteSpace(' ')
            LiteralExpressionImpl
              LiteralIntegerImpl
                PsiElement(LITERAL_INT)('1')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        PsiElement(})('}')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
