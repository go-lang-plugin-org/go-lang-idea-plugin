package main

import "fmt"
import "net/http"
import "time"

func main() {
	b := 1
	b, a := 11, 1
	fmt.Println(b, a)
	c := simple(10)
	fmt.Println(c)
	Foo()
	Foo2()
        fmt.Println(http.ErrMissingFile)
}

func simple(a int) int {
	a, b := 1, 2
	return a + b
}

func Foo() {
    <error descr="Unused variable 'err'">err</error> := 1
    err = 2
}

func Foo2() {
    <error descr="Unused variable 'err'">err</error> := 1
    err,x := 2,1
    fmt.Println(x)
}

func _(p interface{}) error {
	switch <error descr="Unused variable 'p'">p</error> := p.(type) {
		case error:
		return nil
	}
	return nil
}

type Image interface {
	At(x, y int)
}

type Image2 interface {
}


func _() {
	var p Image
	switch q := p.(type) {
	case Image:
		fmt.Println("draw.Image")
		switch p.(type) {
		case Image2:
			q.At(0,0)
		}
	case Image2:
		q.<error descr="Unresolved reference 'At'">At</error>(0,0)
	default:
		fmt.Println("default")
		q.At(0, 0)
	}
}


var (
	interval1 = time.Second
	interval2 = time.Second * 5
)

func TestIdeaTimeApi() {
	interval1.Nanoseconds()
	fmt.Printf("%T %T", interval1, interval2)
	fmt.Printf("%d %d", interval1.Nanoseconds(), interval2.Nanoseconds())
}

func _() {
	fmt.Println(interval2)
	fmt.Println(time.Second * 5)
	f(time.Second)
	TestIdeaTimeApi()
}

func f(d time.Duration) {
	fmt.Println((d * time.Second).Nanoseconds())
	fmt.Println(d * time.Second * time.Second)
}

func _() {
	<warning descr="Variable 'time' collides with imported package name">time</warning> := time.Now();
	fmt.Println(time) 
}
