package main

func main() {
	var myStuff []string
	doSomething(myStuff...)
}

func doSomething(stuff ...string) {
	printStuff(stuff)
}

func printStuff(stuff []string) {
	println("%v\n", stuff)
}
