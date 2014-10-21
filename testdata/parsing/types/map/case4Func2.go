package main
type myHandler struct {
handlers map[string]func(
    w http.ResponseWriter,
    r *http.Request,
    queues *yqs.Queues) (int)
templates map[string]*template.Template
}

/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    TypeSpecImpl
      TypeNameDeclaration(myHandler)
        PsiElement(IDENTIFIER)('myHandler')
      PsiWhiteSpace(' ')
      TypeStructImpl
        PsiElement(KEYWORD_STRUCT)('struct')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace('\n')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('handlers')
          PsiWhiteSpace(' ')
          TypeMapImpl
            PsiElement(KEYWORD_MAP)('map')
            PsiElement([)('[')
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('string')
            PsiElement(])(']')
            TypeFunctionImpl
              PsiElement(KEYWORD_FUNC)('func')
              PsiElement(()('(')
              PsiWhiteSpace('\n')
              PsiWhiteSpace('    ')
              FunctionParameterListImpl
                FunctionParameterImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('w')
                  PsiWhiteSpace(' ')
                  TypeNameImpl
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('http')
                    PsiElement(.)('.')
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('ResponseWriter')
                PsiElement(,)(',')
                PsiWhiteSpace('\n')
                PsiWhiteSpace('    ')
                FunctionParameterImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('r')
                  PsiWhiteSpace(' ')
                  TypePointerImpl
                    PsiElement(*)('*')
                    TypeNameImpl
                      LiteralIdentifierImpl
                        PsiElement(IDENTIFIER)('http')
                      PsiElement(.)('.')
                      LiteralIdentifierImpl
                        PsiElement(IDENTIFIER)('Request')
                PsiElement(,)(',')
                PsiWhiteSpace('\n')
                PsiWhiteSpace('    ')
                FunctionParameterImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('queues')
                  PsiWhiteSpace(' ')
                  TypePointerImpl
                    PsiElement(*)('*')
                    TypeNameImpl
                      LiteralIdentifierImpl
                        PsiElement(IDENTIFIER)('yqs')
                      PsiElement(.)('.')
                      LiteralIdentifierImpl
                        PsiElement(IDENTIFIER)('Queues')
              PsiElement())(')')
              PsiWhiteSpace(' ')
              FunctionResult
                PsiElement(()('(')
                FunctionParameterListImpl
                  FunctionParameterImpl
                    TypeNameImpl
                      LiteralIdentifierImpl
                        PsiElement(IDENTIFIER)('int')
                PsiElement())(')')
        PsiWhiteSpace('\n')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('templates')
          PsiWhiteSpace(' ')
          TypeMapImpl
            PsiElement(KEYWORD_MAP)('map')
            PsiElement([)('[')
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('string')
            PsiElement(])(']')
            TypePointerImpl
              PsiElement(*)('*')
              TypeNameImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('template')
                PsiElement(.)('.')
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('Template')
        PsiWhiteSpace('\n')
        PsiElement(})('}')
  PsiWhiteSpace('\n')
