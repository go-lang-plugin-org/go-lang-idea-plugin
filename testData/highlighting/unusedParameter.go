package main

type (
	de interface {
		Demo(x, y int)
	}

	demoo func(p int) (size int)

	dem string
)

func (d dem) demo(hello string) (<warning descr="Unused named return parameter 'err'">err</warning> int) {
	println("hello")
	return nil
}

func demo(hello string) {
	func(hello string) {
		println(hello)
	}(hello)
}

func demo2(<warning descr="Unused parameter 'hello'">hello</warning> string) {
	func(hello string) {
		println(hello)
	}("hello")
}

func demo3(hello string) {
	func() {
		println(hello)
	}()
}

func _(bar func(baz int) (foo int)) {
	_ = bar
}

func _(fn func() (i int, e error)) {
	i, e :=  fn()
	print(i)
	print(e.Error())
}

func main() {
	demo("hello")
	demo2("hello")
	demo3("hello")
	a := dem("hello")
	a.demo("hello")

	*(*func(width int) struct{})(nil)
}
