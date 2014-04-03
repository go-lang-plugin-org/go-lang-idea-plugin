package main

type Foo struct {
	number int
	name   string
}

func main() {
	f1 := Foo{
number : 2,
		name : "test2",
	}
	foo(func() {
		f2 := Foo{
			number : 3,
		name : "test3",
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
		number : 2,
		name : "test2",
	}
	foo(func() {
		f2 := Foo{
			number : 3,
			name : "test3",
		}
	})
}
