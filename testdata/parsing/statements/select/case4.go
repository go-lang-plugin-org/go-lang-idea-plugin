package main
func main() {
    select {
        case a := <- b:
            break
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
      SelectStmtImpl
        PsiElement(KEYWORD_SELECT)('select')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('        ')
        SelectCommClauseRecvImpl
          PsiElement(KEYWORD_CASE)('case')
          PsiWhiteSpace(' ')
          SelectCaseRecvExpr
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('a')
            PsiWhiteSpace(' ')
            PsiElement(:=)(':=')
            PsiWhiteSpace(' ')
            UnaryExpressionImpl
              PsiElement(<-)('<-')
              PsiWhiteSpace(' ')
              LiteralExpressionImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('b')
          PsiElement(:)(':')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('            ')
          BreakStmtImpl
            PsiElement(KEYWORD_BREAK)('break')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        PsiElement(})('}')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
