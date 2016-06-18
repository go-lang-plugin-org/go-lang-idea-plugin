package main

type (
  T0 []string
  T1 []string
  T2 struct{ a, b int }
  T3 struct{ a, c int }
  T4 func(int, float64) *T0
  T5 func(x int, y float64) *[]string
)

func main() {
        type Point interface {
	}

	var p interface{} = nil
	var c chan int = nil
	var x func() = nil
	var y func()int = nil
	*Point(p)        // same as *(Point(p))
	*(Point(p))
	(*Point)(p)      // p is converted to *Point
	<-chan int(c)    // same as <-(chan int(c))
	(<-chan int)(c)  // c is converted to <-chan int
	(func())(x)      // x is converted to func()
	(func() int)(y)  // x is converted to func() int
	func() int(y)    // x is converted to func() int (unambiguous)
	//func()(x)        // function signature func() x
}