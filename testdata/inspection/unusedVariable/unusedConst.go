package main

// simple variable
const /*begin*/c/*end.Unused constant 'c'|RemoveVariableFix*/  = 3
const C = 3

// variable with parenthesis
const (
    /*begin*/c1/*end.Unused constant 'c1'|RemoveVariableFix*/ int = 2
    C2 = iota
)

func main() {
	const /*begin*/C2/*end.Unused constant 'C2'|RemoveVariableFix*/ = 4
}
