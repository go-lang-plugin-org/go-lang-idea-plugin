package main

import "fmt"

func main() {
    fmt.Println(<warning descr="Final return type of 'test()' is a function not a function call">test()</warning>)
    fmt.Println(<warning descr="Final return type of 'test2()' is a function not a function call">test2()</warning>)
    test3()
    test4()
    test5()
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
	fmt.Println(<error descr="Unresolved reference 'x'">x</error>)
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
    <error descr="Unused variable 'xy'">xy</error> := 5
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