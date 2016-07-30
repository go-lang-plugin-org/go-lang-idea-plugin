package main

import "fmt"
import "time"

var (
	<warning descr="Unused variable 'fmt'"><warning descr="Variable 'fmt' collides with imported package name">fmt</warning></warning>, _ = fmt.Print(1)  // fmt redeclared in this block
                                                                    // previous declaration at *.go:3
)

func main() { }

func _() {
	<warning descr="Variable 'time' collides with imported package name">time</warning> := time.Now()
	fmt.Println(time.Hour())
}
