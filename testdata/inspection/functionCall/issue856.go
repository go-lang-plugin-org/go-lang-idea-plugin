package functionCall

func main() {
	stuff := []string{"1", "2", "3"}
	var myStuff []string
	myStuff = append(myStuff, stuff...)
	doSomething(myStuff...)
}

func doSomething(stuff ...string) {
	printStuff(stuff)
}

func printStuff(stuff []string) {
	println("%v\n", stuff)
}