package main
type T struct { x int }
------
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiElement(WS_NEW_LINES)('\n')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    TypeSpecImpl
      TypeNameDeclaration(T)
        PsiElement(IDENTIFIER)('T')
      PsiWhiteSpace(' ')
      TypeStructImpl
        PsiElement(KEYWORD_STRUCT)('struct')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace(' ')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('x')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
        PsiWhiteSpace(' ')
        PsiElement(})('}')
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiElement(WS_NEW_LINES)('\n')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    TypeSpecImpl
      TypeNameDeclaration(T)
        PsiElement(IDENTIFIER)('T')
      PsiWhiteSpace(' ')
      TypeStructImpl
        PsiElement(KEYWORD_STRUCT)('struct')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace(' ')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('x')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
        PsiWhiteSpace(' ')
        PsiElement(})('}')
  PsiElement(WS_NEW_LINES)('\n')
  PsiErrorElement:unknown.token
    PsiElement(--)('--')
  PsiErrorElement:unknown.token
    PsiElement(--)('--')
  PsiErrorElement:unknown.token
    PsiElement(--)('--')
  PsiElement(WS_NEW_LINES)('\n')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('Go')
  PsiWhiteSpace(' ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('file')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('  ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PackageDeclaration')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('main')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('    ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiElement')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('KEYWORD_PACKAGE')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(WRONG)(''')
  PsiErrorElement:unknown.token
    PsiElement(KEYWORD_PACKAGE)('package')
  PsiErrorElement:unknown.token
    PsiElement(WRONG)(''')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('    ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiWhiteSpace')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(LITERAL_CHAR)('' '')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('    ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiElement')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('IDENTIFIER')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(WRONG)(''')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('main')
  PsiErrorElement:unknown.token
    PsiElement(WRONG)(''')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('  ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiElement')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('WS_NEW_LINES')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(LITERAL_CHAR)(''\n'')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('  ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('TypeDeclarationsImpl')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('    ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiElement')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('KEYWORD_TYPE')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(WRONG)(''')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiErrorElement:error.identifier.expected
      <empty list>
  PsiErrorElement:unknown.token
    PsiElement(WRONG)(''')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('    ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiWhiteSpace')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(LITERAL_CHAR)('' '')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('    ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('TypeSpecImpl')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('      ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('TypeNameDeclaration')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('T')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('        ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiElement')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('IDENTIFIER')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(LITERAL_CHAR)(''T'')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('      ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiWhiteSpace')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(LITERAL_CHAR)('' '')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('      ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('TypeStructImpl')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('        ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiElement')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('KEYWORD_STRUCT')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(WRONG)(''')
  PsiErrorElement:unknown.token
    PsiElement(KEYWORD_STRUCT)('struct')
  PsiErrorElement:unknown.token
    PsiElement(WRONG)(''')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('        ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiWhiteSpace')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(LITERAL_CHAR)('' '')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('        ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiElement')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement({)('{')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(LITERAL_CHAR)(''{'')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('        ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiWhiteSpace')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(LITERAL_CHAR)('' '')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('        ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('TypeStructFieldImpl')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('          ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('LiteralIdentifierImpl')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('            ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiElement')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('IDENTIFIER')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(LITERAL_CHAR)(''x'')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('          ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiWhiteSpace')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(LITERAL_CHAR)('' '')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('          ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('TypeNameImpl')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('            ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('LiteralIdentifierImpl')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('              ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiElement')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('IDENTIFIER')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(WRONG)(''')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('int')
  PsiErrorElement:unknown.token
    PsiElement(WRONG)(''')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('        ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiWhiteSpace')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(LITERAL_CHAR)('' '')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiElement(WS_NEW_LINES)('\n')
  PsiWhiteSpace('        ')
  PsiErrorElement:unknown.token
    PsiElement(IDENTIFIER)('PsiElement')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(})('}')
  PsiErrorElement:unknown.token
    PsiElement())(')')
  PsiErrorElement:unknown.token
    PsiElement(()('(')
  PsiErrorElement:unknown.token
    PsiElement(LITERAL_CHAR)(''}'')
  PsiErrorElement:unknown.token
    PsiElement())(')')