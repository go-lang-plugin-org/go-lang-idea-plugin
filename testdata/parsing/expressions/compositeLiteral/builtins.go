package main
import "ast"
var e = &ast.ArrayType{lbrack, len, elt}
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
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"ast"')
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
      UnaryExpressionImpl
        PsiElement(&)('&')
        LiteralExpressionImpl
          LiteralCompositeImpl
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('ast')
                PsiElement(.)('.')
                PsiElement(IDENTIFIER)('ArrayType')
            LiteralCompositeValueImpl
              PsiElement({)('{')
              LiteralCompositeElementImpl
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('lbrack')
              PsiElement(,)(',')
              PsiWhiteSpace(' ')
              LiteralCompositeElementImpl
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('len')
              PsiElement(,)(',')
              PsiWhiteSpace(' ')
              LiteralCompositeElementImpl
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('elt')
	      PsiElement(})('}')
