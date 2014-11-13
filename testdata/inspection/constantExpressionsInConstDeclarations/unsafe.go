package constantExpressionsInConstDeclarations

import (
	"unsafe"
)

const wordSize = int(unsafe.Sizeof(uintptr(0)))