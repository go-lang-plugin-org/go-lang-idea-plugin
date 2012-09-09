package main
var e = interface{}(nil)
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
      CallOrConversionExpressionImpl
	TypeInterfaceImpl
	  PsiElement(KEYWORD_INTERFACE)('interface')
	  PsiElement({)('{')
	  PsiElement(})('}')
	PsiElement(()('(')
	LiteralExpressionImpl
	  LiteralIdentifierImpl
	    PsiElement(IDENTIFIER)('nil')
	PsiElement())(')')
