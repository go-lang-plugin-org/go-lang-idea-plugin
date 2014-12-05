package main

type Elems []*int

func (s Elems) Len() int {
	return len(s)
}

func main() {}