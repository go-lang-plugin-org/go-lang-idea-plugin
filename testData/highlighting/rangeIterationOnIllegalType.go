package main

import "fmt"

type TestStruct struct {
	SomeId int
}

type DataSlice []*TestStruct

func _() {
	data := &DataSlice{}
	for _, element := range <error descr="Cannot range over data">data</error> {  // here we should have an error: cannot range over data (type *DataSlice)
		if element.SomeId > 20 {
			println("some text")
		}
	}
}

func _() {
	data := DataSlice{}
	for _, element := range <error descr="Cannot range over data">&data</error> {  // here we should have an error: cannot range over cannot range over &data (type *DataSlice)
		if element.SomeId > 20 {
			println("some text")
		}
	}
}

func main() {
	nums := []int{2, 3, 4}
	sum := 0

	for _, num := range nums {
		sum += num
	}
	fmt.Println("sum:", sum)

	for i, num := range nums {
		if num == 3 {
			fmt.Println("index:", i)
		}
	}

	kvs := map[string]string{"a": "apple", "b": "banana"}
	for k, v := range kvs {
		fmt.Printf("%s -> %s\n", k, v)
	}

	for i, c := range "go" {
		fmt.Println(i, c)
	}
}
