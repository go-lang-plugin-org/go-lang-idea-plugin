package main
var e = a[1:2]
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  VarDeclarationsImpl
    PsiElement(KEYWORD_VAR)('var')
    PsiWhiteSpace(' ')
    VarDeclarationImpl
      LiteralIdentifierImpl
	PsiElement(IDENTIFIER)('e')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      SliceExpressionImpl
	LiteralExpressionImpl
	  LiteralIdentifierImpl
	    PsiElement(IDENTIFIER)('a')
	PsiElement([)('[')
	LiteralExpressionImpl
	  LiteralIntegerImpl
	    PsiElement(LITERAL_INT)('1')
	PsiElement(:)(':')
	LiteralExpressionImpl
	  LiteralIntegerImpl
	    PsiElement(LITERAL_INT)('2')
	PsiElement(])(']')
