package main

type Foo struct {
	number int
	name   string
}

func main() {
	f1 := Foo{

	}
	foo(func() {
		f2 := Foo{

	}
	})
}

-----
package main

type Foo struct {
	number int
	name   string
}

func main() {
	f1 := Foo{

	}
	foo(func() {
		f2 := Foo{

		}
	})
}
