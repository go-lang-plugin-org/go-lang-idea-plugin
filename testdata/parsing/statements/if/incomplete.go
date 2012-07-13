package main

func main() {
    var a int = 3
    if a == 2 {
    } else a = 5
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
      VarDeclarationsImpl
        PsiElement(KEYWORD_VAR)('var')
        PsiWhiteSpace(' ')
        VarDeclarationImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('a')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
          PsiWhiteSpace(' ')
          PsiElement(=)('=')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('3')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      IfStmtImpl
        PsiElement(KEYWORD_IF)('if')
        PsiWhiteSpace(' ')
        RelationalExpressionImpl
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('a')
          PsiWhiteSpace(' ')
          PsiElement(==)('==')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralIntegerImpl
              PsiElement(LITERAL_INT)('2')
        PsiWhiteSpace(' ')
        BlockStmtImpl
          PsiElement({)('{')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('    ')
          PsiElement(})('}')
        PsiWhiteSpace(' ')
        PsiElement(KEYWORD_ELSE)('else')
        PsiErrorElement:Block or If statement expected
          <empty list>
      PsiWhiteSpace(' ')
      PsiErrorElement:';' or newline expected
        PsiElement(IDENTIFIER)('a')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      PsiElement(LITERAL_INT)('5')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
