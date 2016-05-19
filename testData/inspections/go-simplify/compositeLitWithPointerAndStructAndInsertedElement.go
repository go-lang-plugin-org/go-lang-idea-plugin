package main

var _ = []*[]*[]int{
	&[]*[]int{
		&[]int<caret>{0, 1, 2, 3},
	},
}

func main (){
}