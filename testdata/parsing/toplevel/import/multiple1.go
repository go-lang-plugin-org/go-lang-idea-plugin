package main
import ( "container/vector"; . "fmt"; _ "go/ast"; P "go/scanner"; "go/token" )
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
    PsiElement(()('(')
    PsiWhiteSpace(' ')
    ImportSpecImpl
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"container/vector"')
    PsiElement(;)(';')
    PsiWhiteSpace(' ')
    ImportSpecImpl
      PackageReferenceImpl
        PsiElement(.)('.')
      PsiWhiteSpace(' ')
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"fmt"')
    PsiElement(;)(';')
    PsiWhiteSpace(' ')
    ImportSpecImpl
      PackageReferenceImpl
        PsiElement(IDENTIFIER)('_')
      PsiWhiteSpace(' ')
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"go/ast"')
    PsiElement(;)(';')
    PsiWhiteSpace(' ')
    ImportSpecImpl
      PackageReferenceImpl
        PsiElement(IDENTIFIER)('P')
      PsiWhiteSpace(' ')
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"go/scanner"')
    PsiElement(;)(';')
    PsiWhiteSpace(' ')
    ImportSpecImpl
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"go/token"')
    PsiWhiteSpace(' ')
    PsiElement())(')')
