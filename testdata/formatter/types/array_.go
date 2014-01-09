package main

type (
	a [10]int
	b [10][23]float
	a [10]struct{}
	a [a + b]struct {
	}
	c [10]struct {
		a int
	}
	d [20]interface{}
	d [20]interface {
		F() int
	}
)
