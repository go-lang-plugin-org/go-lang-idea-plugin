package main

import "fmt"

func main() {
    fmt.Println(test())
    fmt.Println(test2())
    fmt.Println(test3())
    fmt.Println(test4())
    fmt.Println(test5())
}

func foo() int {
    return 1
}

func test() func() int {
    fff := foo
    return func() int {
        r := fff()
        return r
    }
}

func test2() func () int {
    f := foo
    return func() int {
        r := 1
        r = f()
        return r
    }
}

func test3() {
	if x := 10; x < 9 {
		fmt.Println(x)
	} else {
		fmt.Println("not x")
	}
	fmt.Println(<error>x</error>)
}


func test4() {
	x := 5
	if x := 10; x < 9 {
		fmt.Println(x, "exists here")
	} else {		
		fmt.Println("and here!", x)
	}
	fmt.Println(x)
}


func test5() {
    <error>xy</error> := 5
    if xy := 10; xy < 9 {
        fmt.Println(xy, "exists here")
    } else {
        fmt.Println("and here!", xy)
    }
}

func _() {
	fs := *(*func(width int) struct{})(nil)
	width := 1
	_, _ = fs, width
}

func _() {
	*(*func(width int) struct{})(nil)
	width := 1
	_ = width
}