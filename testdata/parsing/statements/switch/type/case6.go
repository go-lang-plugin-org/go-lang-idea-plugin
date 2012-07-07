package main
func main() {
    switch e = 2; i := x[i].(type) {
        case nil:
        case int, []string:
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
      SwitchTypeStmt
        PsiElement(KEYWORD_SWITCH)('switch')
        PsiWhiteSpace(' ')
        AssignStmt
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('e')
          PsiWhiteSpace(' ')
          PsiElement(=)('=')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('2')
        PsiElement(;)(';')
        PsiWhiteSpace(' ')
        SwitchTypeGuard
          PsiElement(IDENTIFIER)('i')
          PsiWhiteSpace(' ')
          PsiElement(:=)(':=')
          PsiWhiteSpace(' ')
          IndexExpressionImpl
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('x')
            PsiElement([)('[')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('i')
            PsiElement(])(']')
          PsiElement(.)('.')
          PsiElement(()('(')
          PsiElement(KEYWORD_TYPE)('type')
          PsiElement())(')')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('        ')
        SwitchTypeCase
          PsiElement(KEYWORD_CASE)('case')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('nil')
          PsiElement(:)(':')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('        ')
        SwitchTypeCase
          PsiElement(KEYWORD_CASE)('case')
          PsiWhiteSpace(' ')
          TypeList
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('int')
            PsiElement(,)(',')
            PsiWhiteSpace(' ')
            TypeSliceImpl
              PsiElement([)('[')
              PsiElement(])(']')
              TypeNameImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('string')
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
