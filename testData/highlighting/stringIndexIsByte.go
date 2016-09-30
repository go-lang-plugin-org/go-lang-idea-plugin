package main

import "fmt"

func main() {
	x := "text"
	if <error descr="Mismatched types: byte and string">x[0] == "t"</error> && x[1] == 't' {
		fmt.Println(x[0])
	}

	valid := x[1] == 't'
	valid = x[0:2] == "te"
	valid = x[:2] == "te"
	valid = x[2:] == "xt"
	valid = x[:] == "xt"

	fmt.Println(valid)

	invalid := <error descr="Mismatched types: byte and string">x[2] == "xt"</error>
	invalid = <error descr="Mismatched types: byte and string">"t" == x[0]</error>
	invalid = <error descr="Mismatched types: byte and string">x[3] == "x"</error>
	invalid = <error descr="Mismatched types: byte and string">x[3] != "x"</error>
	invalid = <error descr="Mismatched types: byte and string">x[3] >= "x"</error>
	invalid = <error descr="Mismatched types: byte and string">x[3] <= "x"</error>
	invalid = <error descr="Mismatched types: byte and string">x[3] > "x"</error>
	invalid = <error descr="Mismatched types: byte and string">x[3] < "x"</error>

	fmt.Println(invalid)

}