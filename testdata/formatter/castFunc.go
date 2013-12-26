package main

func HandleFunc(pa string, handler func(int,*uint)bool){

}

func main() {
	indexHandler := func(){}
	HandleFunc("/",(func(int,*uint)bool)(indexHandler))
}

-----
package main

func HandleFunc(pa string, handler func(int, *uint) bool) {

}

func main() {
	indexHandler := func() {}
	HandleFunc("/", (func(int, *uint) bool)(indexHandler))
}
