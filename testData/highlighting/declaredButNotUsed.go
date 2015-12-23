package main

func main() {
	<error descr="String literal evaluated but not used">"hello"</error>
	<error descr="Function literal evaluated but not used">func</error>(){}
	<error descr="Number literal evaluated but not used">123</error>
	<error descr="Rune literal evaluated but not used">'a'</error>
}
