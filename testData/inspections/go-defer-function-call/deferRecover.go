package main

func main() {
	<weak_warning descr="defer should not call recover() directly">defer recover<caret>()</weak_warning>
}