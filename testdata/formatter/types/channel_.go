package main

type (
	a chan T         // can be used to send and receive values of type T
	b chan<- float64 // can only be used to send float64s
	c <-chan int     // can only be used to receive ints

	d chan<- chan int   // same as chan<- (chan int)
	e chan<- <-chan int // same as chan<- (<-chan int)
	f <-chan <-chan int // same as <-chan (<-chan int)
	g chan (<-chan int)
)
