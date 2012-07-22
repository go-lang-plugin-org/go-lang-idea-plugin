package main
import a "a"
var x = a.A()
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  ImportDeclarationsImpl
    PsiElement(KEYWORD_IMPORT)('import')
    PsiWhiteSpace(' ')
    ImportSpecImpl
      PackageReferenceImpl
        PsiElement(IDENTIFIER)('a')
      PsiWhiteSpace(' ')
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"a"')
  PsiWhiteSpace('\n')
  VarDeclarationsImpl
    PsiElement(KEYWORD_VAR)('var')
    PsiWhiteSpace(' ')
    VarDeclarationImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('x')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      CallOrConversionExpressionImpl
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('a')
            PsiElement(.)('.')
            PsiElement(IDENTIFIER)('A')
        PsiElement(()('(')
        PsiElement())(')')
