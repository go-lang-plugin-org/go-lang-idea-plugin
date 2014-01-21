package main

import <error>""</error>

type aaa aaa

type Boom struct {
   err aaa
}

func (b *Boom) Run() {
   b.err
}

func foo() {
    i := 1
    for (i) {return 0}
    if (i) {return <error>j</error>}

    headers := 1
    for _, h := range headers {
      h++
    }
}

type int int
type float32 float32
type string string

type T struct {
	a int
}
func (tv  T) Mv(a int) int         { return 0 }  // value receiver
func (tp *T) Mp(f float32) float32 { return 1 }  // pointer receiver

var t T

func bar() {
    t.Mv(7)
    <error>T</error>.Mv(t, 7)
    (T).Mv(t, 7)
    f1 := <error>T</error>.Mv; f1(t, 7)
    f2 := (T).Mv; f2(t, 7)
}


func foo() {
    a := &A{}
    b := &B{b:"bbb"}
    e := &Empty{}

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
func BenchmarkName(b *AA) {
     b.N
}