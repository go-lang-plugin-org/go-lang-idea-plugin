package main
func main() {
    switch e1, e2 := 2, 3; e1 {
        case a:
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
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('main')
    PsiElement(()('(')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      SwitchExprStmtImpl
        PsiElement(KEYWORD_SWITCH)('switch')
        PsiWhiteSpace(' ')
        ShortVarStmtImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('e1')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('e2')
          PsiWhiteSpace(' ')
          PsiElement(:=)(':=')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('2')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('3')
        PsiElement(;)(';')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('e1')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('        ')
        SwitchExprCaseImpl
          PsiElement(KEYWORD_CASE)('case')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('a')
          PsiElement(:)(':')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('             ')
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
