package main
import (
	"container/vector"
	. "fmt"
	_ "go/ast"
	P "go/scanner"
	"go/token"
)
-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  ImportDeclarationsImpl
    PsiElement(KEYWORD_IMPORT)('import')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('\t')
    ImportSpecImpl
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"container/vector"')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('\t')
    ImportSpecImpl
      PackageReferenceImpl
        PsiElement(.)('.')
      PsiWhiteSpace(' ')
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"fmt"')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('\t')
    ImportSpecImpl
      PackageReferenceImpl
        PsiElement(IDENTIFIER)('_')
      PsiWhiteSpace(' ')
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"go/ast"')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('\t')
    ImportSpecImpl
      PackageReferenceImpl
        PsiElement(IDENTIFIER)('P')
      PsiWhiteSpace(' ')
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"go/scanner"')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('\t')
    ImportSpecImpl
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"go/token"')
    PsiWhiteSpace('\n')
    PsiElement())(')')
