package main

import . "fmt"
import "net/url"

type alias Formatter

type <warning descr="Type 'int' collides with builtin type">int</warning> int

type Car struct { wheelCount int }
func (car Car) numberOfWheels() int { return car.wheelCount }

type Ferrari struct { Car }

func <warning>Println</warning>(...interface{})  { // should be redeclared!
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

func <warning descr="Function 'new' collides with builtin function">new</warning>(Type) *Type

type T int
func (t T) name()  {
}

func foo() *T {
    return new(T)
}

func doubleFoo() (*T, *T) {
    return foo(), foo()
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
    Println("A Car has this many wheels:", c.numberOfWheels())

    a := AstonMartin{Car{4}}
    Println("An Aston Martin has this many wheels:", a.numberOfWheels())
    a.sayHiToBond()

    t := foo()
    t.name()
    t2 := t.createT()
    Println(t2)
    t3 := <error>doubleFoo()</error>
    Println(t3)
}

type inte struct {
    aa int
    <error>aa</error>, bbb int
    byte1 struct {
        aaa
        <error>aaa</error>
    }
}

type aaa interface {
    String() int
}

type inte2 struct {
    byte1 struct {
        int
        <error>int</error>
    }
    <error>byte1</error> struct {
        int
        <error>int</error>
    }
}

type aaa interface {
    Str()
    <error>Str</error>() interface {
          A()
          b()
          <error>A</error>()
    }
}

type A struct {
    b int
    c int
}

func <warning>NewA</warning>(b int) *A {
    return &A{
        b: b, // Reported error: "unknown field b", but that is not correct
        c: 1,
    }
}

func _() {
	tr := &url.Userinfo{
		<error descr="Unknown field 'username' in struct literal">username</error>:"Name",
	}
	_ = tr
}


type Mtx struct {
	state int32
	sema  uint32
}

func (m *Mtx) Lock() {

}

func (m *Mtx) Unlock() {

}

type Locker interface {
	Lock()
	Unlock()
}

type TMtx struct {
	M struct {
		  Mtx
	  }
}

func _() {
	t := TMtx{}
	t.M.Lock()
	t.M.Unlock()
}