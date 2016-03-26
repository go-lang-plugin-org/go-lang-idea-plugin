package com.plan9.intel.lang.core.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.plan9.intel.lang.core.psi.AsmIntelTypes;
import com.intellij.psi.TokenType;
import static com.plan9.intel.lang.core.AsmIntelParserDefinition.*;

%%

%public
%class _AsmIntelLexer
%implements FlexLexer, AsmIntelTypes
%unicode
%function advance
%type IElementType

%{
  public _AsmIntelLexer() {
    this((java.io.Reader)null);
  }
%}

WSNL          = [ \r\n\t]+
WS            = [ \t\f]
LINE_COMMENT  = "//" [^\r\n]*
STR           = "\""
LETTER        = [:letter:] | "_"
DIGIT         = [:digit:]

HEX_DIGIT     = [0-9A-Fa-f]
INT_DIGIT     = [0-9]

NUM_INT       = "0" | ([1-9] {INT_DIGIT}*)
NUM_HEX       = ("0x" | "0X") {HEX_DIGIT}+

IDENT         = {LETTER} ( {LETTER} | {DIGIT} )*

LABEL         = {IDENT} ":"

PSEUDO_REG    = "FP" | "PC" | "SB" | "SP"

INS           = "AAA" | "AAD" | "AAM" | "AAS" | "ADCB" | "ADCL" | "ADCW" | "ADDB" | "ADDL" | "ADDW" | "ADJSP" | "ANDB" | "ANDL" | "ANDW" | "ARPL" | "BOUNDL"
                | "BOUNDW" | "BSFL" | "BSFW" | "BSRL" | "BSRW" | "BTL" | "BTW" | "BTCL" | "BTCW" | "BTRL" | "BTRW" | "BTSL" | "BTSW" | "BYTE" | "CLC" |
                "CLD" | "CLI" | "CLTS" | "CMC" | "CMPB" | "CMPL" | "CMPW" | "CMPSB" | "CMPSL" | "CMPSW" | "DAA" | "DAS" | "DECB" | "DECL" | "DECQ" | "DECW"
                | "DIVB" | "DIVL" | "DIVW" | "ENTER" | "HLT" | "IDIVB" | "IDIVL" | "IDIVW" | "IMULB" | "IMULL" | "IMULW" | "INB" | "INL" | "INW" | "INCB" |
                "INCL" | "INCQ" | "INCW" | "INSB" | "INSL" | "INSW" | "INT" | "INTO" | "IRETL" | "IRETW" | "JCC" | "JCS" | "JCXZL" | "JEQ" | "JGE" | "JGT" |
                "JHI" | "JLE" | "JLS" | "JLT" | "JMI" | "JNE" | "JOC" | "JOS" | "JPC" | "JPL" | "JPS" | "LAHF" | "LARL" | "LARW" | "LEAL" | "LEAW" |
                "LEAVEL" | "LEAVEW" | "LOCK" | "LODSB" | "LODSL" | "LODSW" | "LONG" | "LOOP" | "LOOPEQ" | "LOOPNE" | "LSLL" | "LSLW" | "MOVB" | "MOVL" |
                "MOVW" | "MOVBLSX" | "MOVBLZX" | "MOVBQSX" | "MOVBQZX" | "MOVBWSX" | "MOVBWZX" | "MOVWLSX" | "MOVWLZX" | "MOVWQSX" | "MOVWQZX" | "MOVSB" |
                "MOVSL" | "MOVSW" | "MULB" | "MULL" | "MULW" | "NEGB" | "NEGL" | "NEGW" | "NOTB" | "NOTL" | "NOTW" | "ORB" | "ORL" | "ORW" | "OUTB" | "OUTL"
                | "OUTW" | "OUTSB" | "OUTSL" | "OUTSW" | "PAUSE" | "POPAL" | "POPAW" | "POPFL" | "POPFW" | "POPL" | "POPW" | "PUSHAL" | "PUSHAW" | "PUSHFL"
                | "PUSHFW" | "PUSHL" | "PUSHW" | "RCLB" | "RCLL" | "RCLW" | "RCRB" | "RCRL" | "RCRW" | "REP" | "REPN" | "ROLB" | "ROLL" | "ROLW" | "RORB" |
                "RORL" | "RORW" | "SAHF" | "SALB" | "SALL" | "SALW" | "SARB" | "SARL" | "SARW" | "SBBB" | "SBBL" | "SBBW" | "SCASB" | "SCASL" | "SCASW" |
                "SETCC" | "SETCS" | "SETEQ" | "SETGE" | "SETGT" | "SETHI" | "SETLE" | "SETLS" | "SETLT" | "SETMI" | "SETNE" | "SETOC" | "SETOS" | "SETPC" |
                "SETPL" | "SETPS" | "CDQ" | "CWD" | "SHLB" | "SHLL" | "SHLW" | "SHRB" | "SHRL" | "SHRW" | "STC" | "STD" | "STI" | "STOSB" | "STOSL" |
                "STOSW" | "SUBB" | "SUBL" | "SUBW" | "SYSCALL" | "TESTB" | "TESTL" | "TESTW" | "VERR" | "VERW" | "WAIT" | "WORD" | "XCHGB" | "XCHGL" |
                "XCHGW" | "XLAT" | "XORB" | "XORL" | "XORW" | "FMOVB" | "FMOVBP" | "FMOVD" | "FMOVDP" | "FMOVF" | "FMOVFP" | "FMOVL" | "FMOVLP" | "FMOVV" |
                "FMOVVP" | "FMOVW" | "FMOVWP" | "FMOVX" | "FMOVXP" | "FCOMB" | "FCOMBP" | "FCOMD" | "FCOMDP" | "FCOMDPP" | "FCOMF" | "FCOMFP" | "FCOML" |
                "FCOMLP" | "FCOMW" | "FCOMWP" | "FUCOM" | "FUCOMP" | "FUCOMPP" | "FADDDP" | "FADDW" | "FADDL" | "FADDF" | "FADDD" | "FMULDP" | "FMULW" |
                "FMULL" | "FMULF" | "FMULD" | "FSUBDP" | "FSUBW" | "FSUBL" | "FSUBF" | "FSUBD" | "FSUBRDP" | "FSUBRW" | "FSUBRL" | "FSUBRF" | "FSUBRD" |
                "FDIVDP" | "FDIVW" | "FDIVL" | "FDIVF" | "FDIVD" | "FDIVRDP" | "FDIVRW" | "FDIVRL" | "FDIVRF" | "FDIVRD" | "FXCHD" | "FFREE" | "FLDCW" |
                "FLDENV" | "FRSTOR" | "FSAVE" | "FSTCW" | "FSTENV" | "FSTSW" | "F2XM1" | "FABS" | "FCHS" | "FCLEX" | "FCOS" | "FDECSTP" | "FINCSTP" |
                "FINIT" | "FLD1" | "FLDL2E" | "FLDL2T" | "FLDLG2" | "FLDLN2" | "FLDPI" | "FLDZ" | "FNOP" | "FPATAN" | "FPREM" | "FPREM1" | "FPTAN" |
                "FRNDINT" | "FSCALE" | "FSIN" | "FSINCOS" | "FSQRT" | "FTST" | "FXAM" | "FXTRACT" | "FYL2X" | "FYL2XP1" | "CMPXCHGB" | "CMPXCHGL" |
                "CMPXCHGW" | "CMPXCHG8B" | "CPUID" | "INVD" | "INVLPG" | "LFENCE" | "MFENCE" | "MOVNTIL" | "RDMSR" | "RDPMC" | "RDTSC" | "RSM" | "SFENCE" |
                "SYSRET" | "WBINVD" | "WRMSR" | "XADDB" | "XADDL" | "XADDW" | "CMOVLCC" | "CMOVLCS" | "CMOVLEQ" | "CMOVLGE" | "CMOVLGT" | "CMOVLHI" |
                "CMOVLLE" | "CMOVLLS" | "CMOVLLT" | "CMOVLMI" | "CMOVLNE" | "CMOVLOC" | "CMOVLOS" | "CMOVLPC" | "CMOVLPL" | "CMOVLPS" | "CMOVQCC" |
                "CMOVQCS" | "CMOVQEQ" | "CMOVQGE" | "CMOVQGT" | "CMOVQHI" | "CMOVQLE" | "CMOVQLS" | "CMOVQLT" | "CMOVQMI" | "CMOVQNE" | "CMOVQOC" |
                "CMOVQOS" | "CMOVQPC" | "CMOVQPL" | "CMOVQPS" | "CMOVWCC" | "CMOVWCS" | "CMOVWEQ" | "CMOVWGE" | "CMOVWGT" | "CMOVWHI" | "CMOVWLE" |
                "CMOVWLS" | "CMOVWLT" | "CMOVWMI" | "CMOVWNE" | "CMOVWOC" | "CMOVWOS" | "CMOVWPC" | "CMOVWPL" | "CMOVWPS" | "ADCQ" | "ADDQ" | "ANDQ" |
                "BSFQ" | "BSRQ" | "BTCQ" | "BTQ" | "BTRQ" | "BTSQ" | "CMPQ" | "CMPSQ" | "CMPXCHGQ" | "CQO" | "DIVQ" | "IDIVQ" | "IMULQ" | "IRETQ" | "JCXZQ"
                | "LEAQ" | "LEAVEQ" | "LODSQ" | "MOVQ" | "MOVLQSX" | "MOVLQZX" | "MOVNTIQ" | "MOVSQ" | "MULQ" | "NEGQ" | "NOTQ" | "ORQ" | "POPFQ" | "POPQ" |
                "PUSHFQ" | "PUSHQ" | "RCLQ" | "RCRQ" | "ROLQ" | "RORQ" | "QUAD" | "SALQ" | "SARQ" | "SBBQ" | "SCASQ" | "SHLQ" | "SHRQ" | "STOSQ" | "SUBQ" |
                "TESTQ" | "XADDQ" | "XCHGQ" | "XORQ" | "ADDPD" | "ADDPS" | "ADDSD" | "ADDSS" | "ANDNPD" | "ANDNPS" | "ANDPD" | "ANDPS" | "CMPPD" | "CMPPS" |
                "CMPSD" | "CMPSS" | "COMISD" | "COMISS" | "CVTPD2PL" | "CVTPD2PS" | "CVTPL2PD" | "CVTPL2PS" | "CVTPS2PD" | "CVTPS2PL" | "CVTSD2SL" |
                "CVTSD2SQ" | "CVTSD2SS" | "CVTSL2SD" | "CVTSL2SS" | "CVTSQ2SD" | "CVTSQ2SS" | "CVTSS2SD" | "CVTSS2SL" | "CVTSS2SQ" | "CVTTPD2PL" |
                "CVTTPS2PL" | "CVTTSD2SL" | "CVTTSD2SQ" | "CVTTSS2SL" | "CVTTSS2SQ" | "DIVPD" | "DIVPS" | "DIVSD" | "DIVSS" | "EMMS" | "FXRSTOR" |
                "FXRSTOR64" | "FXSAVE" | "FXSAVE64" | "LDMXCSR" | "MASKMOVOU" | "MASKMOVQ" | "MAXPD" | "MAXPS" | "MAXSD" | "MAXSS" | "MINPD" | "MINPS" |
                "MINSD" | "MINSS" | "MOVAPD" | "MOVAPS" | "MOVOU" | "MOVHLPS" | "MOVHPD" | "MOVHPS" | "MOVLHPS" | "MOVLPD" | "MOVLPS" | "MOVMSKPD" |
                "MOVMSKPS" | "MOVNTO" | "MOVNTPD" | "MOVNTPS" | "MOVNTQ" | "MOVO" | "MOVQOZX" | "MOVSD" | "MOVSS" | "MOVUPD" | "MOVUPS" | "MULPD" | "MULPS"
                | "MULSD" | "MULSS" | "ORPD" | "ORPS" | "PACKSSLW" | "PACKSSWB" | "PACKUSWB" | "PADDB" | "PADDL" | "PADDQ" | "PADDSB" | "PADDSW" | "PADDUSB"
                | "PADDUSW" | "PADDW" | "PANDB" | "PANDL" | "PANDSB" | "PANDSW" | "PANDUSB" | "PANDUSW" | "PANDW" | "PAND" | "PANDN" | "PAVGB" | "PAVGW" |
                "PCMPEQB" | "PCMPEQL" | "PCMPEQW" | "PCMPGTB" | "PCMPGTL" | "PCMPGTW" | "PEXTRW" | "PFACC" | "PFADD" | "PFCMPEQ" | "PFCMPGE" | "PFCMPGT" |
                "PFMAX" | "PFMIN" | "PFMUL" | "PFNACC" | "PFPNACC" | "PFRCP" | "PFRCPIT1" | "PFRCPI2T" | "PFRSQIT1" | "PFRSQRT" | "PFSUB" | "PFSUBR" |
                "PINSRW" | "PINSRD" | "PINSRQ" | "PMADDWL" | "PMAXSW" | "PMAXUB" | "PMINSW" | "PMINUB" | "PMOVMSKB" | "PMULHRW" | "PMULHUW" | "PMULHW" |
                "PMULLW" | "PMULULQ" | "POR" | "PSADBW" | "PSHUFHW" | "PSHUFL" | "PSHUFLW" | "PSHUFW" | "PSHUFB" | "PSLLO" | "PSLLL" | "PSLLQ" | "PSLLW" |
                "PSRAL" | "PSRAW" | "PSRLO" | "PSRLL" | "PSRLQ" | "PSRLW" | "PSUBB" | "PSUBL" | "PSUBQ" | "PSUBSB" | "PSUBSW" | "PSUBUSB" | "PSUBUSW" |
                "PSUBW" | "PSWAPL" | "PUNPCKHBW" | "PUNPCKHLQ" | "PUNPCKHQDQ" | "PUNPCKHWL" | "PUNPCKLBW" | "PUNPCKLLQ" | "PUNPCKLQDQ" | "PUNPCKLWL" |
                "PXOR" | "RCPPS" | "RCPSS" | "RSQRTPS" | "RSQRTSS" | "SHUFPD" | "SHUFPS" | "SQRTPD" | "SQRTPS" | "SQRTSD" | "SQRTSS" | "STMXCSR" | "SUBPD" |
                "SUBPS" | "SUBSD" | "SUBSS" | "UCOMISD" | "UCOMISS" | "UNPCKHPD" | "UNPCKHPS" | "UNPCKLPD" | "UNPCKLPS" | "XORPD" | "XORPS" | "PF2IW" |
                "PF2IL" | "PI2FW" | "PI2FL" | "RETFW" | "RETFL" | "RETFQ" | "SWAPGS" | "MODE" | "CRC32B" | "CRC32Q" | "IMUL3Q" | "PREFETCHT0" | "PREFETCHT1"
                | "PREFETCHT2" | "PREFETCHNTA" | "MOVQL" | "BSWAPL" | "BSWAPQ" | "AESENC" | "AESENCLAST" | "AESDEC" | "AESDECLAST" | "AESIMC" |
                "AESKEYGENASSIST" | "ROUNDPS" | "ROUNDSS" | "ROUNDPD" | "ROUNDSD" | "PSHUFD" | "PCLMULQDQ" | "VZEROUPPER" | "MOVHDU" | "MOVNTHD" | "MOVHDA"
                | "VPCMPEQB" | "VPXOR" | "VPMOVMSKB" | "VPAND" | "VPTEST" | "VPBROADCASTB" | "JCXZW" | "FCMOVCC" | "FCMOVCS" | "FCMOVEQ" | "FCMOVHI" |
                "FCMOVLS" | "FCMOVNE" | "FCMOVNU" | "FCMOVUN" | "FCOMI" | "FCOMIP" | "FUCOMI" | "FUCOMIP" | "XACQUIRE" | "XRELEASE" | "XBEGIN" | "XEND" |
                "XABORT" | "XTEST" | "LAST"

FLAG          = "NOPROF" | "DUPOK" | "NOSPLIT" | "RODATA" | "NOPTR" | "WRAPPER" | "NEEDCTXT" | "TLSBSS" | "NOFRAME"

%%

"#import"             { return IMPORT; }
"TEXT"                { return TEXT; }

{FLAG}                { return FLAG; }
{PSEUDO_REG}          { return PSEUDO_REG; }
{INS}                 { return INSTRUCTION; }
{LINE_COMMENT}        { return LINE_COMMENT; }
{WS}                  { return TokenType.WHITE_SPACE; }
{WSNL}                { return TokenType.WHITE_SPACE; }
{LABEL}               { return LABEL; }
":"                   { return COLON; }
"("                   { return LPAREN; }
")"                   { return RPAREN; }
","                   { return COMMA; }
"|"                   { return BIT_OR; }

{NUM_HEX}             { return HEX; }
{NUM_INT}             { return INT; }

{IDENT}               { return IDENTIFIER; }

{STR} [^\"]* {STR}    { return STRING; }

///////////////////////////////////////////////////////////////////////////////////////////////////
// Catch All
///////////////////////////////////////////////////////////////////////////////////////////////////

[^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }