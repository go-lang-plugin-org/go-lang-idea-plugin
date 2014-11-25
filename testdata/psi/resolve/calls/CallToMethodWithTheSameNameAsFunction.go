package main

type T int

func F() {

}

func (T) /*def*/F() {

}

func main() {
	T(1)./*ref*/F()
}
