package main

func demo(a, b int) {
_ = a
_ = b
}

func demo2(a int, b string) {
_ = a
_ = b
}

func main(){
buf:=make([]byte,1+len(msg.name))
println(1*2/3)
println(1-2-3,4%5%6)
demo(1+2, 3-4)
demo2(5*6, "7%*")
}

-----
package main

func demo(a, b int) {
	_ = a
	_ = b
}

func demo2(a int, b string) {
	_ = a
	_ = b
}

func main() {
	buf := make([]byte, 1+len(msg.name))
	println(1 * 2 / 3)
	println(1-2-3, 4%5%6)
	demo(1+2, 3-4)
	demo2(5*6, "7%*")
}
