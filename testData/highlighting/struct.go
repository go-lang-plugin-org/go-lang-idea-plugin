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
}