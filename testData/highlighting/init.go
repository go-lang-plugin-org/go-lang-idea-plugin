package main

func init() {
	println("Hello")
}

func main() {
	<error>init</error>()
	println(" world!")
	a{}.init()
}

func _() {
	var init = func() {
		println("HA")
	}
	init()
	println(" world!")
}

type a struct {

}

func (s a) init() {
	println("sa")
}