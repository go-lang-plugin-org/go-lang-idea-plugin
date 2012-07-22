package main
func main() {
    for i, s[i] = range [...]int{0, 1, 2, 3, 4} {
        println(i, s[i])
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
      ForWithRangeStmtImpl
        PsiElement(KEYWORD_FOR)('for')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('i')
        PsiElement(,)(',')
        PsiWhiteSpace(' ')
        IndexExpressionImpl
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('s')
          PsiElement([)('[')
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('i')
          PsiElement(])(']')
        PsiWhiteSpace(' ')
        PsiElement(=)('=')
        PsiWhiteSpace(' ')
        PsiElement(KEYWORD_RANGE)('range')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralCompositeImpl
            TypeArrayImpl
              PsiElement([)('[')
              PsiElement(...)('...')
              PsiElement(])(']')
              TypeNameImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('int')
            LiteralCompositeValueImpl
              PsiElement({)('{')
              LiteralCompositeElementImpl
                LiteralExpressionImpl
                  LiteralIntegerImpl
                    PsiElement(LITERAL_INT)('0')
              PsiElement(,)(',')
              PsiWhiteSpace(' ')
              LiteralCompositeElementImpl
                LiteralExpressionImpl
                  LiteralIntegerImpl
                    PsiElement(LITERAL_INT)('1')
              PsiElement(,)(',')
              PsiWhiteSpace(' ')
              LiteralCompositeElementImpl
                LiteralExpressionImpl
                  LiteralIntegerImpl
                    PsiElement(LITERAL_INT)('2')
              PsiElement(,)(',')
              PsiWhiteSpace(' ')
              LiteralCompositeElementImpl
                LiteralExpressionImpl
                  LiteralIntegerImpl
                    PsiElement(LITERAL_INT)('3')
              PsiElement(,)(',')
              PsiWhiteSpace(' ')
              LiteralCompositeElementImpl
                LiteralExpressionImpl
                  LiteralIntegerImpl
                    PsiElement(LITERAL_INT)('4')
              PsiElement(})('}')
        PsiWhiteSpace(' ')
        BlockStmtImpl
          PsiElement({)('{')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('        ')
          ExpressionStmtImpl
            BuiltInCallExpressionImpl
              LiteralExpressionImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('println')
              PsiElement(()('(')
              ExpressionListImpl
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('i')
                PsiElement(,)(',')
                PsiWhiteSpace(' ')
                IndexExpressionImpl
                  LiteralExpressionImpl
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('s')
                  PsiElement([)('[')
                  LiteralExpressionImpl
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('i')
                  PsiElement(])(']')
              PsiElement())(')')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('    ')
          PsiElement(})('}')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
