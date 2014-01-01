package main

import "fmt"

func printf(str string, args ...interface{}) (int, error) {
    _, err := fmt.Printf(str, args...)
    return len(args), err
}

func main() {
    count := 1
    closure := func(msg string) {
        printf("%d %s\n", count, msg)
        count++
    }
    closure("A Message")
    closure("Another Message")
}

func main2() {
    loops := 1
    // while loop:
    for loops > 0 {
        fmt.Printf("\nNumber of loops?\n")
        fmt.Scanf("%d", &loops)
        // for loop
        for i := 0 ; i < loops ; i++ {
            fmt.Printf("%d ", i)
        }
    }
    // Infinite loop
    for {
        // Explicitly terminated
        break
    }
}

const (
    Red              = (1<<iota)
    Green            = (1<<iota)
    Blue, ColorMask  = (1<<iota), (1<<(iota+1))-1
)

type Example struct {
    Val string
    count int
}

type integer int
func (i integer) log() {
    fmt.Printf("%d\n", i);
}

func (e *Example) Log() {
    e.count++
    fmt.Printf("%d %s\n", e.count, e.Val)
}

type cartesianPoint struct {
    x, y float64
}
type polarPoint struct {
    r, θ float64
}

func (p cartesianPoint) X() float64 {return p.x }
func (p cartesianPoint) Y() float64 {return p.y }
func (p polarPoint) X() float64 {
    return p.r*math.Cos(p. θ )
}
func (p polarPoint) Y() float64 {
    return p.r*math.Sin(p. θ )
}
func (self cartesianPoint) Print() {
    fmt.Printf("(%f, %f)\n", self.x, self.y)
}
func (self polarPoint) Print() {
    fmt.Printf("(%f, %f ◦ )\n", self.r, self. θ )
}
type Point interface {
    Printer
    X() float64
    Y() float64
}
type Printer interface {
    Print()
}