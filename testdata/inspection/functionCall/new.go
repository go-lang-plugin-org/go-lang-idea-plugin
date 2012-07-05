package main

type T struct {
    a int
}

func main() {
    /*begin*/new()/*end.Missing type argument to new*/
    /*begin*/new(5)/*end.5 is not a type*/
    /*begin*/new(int, 5)/*end.Too many arguments in call to new*/

    new(int)
    new(T)
    new(*T)

    new(struct{a int})
}
