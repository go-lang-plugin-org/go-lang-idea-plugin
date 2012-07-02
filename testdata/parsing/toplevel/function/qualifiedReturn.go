package main

import "hash"

func NewHash() (hash.Hash) {}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiElement(WS_NEW_LINES)('\n\n')
  ImportDeclarationsImpl
    PsiElement(KEYWORD_IMPORT)('import')
    PsiWhiteSpace(' ')
    ImportSpecImpl
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"hash"')
  PsiElement(WS_NEW_LINES)('\n\n')
  FunctionDeclaration(NewHash)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('NewHash')
    PsiElement(()('(')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    FunctionResult
      PsiElement(()('(')
      FunctionParameterListImpl
        FunctionParameterImpl
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('hash')
              PsiElement(.)('.')
              PsiElement(IDENTIFIER)('Hash')
      PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiElement(})('}')
