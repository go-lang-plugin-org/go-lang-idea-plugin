package main

type cartesian struct

func createSolver(questions chan polar) chan cartesian {
    answers := make(chan cartesian)
    go func() {
        for {
            polarCoord := <-questions
            answers <-cartesian{x,y}
        }
    }()
    return answers
}

func main() {
}

/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n\n')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    TypeSpecImpl
      TypeNameDeclaration(cartesian)
        PsiElement(IDENTIFIER)('cartesian')
      PsiErrorElement:Type definition expected
        <empty list>
  PsiWhiteSpace(' ')
  PsiErrorElement:';' or newline expected
    PsiElement(KEYWORD_STRUCT)('struct')
  PsiWhiteSpace('\n\n')
  FunctionDeclaration(createSolver)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('createSolver')
    PsiElement(()('(')
    FunctionParameterListImpl
      FunctionParameterImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('questions')
        PsiWhiteSpace(' ')
        TypeChanBidiImpl
          PsiElement(KEYWORD_CHAN)('chan')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('polar')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    FunctionResult
      FunctionParameterListImpl
        FunctionParameterImpl
          TypeChanBidiImpl
            PsiElement(KEYWORD_CHAN)('chan')
            PsiWhiteSpace(' ')
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('cartesian')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      ShortVarStmtImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('answers')
        PsiWhiteSpace(' ')
        PsiElement(:=)(':=')
        PsiWhiteSpace(' ')
        BuiltInCallExpressionImpl
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('make')
          PsiElement(()('(')
          TypeChanBidiImpl
            PsiElement(KEYWORD_CHAN)('chan')
            PsiWhiteSpace(' ')
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('cartesian')
          PsiElement())(')')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      GoStmtImpl
        PsiElement(KEYWORD_GO)('go')
        PsiWhiteSpace(' ')
        CallOrConversionExpressionImpl
          LiteralExpressionImpl
            LiteralFunctionImpl
              PsiElement(KEYWORD_FUNC)('func')
              PsiElement(()('(')
              PsiElement())(')')
              PsiWhiteSpace(' ')
              BlockStmtImpl
                PsiElement({)('{')
                PsiWhiteSpace('\n')
                PsiWhiteSpace('        ')
                ForWithConditionStmtImpl
                  PsiElement(KEYWORD_FOR)('for')
                  PsiWhiteSpace(' ')
                  BlockStmtImpl
                    PsiElement({)('{')
                    PsiWhiteSpace('\n')
                    PsiWhiteSpace('            ')
                    ShortVarStmtImpl
                      LiteralIdentifierImpl
                        PsiElement(IDENTIFIER)('polarCoord')
                      PsiWhiteSpace(' ')
                      PsiElement(:=)(':=')
                      PsiWhiteSpace(' ')
                      UnaryExpressionImpl
                        PsiElement(<-)('<-')
                        LiteralExpressionImpl
                          LiteralIdentifierImpl
                            PsiElement(IDENTIFIER)('questions')
                    PsiWhiteSpace('\n')
                    PsiWhiteSpace('            ')
                    SendStmtImpl
                      LiteralExpressionImpl
                        LiteralIdentifierImpl
                          PsiElement(IDENTIFIER)('answers')
                      PsiWhiteSpace(' ')
                      PsiElement(<-)('<-')
                      LiteralExpressionImpl
                        LiteralCompositeImpl
                          TypeNameImpl
                            LiteralIdentifierImpl
                              PsiElement(IDENTIFIER)('cartesian')
                          LiteralCompositeValueImpl
                            PsiElement({)('{')
                            LiteralCompositeElementImpl
                              LiteralExpressionImpl
                                LiteralIdentifierImpl
                                  PsiElement(IDENTIFIER)('x')
                            PsiElement(,)(',')
                            LiteralCompositeElementImpl
                              LiteralExpressionImpl
                                LiteralIdentifierImpl
                                  PsiElement(IDENTIFIER)('y')
                            PsiElement(})('}')
                    PsiWhiteSpace('\n')
                    PsiWhiteSpace('        ')
                    PsiElement(})('}')
                PsiWhiteSpace('\n')
                PsiWhiteSpace('    ')
                PsiElement(})('}')
          PsiElement(()('(')
          PsiElement())(')')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      ReturnStmtImpl
        PsiElement(KEYWORD_RETURN)('return')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('answers')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
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
      PsiElement(})('}')
  PsiWhiteSpace('\n')
