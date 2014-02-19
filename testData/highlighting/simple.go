package main

import "<error></error>"

type aaa aaa

type Boom struct {
   err aaa
}

func (b *Boom) Run(a aaa) (r1 aaa, r2 aaa) {
   b.err + a + r1 + r2
}

func <warning>foo</warning>() {
    i := 1
    for (i) {return 0}
    if (i) {return <error>j</error>}

    headers := 1
    for _, h := range headers {
      h++
    }
}

type int int
type byte byte
type float32 float32
type string string

type T struct {
	a int
}
func (tv  T) Mv(a int) int         { return 0 }  // value receiver
func (tp *T) Mp(f float32) float32 { return 1 }  // pointer receiver

var t T

func <warning>bar</warning>() {
    t.Mv(7)
    T.<error>Mv</error>(t, 7) // todo: only simple type with ctors
    (T).Mv(t, 7)
    f1 := T.<error>Mv</error>; f1(t, 7) // todo: only simple type with ctors
    f2 := (T).Mv; f2(t, 7)
}


func <warning>foo</warning>() {
    a := &A{}
    b := &B{b:"bbb"}
    e := &Empty{}
    y := make(A, 10)
    z := new(A)

    y.hola()
    z.hola()

    a.hola()
    b.hola()
    e.hola()
    b.b = "jj"
}

type B struct {
    *A
    b string
}
type A struct {
    *Empty
    a int
}
type Empty struct {
}
func (this *Empty) hola() {
}

type AA struct {
    N int
}
func <warning>BenchmarkName</warning>(b *AA) {
     b.N
}

func make(o interface{}, args ...interface{}) {
}

func new(o interface{}) {
}

func <warning>concurrently</warning>(integers []int) []int {
  ch := make(chan int)
  <error>responses</error> := []int{}
  for _, i := range integers {
      go func(j int) {
          ch <- j * j
      }(<error>j</error>)
  }
  for _, i := range integers {
      go func(j int) {
          ch <- j * j
      }(i)
  }
  err := 1
  _, err = 1, 1
  return integers
}

func Println(o ...interface{})  {
}

func <warning>innerTypes</warning>() {
	type connError struct {
		cn  int
	}
	ch := make(chan connError)
	Println(ch.cn)
}

type Iface interface {
  Boo() int
}

const name1 int = 10

func <warning>goo</warning>(st interface {Foo()}, st1 Iface) {
    <error>name1</error>, <error>name1</error> = 1, 2
    Println(st.Foo() + st1.Boo())
    if _ := 1 {
      return
    }
}

func <warning>labelsCheck</warning>() { goto Label1; Label1: 1; goto <error>Label2</error>}

type compositeA struct { int }
type compositeB struct { byte }

func <warning>composite</warning> () {
	a0, b0 := composite1()
	Println(a0.int, b0.byte)
	a1, b1 := new(compositeA), new(compositeB)
	Println(a1.int, b1.byte)
	a2, b2 := composite2()
	Println(a2.int, b2.byte)
}

func composite1() (*compositeA, *compositeB) {
	return new(compositeA), new(compositeB)
}
func composite2() (a *compositeA, b *compositeB) {
	return new(compositeA), new(compositeB)
}

func <warning>do</warning>(o interface {test1() int}) {
	Println(o.test1())
}

func <warning>dial</warning>() (int) {
	 type connError struct { err int }
	ch := make(chan connError)
  select {
		case ce  := <-ch:
		return ce.err
	}
}

type Item struct {
	Key string
	Value []byte
}

func <warning>main2</warning>() {
	m := GetMulti()
	v := m["AA"].Value
	Println(v)
	Println(GetMulti()["AA"].Key)
}

func GetMulti() (map[string]*Item) {
	m := make(map[string]*Item)
	m["AA"] = &Item{}
	return m
}

type Response struct { ResponseWriter }
type ResponseWriter interface { Header() Header }
type Header int

func (h Header) Add(key, value string) { }

func (r Response) AddHeader(header string, value string) Response {
	rr := r.Header()
	rr.Add()
	r.Header().Add(header, value)
	return r
}