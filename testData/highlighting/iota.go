package main

const (
	FooConst = iota
	BarConst
)

var (
	FooVar = <error>iota</error>
)

func _() {
	const name = iota
	println(<error>iota</error>)
	iota := 123
	println(iota)
	println(name)
        println(FooConst)
        println(BarConst)
        println(FooVar)
}

func main() {

}