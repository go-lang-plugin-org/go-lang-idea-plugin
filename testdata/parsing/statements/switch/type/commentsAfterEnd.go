package main
func main() {
    switch e = 2; x[1:2].(type) {
        case nil:
        case int, []string:
             return 1
    }

    //adfas
    //adfa
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
      SwitchTypeStmtImpl
        PsiElement(KEYWORD_SWITCH)('switch')
        PsiWhiteSpace(' ')
        AssignStmtImpl
          ExpressionListImpl
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('e')
          PsiWhiteSpace(' ')
          PsiElement(=)('=')
          PsiWhiteSpace(' ')
          ExpressionListImpl
            LiteralExpressionImpl
              LiteralIntegerImpl
                PsiElement(LITERAL_INT)('2')
        PsiElement(;)(';')
        PsiWhiteSpace(' ')
        SwitchTypeGuardImpl
          SliceExpressionImpl
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('x')
            PsiElement([)('[')
            LiteralExpressionImpl
              LiteralIntegerImpl
                PsiElement(LITERAL_INT)('1')
            PsiElement(:)(':')
            LiteralExpressionImpl
              LiteralIntegerImpl
                PsiElement(LITERAL_INT)('2')
            PsiElement(])(']')
          PsiElement(.)('.')
          PsiElement(()('(')
          PsiElement(KEYWORD_TYPE)('type')
          PsiElement())(')')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('        ')
        SwitchTypeCaseImpl
          PsiElement(KEYWORD_CASE)('case')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('nil')
          PsiElement(:)(':')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('        ')
        SwitchTypeCaseImpl
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
      PsiWhiteSpace('\n\n')
      PsiWhiteSpace('    ')
      PsiComment(SL_COMMENT)('//adfas')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      PsiComment(SL_COMMENT)('//adfa')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
