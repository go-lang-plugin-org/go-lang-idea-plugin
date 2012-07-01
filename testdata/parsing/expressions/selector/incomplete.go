package main;
func main() {
    x.
}

/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
    PsiElement(;)(';')
  PsiElement(WS_NEW_LINES)('\n')
  FunctionDeclaration(main)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
    PsiElement(()('(')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiElement(WS_NEW_LINES)('\n')
      PsiWhiteSpace('    ')
      ExpressionStmtImpl
        SelectorExpression
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('x')
          PsiElement(.)('.')
          PsiElement(WS_NEW_LINES)('\n')
          PsiErrorElement:Identifier expected
            <empty list>
      PsiElement(})('}')
  PsiElement(WS_NEW_LINES)('\n')