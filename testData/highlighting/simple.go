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

