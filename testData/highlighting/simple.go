package main

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

type T struct {
	a int
}
func (tv  T) Mv(a int) int         { return 0 }  // value receiver
func (tp *T) Mp(f float32) float32 { return 1 }  // pointer receiver

var t T

func bar() {
    t.<error>Mv</error>(7)
    <error>T</error>.Mv(t, 7)
    (T).Mv(t, 7)
    f1 := <error>T</error>.Mv; f1(t, 7)
    f2 := (T).Mv; f2(t, 7)
}
