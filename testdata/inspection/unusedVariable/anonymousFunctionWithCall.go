package brt

func main() {
	hypot := func(x, y float64) float64 {
		return x + y
	}

	println(hypot(3.00000, 4.00001))
}
