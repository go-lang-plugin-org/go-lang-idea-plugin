package main

type <warning descr="Type 'byte' collides with builtin type">byte</warning> struct{}
type <warning descr="Type 'string' collides with builtin type">string</warning> interface{}

func <warning descr="Function 'uint' collides with builtin type"><warning descr="Unused function 'uint'">uint</warning></warning>() {}

func (u byte) <warning descr="Method 'uint' collides with builtin type">uint</warning>() {}

func main() {
	const <warning descr="Constant 'int' collides with builtin type"><warning descr="Unused constant 'int'">int</warning></warning> = 1
	<warning descr="Variable 'string' collides with builtin type">string</warning> := ""
	_ = string
}
