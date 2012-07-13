package main

func main() {
    goto exit
    println("hi")

exit:
    println("exit")
}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n\n')
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
      Goto statement
        PsiElement(KEYWORD_GOTO)('goto')
        PsiWhiteSpace(' ')
        Identifier
          PsiElement(IDENTIFIER)('exit')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
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
      PsiWhiteSpace('\n\n')
      LabeledStmtImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('exit')
        PsiElement(:)(':')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        ExpressionStmtImpl
          BuiltInCallExpressionImpl
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('println')
            PsiElement(()('(')
            LiteralExpressionImpl
              LiteralStringImpl
                PsiElement(LITERAL_STRING)('"exit"')
            PsiElement())(')')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
