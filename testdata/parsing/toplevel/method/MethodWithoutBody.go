package main

// that point, the program is terminated and the error condition is reported,
// including the value of the argument to panic. This termination sequence
// is called panicking and can be controlled by the built-in function
// recover.
func panic(v interface{})

/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n\n')
  PsiComment(SL_COMMENT)('// that point, the program is terminated and the error condition is reported,')
  PsiWhiteSpace('\n')
  PsiComment(SL_COMMENT)('// including the value of the argument to panic. This termination sequence')
  PsiWhiteSpace('\n')
  PsiComment(SL_COMMENT)('// is called panicking and can be controlled by the built-in function')
  PsiWhiteSpace('\n')
  PsiComment(SL_COMMENT)('// recover.')
  PsiWhiteSpace('\n')
  FunctionDeclaration(panic)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('panic')
    PsiElement(()('(')
    FunctionParameterListImpl
      FunctionParameterImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('v')
        PsiWhiteSpace(' ')
        TypeInterfaceImpl
          PsiElement(KEYWORD_INTERFACE)('interface')
          PsiElement({)('{')
          PsiElement(})('}')
    PsiElement())(')')
  PsiWhiteSpace('\n')
