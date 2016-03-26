package com.plan9.intel.lang.core.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import static com.plan9.intel.ide.highlighting.AsmIntelLexerTokens.*;

%%

%public 
%class _AsmIntelHighlightingLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

%{
  public _AsmIntelHighlightingLexer() {
    this((java.io.Reader)null);
  }
%}

WSNL          = [ \r\n\t]+
WS            = [ \t\f]
LINE_COMMENT  = "//" [^\r\n]*
STR           = "\""
LETTER        = [:letter:] | "_" | \u00B7 | \u2215
DIGIT         = [:digit:]

HEX_DIGIT     = [0-9A-Fa-f]
INT_DIGIT     = [0-9]
OPERATOR      = [*/%&|+~\^-]

NUM_INT       = "0" | ([1-9] {INT_DIGIT}*)
NUM_HEX       = ("0x" | "0X") {HEX_DIGIT}+

IDENT         = {LETTER} ( {LETTER} | {DIGIT} )*

LABEL         = {IDENT} ":"

DIRECTIVE     = "TEXT" | "GLOBL" | "DATA" | "PCDATA" | "FUNCDATA"

PSEUDO_REG    = "FP" | "PC" | "SB" | "SP"

REG           = "AL" | "CL" | "DL" | "BL" | "SPB" | "BPB" | "SIB" | "DIB" | "R8B" | "R9B" | "R10B" | "R11B" | "R12B" | "R13B" | "R14B" | "R15B" | "AX" |
                "CX" | "DX" | "BX" | "SP" | "BP" | "SI" | "DI" | "R8" | "R9" | "R10" | "R11" | "R12" | "R13" | "R14" | "R15" | "AH" | "CH" | "DH" | "BH" |
                "F0" | "F1" | "F2" | "F3" | "F4" | "F5" | "F6" | "F7" | "M0" | "M1" | "M2" | "M3" | "M4" | "M5" | "M6" | "M7" | "X0" | "X1" | "X2" | "X3" |
                "X4" | "X5" | "X6" | "X7" | "X8" | "X9" | "X10" | "X11" | "X12" | "X13" | "X14" | "X15" | "CS" | "SS" | "DS" | "ES" | "FS" | "GS" | "GDTR" |
                "IDTR" | "LDTR" | "MSW" | "TASK" | "CR0" | "CR1" | "CR2" | "CR3" | "CR4" | "CR5" | "CR6" | "CR7" | "CR8" | "CR9" | "CR10" | "CR11" | "CR12"
                | "CR13" | "CR14" | "CR15" | "DR0" | "DR1" | "DR2" | "DR3" | "DR4" | "DR5" | "DR6" | "DR7" | "TR0" | "TR1" | "TR2" | "TR3" | "TR4" | "TR5" |
                "TR6" | "TR7" | "TLS"

PSEUDO_INS    = "CALL" | "END" | "JMP" | "NOP" | "RET"

// all instructions, including alternates per https://github.com/golang/go/blob/8db371b3d58a1c139f0854738f9962de05ca5d7a/src/cmd/asm/internal/arch/arch.go#L114
INS           = "AAA" | "AAD" | "AAM" | "AAS" | "ADCB" | "ADCL" | "ADCQ" | "ADCW" | "ADDB" | "ADDL" | "ADDPD" | "ADDPS" | "ADDQ" | "ADDSD" | "ADDSS" |
                "ADDW" | "ADJSP" | "AESDEC" | "AESDECLAST" | "AESENC" | "AESENCLAST" | "AESIMC" | "AESKEYGENASSIST" | "ANDB" | "ANDL" | "ANDNPD" | "ANDNPS"
                | "ANDPD" | "ANDPS" | "ANDQ" | "ANDW" | "ARPL" | "BOUNDL" | "BOUNDW" | "BSFL" | "BSFQ" | "BSFW" | "BSRL" | "BSRQ" | "BSRW" | "BSWAPL" |
                "BSWAPQ" | "BTCL" | "BTCQ" | "BTCW" | "BTL" | "BTQ" | "BTRL" | "BTRQ" | "BTRW" | "BTSL" | "BTSQ" | "BTSW" | "BTW" | "BYTE" | "CDQ" | "CLC" |
                "CLD" | "CLI" | "CLTS" | "CMC" | "CMOVLCC" | "CMOVLCS" | "CMOVLEQ" | "CMOVLGE" | "CMOVLGT" | "CMOVLHI" | "CMOVLLE" | "CMOVLLS" | "CMOVLLT" |
                "CMOVLMI" | "CMOVLNE" | "CMOVLOC" | "CMOVLOS" | "CMOVLPC" | "CMOVLPL" | "CMOVLPS" | "CMOVQCC" | "CMOVQCS" | "CMOVQEQ" | "CMOVQGE" |
                "CMOVQGT" | "CMOVQHI" | "CMOVQLE" | "CMOVQLS" | "CMOVQLT" | "CMOVQMI" | "CMOVQNE" | "CMOVQOC" | "CMOVQOS" | "CMOVQPC" | "CMOVQPL" |
                "CMOVQPS" | "CMOVWCC" | "CMOVWCS" | "CMOVWEQ" | "CMOVWGE" | "CMOVWGT" | "CMOVWHI" | "CMOVWLE" | "CMOVWLS" | "CMOVWLT" | "CMOVWMI" |
                "CMOVWNE" | "CMOVWOC" | "CMOVWOS" | "CMOVWPC" | "CMOVWPL" | "CMOVWPS" | "CMPB" | "CMPL" | "CMPPD" | "CMPPS" | "CMPQ" | "CMPSB" | "CMPSD" |
                "CMPSL" | "CMPSQ" | "CMPSS" | "CMPSW" | "CMPW" | "CMPXCHG8B" | "CMPXCHGB" | "CMPXCHGL" | "CMPXCHGQ" | "CMPXCHGW" | "COMISD" | "COMISS" |
                "CPUID" | "CQO" | "CRC32B" | "CRC32Q" | "CVTPD2PL" | "CVTPD2PS" | "CVTPL2PD" | "CVTPL2PS" | "CVTPS2PD" | "CVTPS2PL" | "CVTSD2SL" |
                "CVTSD2SQ" | "CVTSD2SS" | "CVTSL2SD" | "CVTSL2SS" | "CVTSQ2SD" | "CVTSQ2SS" | "CVTSS2SD" | "CVTSS2SL" | "CVTSS2SQ" | "CVTTPD2PL" |
                "CVTTPS2PL" | "CVTTSD2SL" | "CVTTSD2SQ" | "CVTTSS2SL" | "CVTTSS2SQ" | "CWD" | "DAA" | "DAS" | "DECB" | "DECL" | "DECQ" | "DECW" | "DIVB" |
                "DIVL" | "DIVPD" | "DIVPS" | "DIVQ" | "DIVSD" | "DIVSS" | "DIVW" | "EMMS" | "ENTER" | "F2XM1" | "FABS" | "FADDD" | "FADDDP" | "FADDF" |
                "FADDL" | "FADDW" | "FCHS" | "FCLEX" | "FCMOVCC" | "FCMOVCS" | "FCMOVEQ" | "FCMOVHI" | "FCMOVLS" | "FCMOVNE" | "FCMOVNU" | "FCMOVUN" |
                "FCOMB" | "FCOMBP" | "FCOMD" | "FCOMDP" | "FCOMDPP" | "FCOMF" | "FCOMFP" | "FCOMI" | "FCOMIP" | "FCOML" | "FCOMLP" | "FCOMW" | "FCOMWP" |
                "FCOS" | "FDECSTP" | "FDIVD" | "FDIVDP" | "FDIVF" | "FDIVL" | "FDIVRD" | "FDIVRDP" | "FDIVRF" | "FDIVRL" | "FDIVRW" | "FDIVW" | "FFREE" |
                "FINCSTP" | "FINIT" | "FLD1" | "FLDCW" | "FLDENV" | "FLDL2E" | "FLDL2T" | "FLDLG2" | "FLDLN2" | "FLDPI" | "FLDZ" | "FMOVB" | "FMOVBP" |
                "FMOVD" | "FMOVDP" | "FMOVF" | "FMOVFP" | "FMOVL" | "FMOVLP" | "FMOVV" | "FMOVVP" | "FMOVW" | "FMOVWP" | "FMOVX" | "FMOVXP" | "FMULD" |
                "FMULDP" | "FMULF" | "FMULL" | "FMULW" | "FNOP" | "FPATAN" | "FPREM" | "FPREM1" | "FPTAN" | "FRNDINT" | "FRSTOR" | "FSAVE" | "FSCALE" |
                "FSIN" | "FSINCOS" | "FSQRT" | "FSTCW" | "FSTENV" | "FSTSW" | "FSUBD" | "FSUBDP" | "FSUBF" | "FSUBL" | "FSUBRD" | "FSUBRDP" | "FSUBRF" |
                "FSUBRL" | "FSUBRW" | "FSUBW" | "FTST" | "FUCOM" | "FUCOMI" | "FUCOMIP" | "FUCOMP" | "FUCOMPP" | "FXAM" | "FXCHD" | "FXRSTOR" | "FXRSTOR64"
                | "FXSAVE" | "FXSAVE64" | "FXTRACT" | "FYL2X" | "FYL2XP1" | "HLT" | "IDIVB" | "IDIVL" | "IDIVQ" | "IDIVW" | "IMUL3Q" | "IMULB" | "IMULL" |
                "IMULQ" | "IMULW" | "INB" | "INCB" | "INCL" | "INCQ" | "INCW" | "INL" | "INSB" | "INSL" | "INSW" | "INT" | "INTO" | "INVD" | "INVLPG" |
                "INW" | "IRETL" | "IRETQ" | "IRETW" | "JA" | "JAE" | "JB" | "JBE" | "JC" | "JCC" | "JCS" | "JCXZL" | "JCXZQ" | "JCXZW" | "JE" | "JEQ" | "JG"
                | "JGE" | "JGT" | "JHI" | "JHS" | "JL" | "JLE" | "JLO" | "JLS" | "JLT" | "JMI" | "JNA" | "JNAE" | "JNB" | "JNBE" | "JNC" | "JNE" | "JNG" |
                "JNGE" | "JNL" | "JNLE" | "JNO" | "JNP" | "JNS" | "JNZ" | "JO" | "JOC" | "JOS" | "JP" | "JPC" | "JPE" | "JPL" | "JPO" | "JPS" | "JS" | "JZ"
                | "LAHF" | "LARL" | "LARW" | "LAST" | "LDMXCSR" | "LEAL" | "LEAQ" | "LEAVEL" | "LEAVEQ" | "LEAVEW" | "LEAW" | "LFENCE" | "LOCK" | "LODSB" |
                "LODSL" | "LODSQ" | "LODSW" | "LONG" | "LOOP" | "LOOPEQ" | "LOOPNE" | "LSLL" | "LSLW" | "MASKMOVDQU" | "MASKMOVOU" | "MASKMOVQ" | "MAXPD" |
                "MAXPS" | "MAXSD" | "MAXSS" | "MFENCE" | "MINPD" | "MINPS" | "MINSD" | "MINSS" | "MODE" | "MOVAPD" | "MOVAPS" | "MOVB" | "MOVBLSX" |
                "MOVBLZX" | "MOVBQSX" | "MOVBQZX" | "MOVBWSX" | "MOVBWZX" | "MOVD" | "MOVDQ2Q" | "MOVHDA" | "MOVHDU" | "MOVHLPS" | "MOVHPD" | "MOVHPS" |
                "MOVL" | "MOVLHPS" | "MOVLPD" | "MOVLPS" | "MOVLQSX" | "MOVLQZX" | "MOVMSKPD" | "MOVMSKPS" | "MOVNTDQ" | "MOVNTHD" | "MOVNTIL" | "MOVNTIQ" |
                "MOVNTO" | "MOVNTPD" | "MOVNTPS" | "MOVNTQ" | "MOVO" | "MOVOA" | "MOVOU" | "MOVQ" | "MOVQL" | "MOVQOZX" | "MOVSB" | "MOVSD" | "MOVSL" |
                "MOVSQ" | "MOVSS" | "MOVSW" | "MOVUPD" | "MOVUPS" | "MOVW" | "MOVWLSX" | "MOVWLZX" | "MOVWQSX" | "MOVWQZX" | "MULB" | "MULL" | "MULPD" |
                "MULPS" | "MULQ" | "MULSD" | "MULSS" | "MULW" | "NEGB" | "NEGL" | "NEGQ" | "NEGW" | "NOTB" | "NOTL" | "NOTQ" | "NOTW" | "ORB" | "ORL" |
                "ORPD" | "ORPS" | "ORQ" | "ORW" | "OUTB" | "OUTL" | "OUTSB" | "OUTSL" | "OUTSW" | "OUTW" | "PACKSSLW" | "PACKSSWB" | "PACKUSWB" | "PADDB" |
                "PADDL" | "PADDQ" | "PADDSB" | "PADDSW" | "PADDUSB" | "PADDUSW" | "PADDW" | "PAND" | "PANDB" | "PANDL" | "PANDN" | "PANDSB" | "PANDSW" |
                "PANDUSB" | "PANDUSW" | "PANDW" | "PAUSE" | "PAVGB" | "PAVGW" | "PCLMULQDQ" | "PCMPEQB" | "PCMPEQL" | "PCMPEQW" | "PCMPGTB" | "PCMPGTL" |
                "PCMPGTW" | "PEXTRW" | "PF2ID" | "PF2IL" | "PF2IW" | "PFACC" | "PFADD" | "PFCMPEQ" | "PFCMPGE" | "PFCMPGT" | "PFMAX" | "PFMIN" | "PFMUL" |
                "PFNACC" | "PFPNACC" | "PFRCP" | "PFRCPI2T" | "PFRCPIT1" | "PFRSQIT1" | "PFRSQRT" | "PFSUB" | "PFSUBR" | "PI2FD" | "PI2FL" | "PI2FW" |
                "PINSRD" | "PINSRQ" | "PINSRW" | "PMADDWL" | "PMAXSW" | "PMAXUB" | "PMINSW" | "PMINUB" | "PMOVMSKB" | "PMULHRW" | "PMULHUW" | "PMULHW" |
                "PMULLW" | "PMULULQ" | "POPAL" | "POPAW" | "POPFL" | "POPFQ" | "POPFW" | "POPL" | "POPQ" | "POPW" | "POR" | "PREFETCHNTA" | "PREFETCHT0" |
                "PREFETCHT1" | "PREFETCHT2" | "PSADBW" | "PSHUFB" | "PSHUFD" | "PSHUFHW" | "PSHUFL" | "PSHUFLW" | "PSHUFW" | "PSLLDQ" | "PSLLL" | "PSLLO" |
                "PSLLQ" | "PSLLW" | "PSRAL" | "PSRAW" | "PSRLDQ" | "PSRLL" | "PSRLO" | "PSRLQ" | "PSRLW" | "PSUBB" | "PSUBL" | "PSUBQ" | "PSUBSB" | "PSUBSW"
                | "PSUBUSB" | "PSUBUSW" | "PSUBW" | "PSWAPL" | "PUNPCKHBW" | "PUNPCKHLQ" | "PUNPCKHQDQ" | "PUNPCKHWL" | "PUNPCKLBW" | "PUNPCKLLQ" |
                "PUNPCKLQDQ" | "PUNPCKLWL" | "PUSHAL" | "PUSHAW" | "PUSHFL" | "PUSHFQ" | "PUSHFW" | "PUSHL" | "PUSHQ" | "PUSHW" | "PXOR" | "QUAD" | "RCLB" |
                "RCLL" | "RCLQ" | "RCLW" | "RCPPS" | "RCPSS" | "RCRB" | "RCRL" | "RCRQ" | "RCRW" | "RDMSR" | "RDPMC" | "RDTSC" | "REP" | "REPN" | "RETFL" |
                "RETFQ" | "RETFW" | "ROLB" | "ROLL" | "ROLQ" | "ROLW" | "RORB" | "RORL" | "RORQ" | "RORW" | "ROUNDPD" | "ROUNDPS" | "ROUNDSD" | "ROUNDSS" |
                "RSM" | "RSQRTPS" | "RSQRTSS" | "SAHF" | "SALB" | "SALL" | "SALQ" | "SALW" | "SARB" | "SARL" | "SARQ" | "SARW" | "SBBB" | "SBBL" | "SBBQ" |
                "SBBW" | "SCASB" | "SCASL" | "SCASQ" | "SCASW" | "SETCC" | "SETCS" | "SETEQ" | "SETGE" | "SETGT" | "SETHI" | "SETLE" | "SETLS" | "SETLT" |
                "SETMI" | "SETNE" | "SETOC" | "SETOS" | "SETPC" | "SETPL" | "SETPS" | "SFENCE" | "SHLB" | "SHLL" | "SHLQ" | "SHLW" | "SHRB" | "SHRL" |
                "SHRQ" | "SHRW" | "SHUFPD" | "SHUFPS" | "SQRTPD" | "SQRTPS" | "SQRTSD" | "SQRTSS" | "STC" | "STD" | "STI" | "STMXCSR" | "STOSB" | "STOSL" |
                "STOSQ" | "STOSW" | "SUBB" | "SUBL" | "SUBPD" | "SUBPS" | "SUBQ" | "SUBSD" | "SUBSS" | "SUBW" | "SWAPGS" | "SYSCALL" | "SYSRET" | "TESTB" |
                "TESTL" | "TESTQ" | "TESTW" | "UCOMISD" | "UCOMISS" | "UNPCKHPD" | "UNPCKHPS" | "UNPCKLPD" | "UNPCKLPS" | "VERR" | "VERW" | "VPAND" |
                "VPBROADCASTB" | "VPCMPEQB" | "VPMOVMSKB" | "VPTEST" | "VPXOR" | "VZEROUPPER" | "WAIT" | "WBINVD" | "WORD" | "WRMSR" | "XABORT" | "XACQUIRE"
                | "XADDB" | "XADDL" | "XADDQ" | "XADDW" | "XBEGIN" | "XCHGB" | "XCHGL" | "XCHGQ" | "XCHGW" | "XEND" | "XLAT" | "XORB" | "XORL" | "XORPD" |
                "XORPS" | "XORQ" | "XORW" | "XRELEASE" | "XTEST"

PREPROCESSOR  = "#include" | "#define" | "#ifdef" | "#else" | "#endif" | "#undef"

FLAG          = "NOPROF" | "DUPOK" | "NOSPLIT" | "RODATA" | "NOPTR" | "WRAPPER" | "NEEDCTXT" | "TLSBSS" | "NOFRAME"

%%

{PREPROCESSOR}        { return PREPROCESSOR; }
{DIRECTIVE}           { return DIRECTIVE; }

{FLAG}                { return FLAG; }
{PSEUDO_REG}          { return PSEUDO_REG; }
{REG}                 { return REGISTER; }
{PSEUDO_INS}          { return PSEUDO_INS; }
{INS}                 { return INSTRUCTION; }
{LINE_COMMENT}        { return LINE_COMMENT; }
{WS}                  { return TokenType.WHITE_SPACE; }
{WSNL}                { return TokenType.WHITE_SPACE; }
{LABEL}               { return LABEL; }
"("                   { return PAREN; }
")"                   { return PAREN; }
","                   { return COMMA; }
{OPERATOR}            { return OPERATOR; }
"<<" | ">>"           { return OPERATOR; }

{NUM_HEX}             { return HEX; }
{NUM_INT}             { return INT; }

{IDENT}               { return IDENTIFIER; }

{STR} [^\"]* {STR}    { return STRING; }

///////////////////////////////////////////////////////////////////////////////////////////////////
// Catch All
///////////////////////////////////////////////////////////////////////////////////////////////////

[^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }