package unresolved_symbols_iota

const N = iota

const (
	bit0, mask0 = 1 << iota, 1<<iota - 1  // bit0 == 1, mask0 == 0
	bit1, mask1                           // bit1 == 2, mask1 == 1
	_, _                                  // skips iota == 2
	bit3, mask3                           // bit3 == 8, mask3 == 7
)

var v = /*begin*/iota/*end.Unresolved symbol: 'iota'|CreateGlobalVariableFix*/

func foo(a, iota int) int {
    return a + 1
}

func main() {
    k := 5
    println(true, false, /*begin*/iota/*end.Unresolved symbol: 'iota'|CreateLocalVariableFix|CreateGlobalVariableFix|CreateFunctionFix|CreateClosureFunctionFix*/)
    println(bit0, bit1, bit3, mask0, mask1, mask3)
}
