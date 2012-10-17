package main

func main() {
	println(/*ref*/abs(-10))
	println(/*ref*/abs(10))
	println(/*ref*/abs(0))
}


func /*def*/abs(x float64) float64 {
	if x < 0 {
		return -x
	}
	return x
}
