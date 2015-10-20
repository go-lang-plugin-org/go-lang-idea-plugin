package main

import "fmt"
import "time"

var (
	<warning>fmt</warning>, <warning>e</warning> = fmt.Print(1) // fmt redeclared in this block
                                                                    // previous declaration at *.go:3
)

func main() { }

func _() {
	time := time.Now()
	fmt.Println(time.Hour())
}
