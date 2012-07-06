package main
const (X=iota;A)
const (Y=iota;A1 = 5)
const (Z=iota;A2 = )
const (T=iota;
A3,B1 = 5,3
)
const (V=iota; A4, B2 = 1 + 2)
const (B3=iota;
A5, B4 = 5
)
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  ConstDeclarationsImpl
    PsiElement(KEYWORD_CONST)('const')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('X')
      PsiElement(=)('=')
      LiteralExpressionImpl
        LiteralIotaImpl
          PsiElement(IDENTIFIER)('iota')
    PsiElement(;)(';')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('A')
    PsiElement())(')')
  PsiWhiteSpace('\n')
  ConstDeclarationsImpl
    PsiElement(KEYWORD_CONST)('const')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('Y')
      PsiElement(=)('=')
      LiteralExpressionImpl
        LiteralIotaImpl
          PsiElement(IDENTIFIER)('iota')
    PsiElement(;)(';')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('A1')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_INT)('5')
    PsiElement())(')')
  PsiWhiteSpace('\n')
  ConstDeclarationsImpl
    PsiElement(KEYWORD_CONST)('const')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('Z')
      PsiElement(=)('=')
      LiteralExpressionImpl
        LiteralIotaImpl
          PsiElement(IDENTIFIER)('iota')
    PsiElement(;)(';')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('A2')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
    PsiWhiteSpace(' ')
    PsiElement())(')')
  PsiWhiteSpace('\n')
  ConstDeclarationsImpl
    PsiElement(KEYWORD_CONST)('const')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('T')
      PsiElement(=)('=')
      LiteralExpressionImpl
        LiteralIotaImpl
          PsiElement(IDENTIFIER)('iota')
    PsiElement(;)(';')
    PsiWhiteSpace('\n')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('A3')
      PsiElement(,)(',')
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('B1')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_INT)('5')
      PsiElement(,)(',')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_INT)('3')
    PsiWhiteSpace('\n')
    PsiElement())(')')
  PsiWhiteSpace('\n')
  ConstDeclarationsImpl
    PsiElement(KEYWORD_CONST)('const')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('V')
      PsiElement(=)('=')
      LiteralExpressionImpl
        LiteralIotaImpl
          PsiElement(IDENTIFIER)('iota')
    PsiElement(;)(';')
    PsiWhiteSpace(' ')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('A4')
      PsiElement(,)(',')
      PsiWhiteSpace(' ')
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('B2')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      AdditiveExpressionImpl
        LiteralExpressionImpl
          LiteralIntegerImpl
            PsiElement(LITERAL_INT)('1')
        PsiWhiteSpace(' ')
        PsiElement(+)('+')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIntegerImpl
            PsiElement(LITERAL_INT)('2')
    PsiElement())(')')
  PsiWhiteSpace('\n')
  ConstDeclarationsImpl
    PsiElement(KEYWORD_CONST)('const')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('B3')
      PsiElement(=)('=')
      LiteralExpressionImpl
        LiteralIotaImpl
          PsiElement(IDENTIFIER)('iota')
    PsiElement(;)(';')
    PsiWhiteSpace('\n')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('A5')
      PsiElement(,)(',')
      PsiWhiteSpace(' ')
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('B4')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_INT)('5')
    PsiWhiteSpace('\n')
    PsiElement())(')')
