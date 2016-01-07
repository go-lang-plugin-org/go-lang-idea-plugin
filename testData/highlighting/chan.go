package main

import "fmt"

type Struct struct {
	String string
}

func GetChan() chan Struct {
	c := make(chan Struct, 1)
	c <- Struct{"foo"}
	close(c)
	return c
}

func main() {
	c := GetChan()
	for s := range c {
		println(s.String)
	}

}

type Neuron struct {
	ID int
}

type Signal struct {
	Value float64
	ID    string
}

func (s *Signal) IsNull() bool {
	if s.Value != 0 {
		return false
	}
	return true
}

type Dendrit chan *Signal

func (d Dendrit) Sinops(n *Neuron) {
	for {
		select {
		case signal := 	<-d:
			if signal.IsNull() { // not found method
				continue
			}
		// Value not found also
			fmt.Printf("NID=%d\t<- %g (IN)\n", n.ID, signal.Value)
		default:
			continue
		}
	}
}
