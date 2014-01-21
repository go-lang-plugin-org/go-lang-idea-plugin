package main

//import "fmt"

type int int

type Car struct { wheelCount int }
func (car Car) numberOfWheels() int { return car.wheelCount }

type Ferrari struct { Car }

func Println(o ...interface{})  {
//   fmt.Println(o...)
}

func (f Ferrari) sayHiToSchumacher() { Println("Hi Schumacher!") }

type AstonMartin struct { Car }

func (a AstonMartin) sayHiToBond() { Println("Hi Bond, James Bond!") }

type E struct {
    *E
}

func (e E) foo() {
}

type Type int

func new(Type) *Type

type T int
func (t T) name()  {
}

func foo() *T {
    return new(T)
}

func (t T) createT() *T {
    return new(T)
}

func main() {
    e := E{}
    e.foo()
    f := Ferrari{Car{4}}
    Println("A Ferrari has this many wheels:", f.numberOfWheels())
    f.sayHiToSchumacher()

    c := Car{4}
    Println("A Car has this many wheels:", c.wheelCount)
    Println("A Car has this many wheels:", c.numberOfWheels)
    Println("A Car has this many wheels:", c.numberOfWheels())

    a := AstonMartin{Car{4}}
    Println("An Aston Martin has this many wheels:", a.numberOfWheels())
    a.sayHiToBond()

    t := foo()
    t.name()
    t2 := t.createT()
}