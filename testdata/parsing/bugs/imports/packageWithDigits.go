package main

import (
	"labix.org/v2/mgo/bson"
)

func main() {
	q := bson.M{"name":0}
}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n\n')
  ImportDeclarationsImpl
    PsiElement(KEYWORD_IMPORT)('import')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('\t')
    ImportSpecImpl
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"labix.org/v2/mgo/bson"')
    PsiWhiteSpace('\n')
    PsiElement())(')')
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
      PsiWhiteSpace('\t')
      ShortVarStmtImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('q')
        PsiWhiteSpace(' ')
        PsiElement(:=)(':=')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralCompositeImpl
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('bson')
                PsiElement(.)('.')
                PsiElement(IDENTIFIER)('M')
            LiteralCompositeValueImpl
              PsiElement({)('{')
              LiteralCompositeElementImpl
                CompositeLiteralElementKey
                  LiteralExpressionImpl
                    LiteralStringImpl
                      PsiElement(LITERAL_STRING)('"name"')
                PsiElement(:)(':')
                LiteralExpressionImpl
                  LiteralIntegerImpl
                    PsiElement(LITERAL_INT)('0')
              PsiElement(})('}')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
