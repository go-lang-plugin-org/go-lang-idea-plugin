// a line comment
TEXT foo(SB),NOSPLIT,$24-8

label:
	MOVB	(SI)(BX*1), CX
	XORL	$0xffff, BX	// convert EQ to NE
	ANDL	$0x0f0f >> 1, CX
	CLD
	CALL	runtime·args(SB)
	RET

DATA	bad_proc_msg<>+0x00(SB)/8, $"This pro"
DATA	bad_proc_msg<>+0x08(SB)/8, $"gram can"
