package main

type polar struct {
    radius  float64
    theta   float64
}

func foo(thing chan polar) {
    aPolarObject := <-thing
    aPolarObject.<caret>
}
/**---
radius
theta
