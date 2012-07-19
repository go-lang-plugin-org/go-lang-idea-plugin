package main
func main() {
    a := 5
Test:
    switch (a) {
        case 3:
    break Test
    case 5:
        println("hi")
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
      ShortVarStmtImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('a')
        PsiWhiteSpace(' ')
        PsiElement(:=)(':=')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIntegerImpl
            PsiElement(LITERAL_INT)('5')
      PsiWhiteSpace('\n')
      LabeledStmtImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Test')
        PsiElement(:)(':')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        SwitchExprStmtImpl
          PsiElement(KEYWORD_SWITCH)('switch')
          PsiWhiteSpace(' ')
          ParenthesisedExpressionImpl
            PsiElement(()('(')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('a')
            PsiElement())(')')
          PsiWhiteSpace(' ')
          PsiElement({)('{')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('        ')
          SwitchExprCaseImpl
            PsiElement(KEYWORD_CASE)('case')
            PsiWhiteSpace(' ')
            LiteralExpressionImpl
              LiteralIntegerImpl
                PsiElement(LITERAL_INT)('3')
            PsiElement(:)(':')
            PsiWhiteSpace('\n')
            PsiWhiteSpace('    ')
            BreakStmtImpl
              PsiElement(KEYWORD_BREAK)('break')
              PsiWhiteSpace(' ')
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('Test')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('    ')
          SwitchExprCaseImpl
            PsiElement(KEYWORD_CASE)('case')
            PsiWhiteSpace(' ')
            LiteralExpressionImpl
              LiteralIntegerImpl
                PsiElement(LITERAL_INT)('5')
            PsiElement(:)(':')
            PsiWhiteSpace('\n')
            PsiWhiteSpace('        ')
            ExpressionStmtImpl
              BuiltInCallExpressionImpl
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('println')
                PsiElement(()('(')
                LiteralExpressionImpl
                  LiteralStringImpl
                    PsiElement(LITERAL_STRING)('"hi"')
                PsiElement())(')')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('    ')
          PsiElement(})('}')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
