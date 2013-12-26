package main

func HandleFunc(pa string, handler func(int,*string)bool){

}

func main() {
	indexHandler := func(){}
	HandleFunc("/",(func(int,*string)bool)(indexHandler))
}

-----
package main

func HandleFunc(pa string, handler func(int, *string) bool) {

}

func main() {
	indexHandler := func() {}
	HandleFunc("/", (func(int, *string) bool)(indexHandler))
}
