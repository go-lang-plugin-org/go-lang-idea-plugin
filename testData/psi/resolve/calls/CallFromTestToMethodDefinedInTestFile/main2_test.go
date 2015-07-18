package main

type T struct {

}

func f() {
    r := T()
    x := r./*ref*/TestMethod()
}
