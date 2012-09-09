package main
var e = m["foo"]
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
      IndexExpressionImpl
	LiteralExpressionImpl
	  LiteralIdentifierImpl
	    PsiElement(IDENTIFIER)('m')
	PsiElement([)('[')
	LiteralExpressionImpl
	  LiteralStringImpl
	    PsiElement(LITERAL_STRING)('"foo"')
	PsiElement(])(']')
