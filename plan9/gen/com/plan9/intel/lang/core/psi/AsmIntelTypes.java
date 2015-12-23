// This is a generated file. Not intended for manual editing.
package com.plan9.intel.lang.core.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.plan9.intel.lang.core.lexer.AsmIntelTokenType;
import com.plan9.intel.lang.core.psi.impl.*;

public interface AsmIntelTypes {

  IElementType FRAME_SIZE = new AsmIntelElementType("FRAME_SIZE");
  IElementType FUNCTION = new AsmIntelElementType("FUNCTION");
  IElementType FUNCTION_BODY = new AsmIntelElementType("FUNCTION_BODY");
  IElementType FUNCTION_FLAGS = new AsmIntelElementType("FUNCTION_FLAGS");
  IElementType FUNCTION_HEADER = new AsmIntelElementType("FUNCTION_HEADER");
  IElementType INSTRUCTION_STMT = new AsmIntelElementType("INSTRUCTION_STMT");
  IElementType LITERAL = new AsmIntelElementType("LITERAL");
  IElementType OPERANDS = new AsmIntelElementType("OPERANDS");
  IElementType PREPROCESSOR_DIRECTIVE = new AsmIntelElementType("PREPROCESSOR_DIRECTIVE");
  IElementType STATEMENT = new AsmIntelElementType("STATEMENT");

  IElementType BIT_OR = new AsmIntelTokenType("|");
  IElementType COLON = new AsmIntelTokenType(":");
  IElementType COMMA = new AsmIntelTokenType(",");
  IElementType FLAG = new AsmIntelTokenType("FLAG");
  IElementType HEX = new AsmIntelTokenType("hex");
  IElementType IDENTIFIER = new AsmIntelTokenType("identifier");
  IElementType IMPORT = new AsmIntelTokenType("import");
  IElementType INSTRUCTION = new AsmIntelTokenType("INSTRUCTION");
  IElementType INT = new AsmIntelTokenType("int");
  IElementType LABEL = new AsmIntelTokenType("LABEL");
  IElementType LPAREN = new AsmIntelTokenType("(");
  IElementType PSEUDO_REG = new AsmIntelTokenType("PSEUDO_REG");
  IElementType RPAREN = new AsmIntelTokenType(")");
  IElementType STRING = new AsmIntelTokenType("STRING");
  IElementType TEXT = new AsmIntelTokenType("TEXT");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == FRAME_SIZE) {
        return new AsmIntelFrameSizeImpl(node);
      }
      else if (type == FUNCTION) {
        return new AsmIntelFunctionImpl(node);
      }
      else if (type == FUNCTION_BODY) {
        return new AsmIntelFunctionBodyImpl(node);
      }
      else if (type == FUNCTION_FLAGS) {
        return new AsmIntelFunctionFlagsImpl(node);
      }
      else if (type == FUNCTION_HEADER) {
        return new AsmIntelFunctionHeaderImpl(node);
      }
      else if (type == INSTRUCTION_STMT) {
        return new AsmIntelInstructionStmtImpl(node);
      }
      else if (type == LITERAL) {
        return new AsmIntelLiteralImpl(node);
      }
      else if (type == OPERANDS) {
        return new AsmIntelOperandsImpl(node);
      }
      else if (type == PREPROCESSOR_DIRECTIVE) {
        return new AsmIntelPreprocessorDirectiveImpl(node);
      }
      else if (type == STATEMENT) {
        return new AsmIntelStatementImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
