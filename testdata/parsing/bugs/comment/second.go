package main

import "fmt"

func main() {
    /* sdsds

    fmt.Printf("Hello world3!")

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
    ImportSpecImpl
      LiteralStringImpl
        PsiElement(LITERAL_STRING)('"fmt"')
  PsiWhiteSpace('\n\n')
  FunctionDeclaration(main)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
    PsiElement(()('(')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiErrorElement:right.curly.expected
        <empty list>
  PsiWhiteSpace('\n')
  PsiWhiteSpace('    ')
  PsiComment(ML_COMMENT)('/* sdsds\n\n    fmt.Printf("Hello world3!")\n\n}')
