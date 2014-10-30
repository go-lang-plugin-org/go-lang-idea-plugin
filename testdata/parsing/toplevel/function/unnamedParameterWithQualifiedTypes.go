package main
import "io"

func f(string, uint32, io.Reader) (bool, error) {
	return false, nil
}

func test(a string, x func (string, uint32, io.Reader) (bool, error)) {

}

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
        PsiElement(LITERAL_STRING)('"io"')
  PsiWhiteSpace('\n\n')
  FunctionDeclaration(f)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('f')
    PsiElement(()('(')
    FunctionParameterListImpl
      FunctionParameterImpl
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('string')
      PsiElement(,)(',')
      PsiWhiteSpace(' ')
      FunctionParameterImpl
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('uint32')
      PsiElement(,)(',')
      PsiWhiteSpace(' ')
      FunctionParameterImpl
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('io')
          PsiElement(.)('.')
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('Reader')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    FunctionResult
      PsiElement(()('(')
      FunctionParameterListImpl
        FunctionParameterImpl
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('bool')
        PsiElement(,)(',')
        PsiWhiteSpace(' ')
        FunctionParameterImpl
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('error')
      PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('\t')
      ReturnStmtImpl
        PsiElement(KEYWORD_RETURN)('return')
        PsiWhiteSpace(' ')
        ExpressionListImpl
          LiteralExpressionImpl
            LiteralBoolImpl
              PsiElement(IDENTIFIER)('false')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('nil')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
  PsiWhiteSpace('\n\n')
  FunctionDeclaration(test)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('test')
    PsiElement(()('(')
    FunctionParameterListImpl
      FunctionParameterImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('a')
        PsiWhiteSpace(' ')
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('string')
      PsiElement(,)(',')
      PsiWhiteSpace(' ')
      FunctionParameterImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('x')
        PsiWhiteSpace(' ')
        TypeFunctionImpl
          PsiElement(KEYWORD_FUNC)('func')
          PsiWhiteSpace(' ')
          PsiElement(()('(')
          FunctionParameterListImpl
            FunctionParameterImpl
              TypeNameImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('string')
            PsiElement(,)(',')
            PsiWhiteSpace(' ')
            FunctionParameterImpl
              TypeNameImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('uint32')
            PsiElement(,)(',')
            PsiWhiteSpace(' ')
            FunctionParameterImpl
              TypeNameImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('io')
                PsiElement(.)('.')
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('Reader')
          PsiElement())(')')
          PsiWhiteSpace(' ')
          FunctionResult
            PsiElement(()('(')
            FunctionParameterListImpl
              FunctionParameterImpl
                TypeNameImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('bool')
              PsiElement(,)(',')
              PsiWhiteSpace(' ')
              FunctionParameterImpl
                TypeNameImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('error')
            PsiElement())(')')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n\n')
      PsiElement(})('}')
  PsiWhiteSpace('\n')
