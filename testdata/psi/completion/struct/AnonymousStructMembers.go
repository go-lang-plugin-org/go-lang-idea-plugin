package main

import "P"

type T1 int
type T2 int

type S struct {
	T1        // field name is T1
	*T2       // field name is T2
	P.T3      // field name is T3
	*P.T4     // field name is T4
	x, y int  // field names are x and y
}

func main() {
	var x S
	x.<caret>
}
/**---
T1
T2
T3
T4
x
y
