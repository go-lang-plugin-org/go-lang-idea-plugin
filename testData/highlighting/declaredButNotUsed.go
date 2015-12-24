package main

func _(c interface{}) {
	<error descr="Type assertion evaluated but not used">c.(chan int)</error>
	iAreaId := c.(chan int)
	<error descr="Type conversion evaluated but not used">chan int(iAreaId)</error>
}

func main() {
	<error descr="String literal evaluated but not used">"hello"</error>
	<error descr="Function literal evaluated but not used">func</error>(){}
	<error descr="Numeric value evaluated but not used">123</error>
	<error descr="Rune literal evaluated but not used">'a'</error>
	switch 1 {
		case 1: println("1")
	}
}
