package main

type Rat struct {
	a, b int
}


type Ad struct {
	Price Rat
}

func (x *Rat) Cmp(*Rat) int {
	return 1
}

func main() {
	rat := &Rat{1, 1}
	i1 := Ad{}.Price
	i :=  &i1
	i.Cmp(rat)
}
