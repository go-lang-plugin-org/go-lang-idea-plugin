package main

type str struct {}

func (a str) Demo() str {
	return str{}
}

func main() {
	a := str{}
	b := a.Demo().Demo()
}