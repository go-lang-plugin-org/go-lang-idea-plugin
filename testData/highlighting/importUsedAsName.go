package demo

import "fmt"
import iio "io"

func _() {
	fmt.Println("demo")
	demo := true
	 _, _ = iio.EOF, demo
}

func _() {
	<warning descr="Variable 'fmt' collides with imported package name">fmt</warning> := "demo"
	<warning descr="Variable 'iio' collides with imported package name">iio</warning> := 1
	_, _ = iio, fmt
}
