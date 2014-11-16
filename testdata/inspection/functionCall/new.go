package main

type T struct {
    a int
}

func main() {
    /*begin*/new()/*end.missing type argument to new*/
    new(/*begin*/5/*end.5 is not a type*/)
    new(/*begin*/3/*end.3 is not a type*/, 5)
    /*begin*/new(int, 5)/*end.too many arguments in call to new*/

    new(int)
    new(T)
    new(*T)

    new(struct{a int})
}
