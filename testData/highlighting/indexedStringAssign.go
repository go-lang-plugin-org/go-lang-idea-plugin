package main

func main() {
	var s string = "hello"
	<error descr="Cannot assign to 's[0]'">s[0]</error> = 0
	for <error descr="Cannot assign to 's[0]'">s[0]</error> = range "asdf"{
	}
}