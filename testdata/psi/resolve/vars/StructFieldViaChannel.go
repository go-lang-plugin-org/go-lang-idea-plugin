package main

type polar struct {
    /*def*/radius  float64
    theta   float64
}

func foo(thing chan polar) {
    aPolarObject := <-thing
    aPolarObject./*ref*/radius
}
